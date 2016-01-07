package hsoines.oekoflex.impl;

import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.marketoperator.impl.EnergyOnlyMarketOperatorImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jhense on 03.12.2015.
 */
public class EnergyOnlyMarketOperatorTest {

    private EnergyOnlyMarketOperatorImpl energyOnlyMarketOperator;

    @Before
    public void setUp() throws Exception {
        energyOnlyMarketOperator = new EnergyOnlyMarketOperatorImpl("test");
    }

    @org.junit.Test
    public void testClearingWithPartialDemand() throws Exception {
        energyOnlyMarketOperator.addDemand(new Demand(9, 11, null));
        energyOnlyMarketOperator.addDemand(new Demand(7, 6, null));

        energyOnlyMarketOperator.addSupply(new Supply(5, 10, null));
        energyOnlyMarketOperator.addSupply(new Supply(6, 5, null));
        energyOnlyMarketOperator.addSupply(new Supply(8, 5, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(15, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(4f/6, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
    }

    @org.junit.Test
    public void testClearingWithPartialSupport() throws Exception {
        energyOnlyMarketOperator.addSupply(new Supply(9, 11, null));
        energyOnlyMarketOperator.addSupply(new Supply(7, 6, null));

        energyOnlyMarketOperator.addDemand(new Demand(5, 10, null));
        energyOnlyMarketOperator.addDemand(new Demand(6, 5, null));
        energyOnlyMarketOperator.addDemand(new Demand(8, 5, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(5, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(5f/6, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
    }


    @org.junit.Test
    public void stopsAtEqualQuantity(){
        energyOnlyMarketOperator.addDemand(new Demand(1000, 100, null));
        energyOnlyMarketOperator.addDemand(new Demand(900, 110, null));

        energyOnlyMarketOperator.addDemand(new Demand(800, 120, null));
        energyOnlyMarketOperator.addDemand(new Demand(700, 130, null));
        energyOnlyMarketOperator.addDemand(new Demand(600, 140, null));
        energyOnlyMarketOperator.addDemand(new Demand(500, 150, null));
        energyOnlyMarketOperator.addDemand(new Demand(400, 160, null));

        energyOnlyMarketOperator.addSupply(new Supply(550, 60, null));
        energyOnlyMarketOperator.addSupply(new Supply(650, 70, null));
        energyOnlyMarketOperator.addSupply(new Supply(750, 80, null));

        energyOnlyMarketOperator.addSupply(new Supply(850, 90, null));
        energyOnlyMarketOperator.addSupply(new Supply(950, 100, null));
        energyOnlyMarketOperator.addSupply(new Supply(1050, 110, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(210, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(0, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);
        assertEquals(775, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
    }

    @Test
    public void testAssignmentRateOnSequentialDemands() throws Exception {
        energyOnlyMarketOperator.addDemand(new Demand(10, 100, null));

        energyOnlyMarketOperator.addSupply(new Supply(7.5f, 100, null));
        energyOnlyMarketOperator.addSupply(new Supply(7.5f, 500, null));

        energyOnlyMarketOperator.addDemand(new Demand(9, 100, null));
        energyOnlyMarketOperator.addDemand(new Demand(8, 100, null));
        energyOnlyMarketOperator.addDemand(new Demand(7, 100, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(300, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.4000000059604645f, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);

    }

    @Test
    public void testAssignmentRateOnSequentialSupplies() throws Exception {
        energyOnlyMarketOperator.addSupply(new Supply(5, 100, null));

        energyOnlyMarketOperator.addDemand(new Demand(7.5f, 100, null));
        energyOnlyMarketOperator.addDemand(new Demand(7.5f, 500, null));

        energyOnlyMarketOperator.addSupply(new Supply(6, 100, null));
        energyOnlyMarketOperator.addSupply(new Supply(7, 100, null));
        energyOnlyMarketOperator.addSupply(new Supply(8, 100, null));
        energyOnlyMarketOperator.addSupply(new Supply(9, 100, null));

        energyOnlyMarketOperator.clearMarket();

        assertEquals(300, energyOnlyMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, energyOnlyMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.4f, energyOnlyMarketOperator.getLastAssignmentRate(), 0.00001);


    }
}