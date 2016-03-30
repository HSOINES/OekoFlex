package hsoines.oekoflex.builder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 30/03/16
 * Time: 17:45
 */
public class CSVParameterTest {

    private CSVParser parser;

    @Before
    public void setUp() throws Exception {
        final InputStream inputStream = CSVParameterTest.class.getResourceAsStream("test.csv");
        final CSVFormat csvFormat = CSVParameter.getCSVFormat();
        parser = csvFormat.parse(new InputStreamReader(inputStream));
    }

    @Test
    public void testCSV() throws Exception {
        final Iterator<CSVRecord> iterator = parser.iterator();
        while (iterator.hasNext()) {
            final CSVRecord next = iterator.next();
            final String param1 = next.get("param1");
            final String param2 = next.get("param2");
            assertEquals(10.22f, OekoFlexContextBuilder.defaultNumberFormat.parse(param1).floatValue(), 0.0001f);
            assertEquals(11.44f, OekoFlexContextBuilder.defaultNumberFormat.parse(param2).floatValue(), 0.0001f);
        }
    }
}