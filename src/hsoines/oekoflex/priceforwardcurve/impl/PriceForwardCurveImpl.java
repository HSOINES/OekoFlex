package hsoines.oekoflex.priceforwardcurve.impl;

import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jh
 * Date: 30/03/16
 * Time: 18:47
 */
public final class PriceForwardCurveImpl implements PriceForwardCurve {

    private final Map<Long, Float> priceOnTick;
    private final File priceForwardOutFile;

    public PriceForwardCurveImpl(final File priceForwardOutFile) {
        this.priceForwardOutFile = priceForwardOutFile;
        priceOnTick = new HashMap<>();
    }

    @Override
    public void readData() throws IOException, ParseException {
        priceOnTick.clear();
        final FileReader reader = new FileReader(priceForwardOutFile);
        final CSVFormat csvFormat = CSVParameter.getCSVFormat();
        final CSVParser csvParser = csvFormat.parse(reader);
        for (CSVRecord values : csvParser.getRecords()) {
            final long tick = Long.parseLong(values.get("tick"));
            final float price = OekoFlexContextBuilder.defaultNumberFormat.parse(values.get("price")).floatValue();
            priceOnTick.put(tick, price);
        }
        reader.close();
    }

    @Override
    public float getPriceSummation(final long currentTick, final int ticks) {
        float sum = 0;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            sum += priceOnTick.get(i);
        }
        return sum;
    }

    @Override
    public float getSpread(final long currentTick, final int ticks) {
        float min = getMinimum(currentTick, ticks);
        float max = getMaximum(currentTick, ticks);
        return max - min;
    }

    @Override
    public float getMinimum(final long currentTick, final int ticks) {
        float min = Float.MAX_VALUE;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            float v = priceOnTick.get(i);
            if (min > v) min = v;
        }
        return min;
    }

    @Override
    public float getMaximum(final long currentTick, final int ticks) {
        float max = -Float.MAX_VALUE;
        for (long i = currentTick; i < currentTick + ticks; i++) {
            float v = priceOnTick.get(i);
            if (max < v) max = v;
        }
        return max;
    }
}
