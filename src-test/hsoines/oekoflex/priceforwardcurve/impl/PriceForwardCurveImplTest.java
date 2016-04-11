package hsoines.oekoflex.priceforwardcurve.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

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
        final File priceForwardOutFile = new File("src-test/resources/price-forward.csv");
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

    @Test
    public void testSpread() throws Exception {
        float spread = priceForwardCurve.getSpread(0, 8);
        assertEquals(0, spread, 0.00001f);
        spread = priceForwardCurve.getSpread(12, 16);
        assertEquals(3, spread, 0.00001f);
        spread = priceForwardCurve.getSpread(16, 16);
        assertEquals(0, spread, 0.00001f);

        spread = priceForwardCurve.getSpread(0, 16);
        assertEquals(10, spread, 0.00001f);
        spread = priceForwardCurve.getSpread(8, 16);
        assertEquals(10, spread, 0.00001f);
        spread = priceForwardCurve.getSpread(0, 32);
        assertEquals(10, spread, 0.00001f);
    }

    @Test
    public void testTicksWithLowestPrices() throws Exception {
        List<Long> ticksWithLowestPrices = priceForwardCurve.getTicksWithLowestPrices(5, 10, 10);
        assertEquals(5, ticksWithLowestPrices.size());
        assertEquals(Long.valueOf(15l), ticksWithLowestPrices.get(0));
        assertEquals(Long.valueOf(14l), ticksWithLowestPrices.get(1));
        assertEquals(Long.valueOf(13l), ticksWithLowestPrices.get(2));
        assertEquals(Long.valueOf(12l), ticksWithLowestPrices.get(3));
        assertEquals(Long.valueOf(10l), ticksWithLowestPrices.get(4));
    }
}