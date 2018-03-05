package de.qaware.qav.graph.index;

import com.google.common.base.Stopwatch;
import de.qaware.qav.graph.api.DependencyGraph;
import de.qaware.qav.graph.api.Node;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Index to find nodes. Based on an in-memory Lucene index.
 * <p>
 * We use Lucene foremost for its convenient query language. It's used mainly (or only) in the Web UI to find nodes.
 *
 * @author QAware GmbH
 */
public class DependencyGraphIndex {

    private static final Logger LOGGER = getLogger(DependencyGraphIndex.class);
    private static final int MAX_RESULTS = 1000;

    private final DependencyGraph graph;
    private final Set<String> numericFields = new HashSet<>();

    private Directory directory;
    private Analyzer analyzer;

    /**
     * Constructor.
     *
     * @param graph         the {@link DependencyGraph} to index
     * @param numericFields the names of numeric fields.
     */
    public DependencyGraphIndex(DependencyGraph graph, String... numericFields) {
        this.graph = graph;
        Collections.addAll(this.numericFields, numericFields);

        initIndex();
    }

    /**
     * Setup up the Lucene index.
     *
     * Put all {@link Node}s of the {@link #graph} into the index.
     */
    private void initIndex() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        analyzer = new StandardAnalyzer();

        // Store the index in memory:
        directory = new RAMDirectory();
        // To store an index on disk, use the method FSDirectory#open() instead.

        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            indexAllNodes(indexWriter);

            indexWriter.close();
        } catch (IOException e) {
            LOGGER.error("Indexing failed: ", e);
        }

        LOGGER.debug("Index built, took {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    private void indexAllNodes(IndexWriter indexWriter) throws IOException {
        for (Node node : graph.getAllNodes()) {
            indexNode(indexWriter, node);
        }
    }

    private void indexNode(IndexWriter indexWriter, Node node) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : node.getProperties().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (numericFields.contains(key)) {
                doc.add(new LongPoint(key, Long.valueOf((String) value)));
            } else {
                doc.add(new Field(key, value.toString(), TextField.TYPE_STORED));
            }
        }

        indexWriter.addDocument(doc);
    }

    /**
     * Find nodes with the given query.
     *
     * @param queryString the Lucene query
     * @return the matching nodes
     */
    public Set<Node> findNodes(String queryString) {
        try {
            return doFindNodes(queryString);
        } catch (IOException e) {
            throw new IllegalArgumentException("Finding nodes failed for query: " + queryString, e);
        }
    }

    /**
     * Execute the query against the Lucene index.
     *
     * @param queryString the query String
     * @return the {@link Set} of {@link Node}s. May be empty, but never null.
     * @throws IOException if there is a low-level IO error in Lucene
     */
    private Set<Node> doFindNodes(String queryString) throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        Query query = parseQuery(queryString);

        ScoreDoc[] hits = indexSearcher.search(query, MAX_RESULTS, Sort.INDEXORDER).scoreDocs;

        Set<Node> result = new HashSet<>();

        for (ScoreDoc hit : hits) {
            Document hitDoc = indexSearcher.doc(hit.doc);
            String name = hitDoc.get("name");
            result.add(graph.getNode(name));
        }

        directoryReader.close();

        return result;
    }

    /**
     * Parse the Lucene query; take care of numeric fields.
     *
     * @param queryString the original query String
     * @return the parsed {@link Query}
     */
    private Query parseQuery(String queryString) {
        try {
            QueryParser parser = new CustomQueryParser(queryString, analyzer, numericFields);
            return parser.parse(queryString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Parsing of query failed: " + queryString, e);
        }
    }
}
