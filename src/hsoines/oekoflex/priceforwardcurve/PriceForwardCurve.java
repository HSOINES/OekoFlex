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
}
