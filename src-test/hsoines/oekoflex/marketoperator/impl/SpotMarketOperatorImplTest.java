package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * 
 */
public class SpotMarketOperatorImplTest {

    private SpotMarketOperatorImpl operator;
    private File priceForwardOutDir;
    private MarketOperatorListener listener;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        operator = new SpotMarketOperatorImpl("test", "run/summary-logs/test", true);
        listener = mock(MarketOperatorListener.class);

    }


    @Test
    public void testClearingWithRegularEnergyBids() throws Exception {
        operator.addDemand(new EnergyDemand(1, 2000, listener));
        operator.addDemand(new EnergyDemand(20, 2000, listener));
        operator.addSupply(new EnergySupply(10, 500, listener));
        operator.addSupply(new EnergySupply(11, 500, listener));
        operator.addSupply(new EnergySupply(12, 500, listener));
        operator.addSupply(new EnergySupply(13.000001f, 600, listener));
        operator.addSupply(new EnergySupply(14, 500, listener));

        operator.clearMarket();

        verify(listener, times(4)).notifyClearingDone(any(), Matchers.eq(Market.SPOT_MARKET), Matchers.<Bid>any(), Matchers.eq(13.000001f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(any(), Matchers.eq(Market.SPOT_MARKET), Matchers.<Bid>any(), Matchers.eq(13.000001f), Matchers.eq(.8333333f));
        verify(listener, times(1)).notifyClearingDone(any(), Matchers.eq(Market.SPOT_MARKET), Matchers.<Bid>any(), Matchers.eq(13.000001f), Matchers.eq(0.0f));
    }

    @Test
    public void testClearingWithFlexPowerPlants() throws Exception {
        operator.addSupply(new EnergySupply(-840f, 1.5f, listener));
        operator.addSupply(new EnergySupply(-836.29f, 47.5f, listener));
        operator.addSupply(new EnergySupply(-836.29f, 47.5f, listener));
        operator.addSupply(new EnergySupply(-836.27f, 48.5f, listener));
        operator.addSupply(new EnergySupply(-836.17f, 54f, listener));
        operator.addSupply(new EnergySupply(-836.17f, 54f, listener));
        operator.addSupply(new EnergySupply(-835.22f, 21.75f, listener));
        operator.addSupply(new EnergySupply(-835.2f, 51.25f, listener));
        operator.addSupply(new EnergySupply(-835.2f, 51.25f, listener));
        operator.addSupply(new EnergySupply(-835.2f, 50f, listener));
        operator.addSupply(new EnergySupply(-835.2f, 18.75f, listener));
        operator.addSupply(new EnergySupply(-835.2f, 18.75f, listener));
        operator.addSupply(new EnergySupply(-835.19f, 57f, listener));
        operator.addSupply(new EnergySupply(-835.19f, 22.25f, listener));
        operator.addSupply(new EnergySupply(-835.19f, 22.25f, listener));
        operator.addSupply(new EnergySupply(-835.15f, 8.25f, listener));
        operator.addSupply(new EnergySupply(-835.14f, 3.5f, listener));
        operator.addSupply(new EnergySupply(-834.28f, 57f, listener));
        operator.addSupply(new EnergySupply(-834.01f, 44.25f, listener));
        operator.addSupply(new EnergySupply(-833.09f, 49.5f, listener));
        operator.addSupply(new EnergySupply(-832.98f, 47f, listener));
        operator.addSupply(new EnergySupply(-832.17f, 34.5f, listener));
        operator.addSupply(new EnergySupply(-830.17f, 20.75f, listener));
        operator.addSupply(new EnergySupply(-829.09f, 2.75f, listener));
        operator.addSupply(new EnergySupply(-829.09f, 2.75f, listener));
        operator.addSupply(new EnergySupply(-829.09f, 2.75f, listener));
        operator.addSupply(new EnergySupply(-829.09f, 2.75f, listener));
        operator.addSupply(new EnergySupply(-829.09f, 2.75f, listener)); //Sum: 844,75, n=28
        operator.addSupply(new EnergySupply(3000f, 1f, listener));

        operator.addDemand(new EnergyDemand(20, 840, listener));
        operator.addDemand(new EnergyDemand(20, 4, listener));

        operator.clearMarket();

        assertEquals(844f, operator.getTotalClearedQuantity(), 0.0001f);
        verify(listener, times(1)).notifyClearingDone(any(Date.class), Matchers.eq(Market.SPOT_MARKET), Matchers.any(Bid.class), Matchers.eq(-829.09f), Matchers.eq(0.72727275f));
        verify(listener, times(29)).notifyClearingDone(any(Date.class), Matchers.eq(Market.SPOT_MARKET), Matchers.any(Bid.class), Matchers.eq(-829.09f), Matchers.eq(1.0f));
    }

    @Test
    public void testLog() throws Exception {
        MarketOperatorListener listener = new MyMarketOperatorListener();
        operator.addDemand(new EnergyDemand(10f, 100, listener));
        operator.addSupply(new EnergySupply(9f, 50, listener));
        operator.clearMarket();
        operator.stop();
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