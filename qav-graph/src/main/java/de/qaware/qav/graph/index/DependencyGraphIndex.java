package de.qaware.qav.graph.index;

import com.google.common.collect.Sets;
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
import java.util.Map;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Index to find nodes. Based on in-memory Lucene index.
 *
 * @author QAware GmbH
 */
public class DependencyGraphIndex {

    private static final Logger LOGGER = getLogger(DependencyGraphIndex.class);

    private final DependencyGraph graph;
    private final Set<String> numericFields;

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
        this.numericFields = Sets.newHashSet();
        Collections.addAll(this.numericFields, numericFields);

        long start = System.currentTimeMillis();
        initIndex();

        if (LOGGER.isDebugEnabled()) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.debug("Index built, took {} ms", duration);
        }
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

    private void initIndex() {
        analyzer = new StandardAnalyzer();

        // Store the index in memory:
        directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");

        try {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            indexAllNodes(indexWriter);

            indexWriter.close();
        } catch (IOException e) {
            LOGGER.error("Indexing failed: ", e);
        }
    }

    private void indexAllNodes(IndexWriter indexWriter) throws IOException {
        for (Node node : graph.getAllNodes()) {
            indexNode(indexWriter, node);
        }
    }

    private void indexNode(IndexWriter indexWriter, Node node) throws IOException {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : node.getProperties().entrySet()) {
            Object value = entry.getValue();

            String key = entry.getKey();
            if (numericFields.contains(key)) {
                doc.add(new LongPoint(key, Long.valueOf((String) value)));
            } else {
                doc.add(new Field(key, value.toString(), TextField.TYPE_STORED));
            }
        }

        indexWriter.addDocument(doc);
    }

    private Set<Node> doFindNodes(String queryString) throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        Query query;
        try {
            QueryParser parser = new CustomQueryParser(queryString, analyzer, numericFields);
            query = parser.parse(queryString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Parsing of query failed: " + queryString, e);
        }

        ScoreDoc[] hits = indexSearcher.search(query, 1000, Sort.INDEXORDER).scoreDocs;

        Set<Node> result = Sets.newHashSet();

        for (ScoreDoc hit : hits) {
            Document hitDoc = indexSearcher.doc(hit.doc);
            String name = hitDoc.get("name");
            result.add(graph.getNode(name));
        }

        directoryReader.close();

        return result;
    }
}
