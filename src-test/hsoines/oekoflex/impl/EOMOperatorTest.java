package hsoines.oekoflex.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jhense on 03.12.2015.
 */
public class EOMOperatorTest {

    private EOMOperatorImpl energyOnlyMarketOperator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        energyOnlyMarketOperator = new EOMOperatorImpl("test", "run/summary-logs/test");
    }

    @org.junit.Test
    public void testClearingWithPartialDemand() throws Exception {
        energyOnlyMarketOperator.addDemand(new EnergyDemand(9, 11, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(7, 6, null));

        energyOnlyMarketOperator.addSupply(new EnergySupply(5, 10, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(6, 5, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(8, 5, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(15, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(4f/6, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
    }

    @org.junit.Test
    public void testClearingWithPartialSupport() throws Exception {
        energyOnlyMarketOperator.addSupply(new EnergySupply(9, 11, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(7, 6, null));

        energyOnlyMarketOperator.addDemand(new EnergyDemand(5, 10, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(6, 5, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(8, 5, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(5, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(5f/6, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
    }


    @org.junit.Test
    public void stopsAtEqualQuantity(){
        energyOnlyMarketOperator.addDemand(new EnergyDemand(1000, 100, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(900, 110, null));

        energyOnlyMarketOperator.addDemand(new EnergyDemand(800, 120, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(700, 130, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(600, 140, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(500, 150, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(400, 160, null));

        energyOnlyMarketOperator.addSupply(new EnergySupply(550, 60, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(650, 70, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(750, 80, null));

        energyOnlyMarketOperator.addSupply(new EnergySupply(850, 90, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(950, 100, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(1050, 110, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(210, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(0, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
        assertEquals(775, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
    }

    @Test
    public void testAssignmentRateOnSequentialDemands() throws Exception {
        energyOnlyMarketOperator.addDemand(new EnergyDemand(10, 100, null));

        energyOnlyMarketOperator.addSupply(new EnergySupply(7.5f, 100, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(7.5f, 500, null));

        energyOnlyMarketOperator.addDemand(new EnergyDemand(9, 100, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(8, 100, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(7, 100, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(300, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.4000000059604645f, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);

    }

    @Test
    public void testAssignmentRateOnSequentialSupplies() throws Exception {
        energyOnlyMarketOperator.addSupply(new EnergySupply(5, 100, null));

        energyOnlyMarketOperator.addDemand(new EnergyDemand(7.5f, 100, null));
        energyOnlyMarketOperator.addDemand(new EnergyDemand(7.5f, 500, null));

        energyOnlyMarketOperator.addSupply(new EnergySupply(6, 100, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(7, 100, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(8, 100, null));
        energyOnlyMarketOperator.addSupply(new EnergySupply(9, 100, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(300, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.4f, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);


    }
}