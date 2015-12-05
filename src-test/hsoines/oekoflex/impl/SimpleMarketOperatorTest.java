package hsoines.oekoflex.impl;

import hsoines.oekoflex.supply.Supply;
import hsoines.oekoflex.demand.Demand;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by jhense on 03.12.2015.
 */
public class SimpleMarketOperatorTest {

    private SimpleMarketOperator simpleMarketOperator;

    @Before
    public void setUp() throws Exception {
        simpleMarketOperator = new SimpleMarketOperator();
    }

    @org.junit.Test
    public void testClearingWithPartialDemand() throws Exception {
        simpleMarketOperator.addDemand(new Demand(9, 11, null));
        simpleMarketOperator.addDemand(new Demand(7, 6, null));

        simpleMarketOperator.addSupply(new Supply(5, 10, null));
        simpleMarketOperator.addSupply(new Supply(6, 5, null));
        simpleMarketOperator.addSupply(new Supply(8, 5, null));

        simpleMarketOperator.clearMarket();

        assertEquals(15, simpleMarketOperator.getTotalClearedQuantity());
        assertEquals(7, simpleMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(4f/6, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
    }

    @org.junit.Test
    public void testClearingWithPartialSupport() throws Exception {
        simpleMarketOperator.addSupply(new Supply(9, 11, null));
        simpleMarketOperator.addSupply(new Supply(7, 6, null));

        simpleMarketOperator.addDemand(new Demand(5, 10, null));
        simpleMarketOperator.addDemand(new Demand(6, 5, null));
        simpleMarketOperator.addDemand(new Demand(8, 5, null));

        simpleMarketOperator.clearMarket();

        assertEquals(5, simpleMarketOperator.getTotalClearedQuantity());
        assertEquals(7, simpleMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(5f/6, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
    }


    @org.junit.Test
    public void stopsAtEqualQuantity(){
        simpleMarketOperator.addDemand(new Demand(1000, 100, null));
        simpleMarketOperator.addDemand(new Demand(900, 110, null));

        simpleMarketOperator.addDemand(new Demand(800, 120, null));
        simpleMarketOperator.addDemand(new Demand(700, 130, null));
        simpleMarketOperator.addDemand(new Demand(600, 140, null));
        simpleMarketOperator.addDemand(new Demand(500, 150, null));
        simpleMarketOperator.addDemand(new Demand(400, 160, null));

        simpleMarketOperator.addSupply(new Supply(550, 60, null));
        simpleMarketOperator.addSupply(new Supply(650, 70, null));
        simpleMarketOperator.addSupply(new Supply(750, 80, null));

        simpleMarketOperator.addSupply(new Supply(850, 90, null));
        simpleMarketOperator.addSupply(new Supply(950, 100, null));
        simpleMarketOperator.addSupply(new Supply(1050, 110, null));

        simpleMarketOperator.clearMarket();

        assertEquals(210, simpleMarketOperator.getTotalClearedQuantity());
        assertEquals(0, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
        assertEquals(750, simpleMarketOperator.getLastClearedPrice(), 0.00001);    // todo noch unklar
    }

    @Test
    public void testAssignmentRateOnSequentialDemands() throws Exception {
        simpleMarketOperator.addDemand(new Demand(10, 100, null));

        simpleMarketOperator.addSupply(new Supply(7.5f, 100, null));
        simpleMarketOperator.addSupply(new Supply(7.5f, 500, null));

        simpleMarketOperator.addDemand(new Demand(9, 100, null));
        simpleMarketOperator.addDemand(new Demand(8, 100, null));
        simpleMarketOperator.addDemand(new Demand(7, 100, null));

        simpleMarketOperator.clearMarket();

        assertEquals(300, simpleMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, simpleMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.4000000059604645f, simpleMarketOperator.getLastAssignmentRate(), 0.00001);

    }

    @Test
    public void testAssignmentRateOnSequentialSupplies() throws Exception {
        simpleMarketOperator.addSupply(new Supply(5, 100, null));

        simpleMarketOperator.addDemand(new Demand(7.5f, 100, null));
        simpleMarketOperator.addDemand(new Demand(7.5f, 500, null));

        simpleMarketOperator.addSupply(new Supply(6, 100, null));
        simpleMarketOperator.addSupply(new Supply(7, 100, null));
        simpleMarketOperator.addSupply(new Supply(8, 100, null));
        simpleMarketOperator.addSupply(new Supply(9, 100, null));

        simpleMarketOperator.clearMarket();

        assertEquals(300, simpleMarketOperator.getTotalClearedQuantity());
        assertEquals(7.5f, simpleMarketOperator.getLastClearedPrice(), 0.00001);
        assertEquals(.6666f, simpleMarketOperator.getLastAssignmentRate(), 0.00001);


    }
}