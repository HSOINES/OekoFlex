package hsoines.oekoflex.impl;

import hsoines.oekoflex.ask.Support;
import hsoines.oekoflex.bid.Demand;
import org.junit.Before;

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

        simpleMarketOperator.addSupport(new Support(5, 10, null));
        simpleMarketOperator.addSupport(new Support(6, 5, null));
        simpleMarketOperator.addSupport(new Support(8, 5, null));

        simpleMarketOperator.clearMarket();

        assertEquals(15, simpleMarketOperator.getTotalSupportQuantity());
        assertEquals(7, simpleMarketOperator.getClearedPrice(), 0.00001);
        assertEquals(4f/6, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
    }

    @org.junit.Test
    public void testClearingWithPartialSupport() throws Exception {
        simpleMarketOperator.addSupport(new Support(9, 11, null));
        simpleMarketOperator.addSupport(new Support(7, 6, null));

        simpleMarketOperator.addDemand(new Demand(5, 10, null));
        simpleMarketOperator.addDemand(new Demand(6, 5, null));
        simpleMarketOperator.addDemand(new Demand(8, 5, null));

        simpleMarketOperator.clearMarket();

        assertEquals(5, simpleMarketOperator.getTotalSupportQuantity());
        assertEquals(7, simpleMarketOperator.getClearedPrice(), 0.00001);
        assertEquals(5f/6, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
    }


    @org.junit.Test
    public void moreBids(){
        simpleMarketOperator.addDemand(new Demand(1000, 100, null));
        simpleMarketOperator.addDemand(new Demand(900, 110, null));
        simpleMarketOperator.addDemand(new Demand(800, 120, null));
        simpleMarketOperator.addDemand(new Demand(700, 130, null));
        simpleMarketOperator.addDemand(new Demand(600, 140, null));
        simpleMarketOperator.addDemand(new Demand(500, 150, null));
        simpleMarketOperator.addDemand(new Demand(400, 160, null));

        simpleMarketOperator.addSupport(new Support(550, 60, null));
        simpleMarketOperator.addSupport(new Support(650, 70, null));
        simpleMarketOperator.addSupport(new Support(750, 80, null));
        simpleMarketOperator.addSupport(new Support(850, 90, null));
        simpleMarketOperator.addSupport(new Support(950, 100, null));
        simpleMarketOperator.addSupport(new Support(1050, 110, null));

        simpleMarketOperator.clearMarket();

        assertEquals(210, simpleMarketOperator.getTotalSupportQuantity());
        assertEquals(1, simpleMarketOperator.getLastAssignmentRate(), 0.00001);
        assertEquals(750, simpleMarketOperator.getClearedPrice(), 0.00001);    // noch unklar
    }
}