package hsoines.oekoflex.priceforwardcurve;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * User: jh
 * Date: 30/03/16
 * Time: 18:45
 */
public interface PriceForwardCurve {
    void readData() throws IOException, ParseException;

    float getPriceSummation(long currentTick, int ticks);

    float getSpread(long currentTick, int ticks);

    float getMinimum(long currentTick, int ticks);

    float getMaximum(long currentTick, int ticks);

    float getNegativePriceSummation(long currentTick, int ticks);

    List<Long> getTicksWithLowestPrices(int nTicks, long fromTick, int intervalTicks);

    float getPriceOnTick(long tick);

    List<Long> getTicksWithHighestPrices(int nTicks, long fromTick, int intervalTicks);
}
