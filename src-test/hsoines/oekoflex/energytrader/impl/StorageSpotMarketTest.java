package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 18:23
 */
public class StorageSpotMarketTest {

    public static final float SOC_MIN = 0.2f;
    public static final float SOC_MAX = .9f;
    private Storage storage;
    private SpotMarketOperatorImpl operator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        final File priceForwardOutFile = new File("src-test/resources/price-forward.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        storage = new Storage("test", "description", 1f, 0.1f, 1000, SOC_MAX, SOC_MIN, 100, 100, priceForwardCurve);
        operator = new SpotMarketOperatorImpl("test_operator", "run/summary-logs/test", true);
        storage.setSpotMarketOperator(operator);
    }

    @Test
    public void testBatteryEmpty() throws Exception {
        storage.makeBidEOM();
        operator.addSupply(new EnergySupply(0.7f, 500, null));
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addDemand(new EnergyDemand(0.8f, 1000, null));
        operator.clearMarket();

        float load = storage.getSoc();
        assertEquals(1000, load, 0.0001f);
    }

    @Test
    public void testBatteryEmpty2() throws Exception {
        storage.makeBidEOM();
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addSupply(new EnergySupply(1f, 300, null));

        operator.clearMarket();
        float load = storage.getSoc();
        assertEquals(500, load, 0.0001f);
    }

    @Test
    public void test2Cycles() throws Exception {
        storage.makeBidEOM();
        operator.addSupply(new EnergySupply(0.8f, 400, null));
        operator.clearMarket();
        assertEquals(400, storage.getSoc());
        TimeUtil.nextTick();
        storage.makeBidEOM();
        operator.addDemand(new EnergyDemand(1.2f, 150, null));
        operator.clearMarket();

        assertEquals(250, storage.getSoc());
    }
}