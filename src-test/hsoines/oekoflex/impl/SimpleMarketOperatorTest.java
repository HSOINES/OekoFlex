package hsoines.oekoflex.impl;

import hsoines.oekoflex.ask.Support;
import hsoines.oekoflex.bid.Demand;

import static org.junit.Assert.*;

/**
 * Created by jhense on 03.12.2015.
 */
public class SimpleMarketOperatorTest {

    @org.junit.Test
    public void testClearingWithPartialDemand() throws Exception {
        SimpleMarketOperator simpleMarketOperator = new SimpleMarketOperator();
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
        SimpleMarketOperator simpleMarketOperator = new SimpleMarketOperator();
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
}