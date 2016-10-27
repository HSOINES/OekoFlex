package hsoines.oekoflex.builder;

import org.apache.commons.csv.CSVFormat;

/**
 * 
 */
public final class CSVParameter {
    public static CSVFormat getCSVFormat() {
        return CSVFormat.DEFAULT.withHeader().withDelimiter(';');
    }
}
