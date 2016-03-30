package hsoines.oekoflex.priceforwardcurve;

import java.io.IOException;
import java.text.ParseException;

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
}
