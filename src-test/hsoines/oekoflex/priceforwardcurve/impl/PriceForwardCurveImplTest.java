package hsoines.oekoflex.priceforwardcurve.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * 
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
        List<Long> ticksWithLowestPrices = priceForwardCurve.getTicksWithLowestPrices(6, 10, 10);
        assertEquals(6, ticksWithLowestPrices.size());
        assertEquals(Long.valueOf(15l), ticksWithLowestPrices.get(0));
        assertEquals(Long.valueOf(14l), ticksWithLowestPrices.get(1));
        assertEquals(Long.valueOf(13l), ticksWithLowestPrices.get(2));
        assertEquals(Long.valueOf(12l), ticksWithLowestPrices.get(3));
        assertEquals(Long.valueOf(10l), ticksWithLowestPrices.get(4));
        assertEquals(Long.valueOf(19l), ticksWithLowestPrices.get(5));
    }
    @Test
    public void testTicksWithHighestPrices() throws Exception {
        List<Long> ticksWithHighestPrices = priceForwardCurve.getTicksWithHighestPrices(6, 10, 10);
        assertEquals(6, ticksWithHighestPrices.size());
        assertEquals(Long.valueOf(11l), ticksWithHighestPrices.get(0));
        assertEquals(Long.valueOf(16l), ticksWithHighestPrices.get(1));
        assertEquals(Long.valueOf(17l), ticksWithHighestPrices.get(2));
        assertEquals(Long.valueOf(18l), ticksWithHighestPrices.get(3));
        assertEquals(Long.valueOf(19l), ticksWithHighestPrices.get(4));
        assertEquals(Long.valueOf(10l), ticksWithHighestPrices.get(5));
    }

    @Test
    public void testTicksWithHighestPrices2() throws Exception {
        List<Long> ticksWithHighestPrices = priceForwardCurve.getTicksWithHighestPrices(11, 10, 15);
        assertEquals(11, ticksWithHighestPrices.size());
        assertEquals(Long.valueOf(11l), ticksWithHighestPrices.get(0));
        assertEquals(Long.valueOf(16l), ticksWithHighestPrices.get(1));
        assertEquals(Long.valueOf(17l), ticksWithHighestPrices.get(2));
        assertEquals(Long.valueOf(18l), ticksWithHighestPrices.get(3));
        assertEquals(Long.valueOf(19l), ticksWithHighestPrices.get(4));
        assertEquals(Long.valueOf(20l), ticksWithHighestPrices.get(5));
        assertEquals(Long.valueOf(21l), ticksWithHighestPrices.get(6));
        assertEquals(Long.valueOf(22l), ticksWithHighestPrices.get(7));
        assertEquals(Long.valueOf(23l), ticksWithHighestPrices.get(8));
        assertEquals(Long.valueOf(24l), ticksWithHighestPrices.get(9));
        assertEquals(Long.valueOf(10l), ticksWithHighestPrices.get(10));
    }
}