package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 13:38
 */
public class SpotMarketOperatorImplTest {

    private SpotMarketOperatorImpl operator;
    private File priceForwardOutDir;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        priceForwardOutDir = new File("run/price-forward/test");
        operator = new SpotMarketOperatorImpl("test", "run/summary-logs/test", true, priceForwardOutDir);
    }

    @Test
    public void testLog() throws Exception {
        MarketOperatorListener listener = new MyMarketOperatorListener();
        operator.addDemand(new EnergyDemand(10f, 100, listener));
        operator.addSupply(new EnergySupply(9f, 50, listener));
        operator.clearMarket();
        operator.stop();
        //must write log in test
        assertTrue(priceForwardOutDir.exists());
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