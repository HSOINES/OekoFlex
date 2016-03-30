package hsoines.oekoflex.builder;

import org.apache.commons.csv.CSVFormat;

/**
 * User: jh
 * Date: 17/01/16
 * Time: 23:07
 */
public final class CSVParameter {
    public static CSVFormat getCSVFormat() {
        return CSVFormat.DEFAULT.withHeader().withDelimiter(';');
    }
}
