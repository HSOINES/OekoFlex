package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 13:38
 */
public class EOMOperatorImplTest {

    private EOMOperatorImpl operator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        operator = new EOMOperatorImpl("test", "run/summary-logs/test");
    }

    @Test
    public void testName() throws Exception {
        MarketOperatorListener listener = new MyMarketOperatorListener();
        operator.addDemand(new Demand(10f, 100, listener));
        operator.addSupply(new Supply(9f, 100, listener));
        operator.clearMarket();
        //must write log in test
    }

    private static class MyMarketOperatorListener implements MarketOperatorListener {
        @Override
        public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

        }

        @Override
        public String getName() {
            return "testlistener";
        }
    }
}