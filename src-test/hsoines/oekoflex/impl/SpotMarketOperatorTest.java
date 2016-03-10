package hsoines.oekoflex.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jhense on 03.12.2015.
 */
public class SpotMarketOperatorTest {

    private SpotMarketOperatorImpl spotMarketOperator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        spotMarketOperator = new SpotMarketOperatorImpl("test", "run/summary-logs/test", true, null);
    }

    @org.junit.Test
    public void testClearingWithPartialDemand() throws Exception {
        spotMarketOperator.addDemand(new EnergyDemand(9, 11, null));
        spotMarketOperator.addDemand(new EnergyDemand(7, 6, null));

        spotMarketOperator.addSupply(new EnergySupply(5, 10, null));
        spotMarketOperator.addSupply(new EnergySupply(6, 5, null));
        spotMarketOperator.addSupply(new EnergySupply(8, 5, null));

        spotMarketOperator.clearMarket();

        assertEquals(15, spotMarketOperator.getTotalClearedQuantity());
        assertEquals(7, spotMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(4f / 6, spotMarketOperator.getLastAssignmentRate(), 0.00001);
    }

    @org.junit.Test
    public void testClearingWithPartialSupport() throws Exception {
        spotMarketOperator.addSupply(new EnergySupply(9, 11, null));
        spotMarketOperator.addSupply(new EnergySupply(7, 6, null));

        spotMarketOperator.addDemand(new EnergyDemand(5, 10, null));
        spotMarketOperator.addDemand(new EnergyDemand(6, 5, null));
        spotMarketOperator.addDemand(new EnergyDemand(8, 5, null));

        spotMarketOperator.clearMarket();

        assertEquals(5, spotMarketOperator.getTotalClearedQuantity());
        assertEquals(7, spotMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(5f / 6, spotMarketOperator.getLastAssignmentRate(), 0.00001);
    }


    @org.junit.Test
    public void stopsAtEqualQuantity(){
        spotMarketOperator.addDemand(new EnergyDemand(1000, 100, null));
        spotMarketOperator.addDemand(new EnergyDemand(900, 110, null));

        spotMarketOperator.addDemand(new EnergyDemand(800, 120, null));
        spotMarketOperator.addDemand(new EnergyDemand(700, 130, null));
        spotMarketOperator.addDemand(new EnergyDemand(600, 140, null));
        spotMarketOperator.addDemand(new EnergyDemand(500, 150, null));
        spotMarketOperator.addDemand(new EnergyDemand(400, 160, null));

        spotMarketOperator.addSupply(new EnergySupply(550, 60, null));
        spotMarketOperator.addSupply(new EnergySupply(650, 70, null));
        spotMarketOperator.addSupply(new EnergySupply(750, 80, null));

        spotMarketOperator.addSupply(new EnergySupply(850, 90, null));
        spotMarketOperator.addSupply(new EnergySupply(950, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(1050, 110, null));

        spotMarketOperator.clearMarket();

        assertEquals(210, spotMarketOperator.getTotalClearedQuantity());
        assertEquals(0, spotMarketOperator.getLastAssignmentRate(), 0.00001);
        assertEquals(775, spotMarketOperator.getLastClearedPrice(), 0.00001);
    }

    @Test
    public void testAssignmentRateOnSequentialDemands() throws Exception {
        spotMarketOperator.addDemand(new EnergyDemand(10, 100, null));

        spotMarketOperator.addSupply(new EnergySupply(7.5f, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(7.5f, 500, null));

        spotMarketOperator.addDemand(new EnergyDemand(9, 100, null));
        spotMarketOperator.addDemand(new EnergyDemand(8, 100, null));
        spotMarketOperator.addDemand(new EnergyDemand(7, 100, null));

        spotMarketOperator.clearMarket();

        assertEquals(300, spotMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, spotMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(0.6f, spotMarketOperator.getLastAssignmentRate(), 0.00001);

    }

    @Test
    public void testAssignmentRateOnSequentialSupplies() throws Exception {

        spotMarketOperator.addDemand(new EnergyDemand(7.5f, 100, null));
        spotMarketOperator.addDemand(new EnergyDemand(7.5f, 500, null));

        spotMarketOperator.addSupply(new EnergySupply(5, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(6, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(7, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(8, 100, null));
        spotMarketOperator.addSupply(new EnergySupply(9, 100, null));

        spotMarketOperator.clearMarket();

        assertEquals(300, spotMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, spotMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.6f, spotMarketOperator.getLastAssignmentRate(), 0.00001);


    }
}