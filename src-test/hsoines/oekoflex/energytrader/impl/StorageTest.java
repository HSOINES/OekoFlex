package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 18:23
 */
public class StorageTest {

    private Storage storage;
    private EOMOperatorImpl operator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        storage = new Storage("test", 500, 10, 1f, 0.1f, 1000, 1, 0, 100, 100);
        operator = new EOMOperatorImpl("test_operator", "run/summary-logs/test");
        storage.setEOMOperator(operator);
    }

    @Test
    public void testBatteryEmpty() throws Exception {
        storage.makeBidEOM();
        operator.addSupply(new EnergySupply(0.7f, 500, null));
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addDemand(new EnergyDemand(0.8f, 1000, null));
        operator.clearMarket();

        int load = storage.getSoc();
        assertEquals(1000, load);
    }

    @Test
    public void testBatteryEmpty2() throws Exception {
        storage.makeBidEOM();
        operator.addSupply(new EnergySupply(0.8f, 500, null));
        operator.addSupply(new EnergySupply(1f, 300, null));

        operator.clearMarket();
        int load = storage.getSoc();
        assertEquals(500, load);
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