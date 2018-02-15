package de.qaware.qav.graph.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import java.util.HashSet;
import java.util.Set;

/**
 * Custom query parser to make Lucene do correct range queries.
 *
 * @author QAware GmbH
 */
public class CustomQueryParser extends QueryParser {

    private final Set<String> numericFields;

    /**
     * Constructor.
     *
     * @param f             the default field for query terms
     * @param a             used to find terms in the query text
     * @param numericFields the names of numeric fields, i.e. the fields where numeric ranges will be applied
     */
    public CustomQueryParser(String f, Analyzer a, Set<String> numericFields) {
        super(f, a);
        this.numericFields = new HashSet<>(numericFields);
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
        if (numericFields.contains(field)) {
            return LongPoint.newExactQuery(field, Long.valueOf(queryText));
        } else {
            return super.getFieldQuery(field, queryText, quoted);
        }
    }

    @Override
    protected Query newRangeQuery(String field, String part1, String part2, boolean startInclusive,
                                  boolean endInclusive) {

        if (numericFields.contains(field)) {
            return LongPoint.newRangeQuery(field, Long.valueOf(part1), Long.valueOf(part2));
        } else {
            return super.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
        }
    }
}