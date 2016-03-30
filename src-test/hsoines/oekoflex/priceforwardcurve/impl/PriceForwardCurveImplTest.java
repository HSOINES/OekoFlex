package hsoines.oekoflex.priceforwardcurve.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 30/03/16
 * Time: 19:19
 */
public class PriceForwardCurveImplTest {

    private PriceForwardCurveImpl priceForwardCurve;

    @Before
    public void setUp() throws Exception {
        final File priceForwardOutFile = new File("run-config/test/price-forward/price-forward.csv");
        priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
    }

    @Test
    public void testSum() throws Exception {
        float priceSummation = priceForwardCurve.getPriceSummation(0, 16);
        assertEquals(16f, priceSummation, 0.0000f);
        priceSummation = priceForwardCurve.getPriceSummation(8, 16);
        assertEquals(24f, priceSummation, 0.0000f);
        priceSummation = priceForwardCurve.getPriceSummation(16, 16);
        assertEquals(32f, priceSummation, 0.0000f);
    }
}