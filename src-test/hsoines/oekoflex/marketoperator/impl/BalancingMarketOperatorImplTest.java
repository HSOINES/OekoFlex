package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * 
 */
public class BalancingMarketOperatorImplTest {

    private BalancingMarketOperatorImpl operator;
    private MarketOperatorListener testAgentlistener;

    @Before
    public void setUp() throws Exception {
        operator = new BalancingMarketOperatorImpl("test", true, "run/summary-logs/test", 0, 10000);
        testAgentlistener = mock(MarketOperatorListener.class);

        RepastTestInitializer.init();
    }

    @Test
    public void testClearingWithTooLittleSupplies() throws Exception {
        operator.addNegativeSupply(new PowerNegative(10, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(20, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(30, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(40, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));

        operator.clearMarketCapacityPrice();

        verify(testAgentlistener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.BALANCING_MARKET), Matchers.<Bid>any(), Matchers.eq(10f), Matchers.eq(1f));
        verify(testAgentlistener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.BALANCING_MARKET), Matchers.<Bid>any(), Matchers.eq(20f), Matchers.eq(1f));
        verify(testAgentlistener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.BALANCING_MARKET), Matchers.<Bid>any(), Matchers.eq(30f), Matchers.eq(1f));
        verify(testAgentlistener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.BALANCING_MARKET), Matchers.<Bid>any(), Matchers.eq(40f), Matchers.eq(1f));
        verify(testAgentlistener, times(4)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.BALANCING_MARKET), Matchers.<Bid>any(), Matchers.anyFloat(), Matchers.anyFloat());

        assertEquals(40f, operator.getLastClearedNegativeMaxPrice(), 0.0001f);
        assertEquals(4000, operator.getTotalClearedNegativeQuantity(), 0.0001f);
        assertEquals(1, operator.getLastNegativeAssignmentRate(), 0.0001f);
    }

    @Test
    public void testMoreNegativeSupplies() throws Exception {
        operator.addNegativeSupply(new PowerNegative(10, 2000, testAgentlistener, BidType.POWER_POSITIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(20, 2000, testAgentlistener, BidType.POWER_POSITIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(30, 5000, testAgentlistener, BidType.POWER_POSITIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(50, 1500, testAgentlistener, BidType.POWER_POSITIVE_LEISTUNGSPREIS));

        operator.clearMarketCapacityPrice();

        assertEquals(50f, operator.getLastClearedNegativeMaxPrice(), 0.0001f);
        assertEquals(10000, operator.getTotalClearedNegativeQuantity(), 0.0001f);
        assertEquals(2f / 3, operator.getLastNegativeAssignmentRate(), 0.0001f);
    }

    @Test
    public void testLastAssignmentZero() throws Exception {
        operator.addNegativeSupply(new PowerNegative(10, 2000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(20, 2000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(30, 5000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(55, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));
        operator.addNegativeSupply(new PowerNegative(60, 1000, testAgentlistener, BidType.POWER_NEGATIVE_LEISTUNGSPREIS));

        operator.clearMarketCapacityPrice();

        assertEquals(55f, operator.getLastClearedNegativeMaxPrice(), 0.0001f);
        assertEquals(10000, operator.getTotalClearedNegativeQuantity(), 0.0001f);
        assertEquals(1, operator.getLastNegativeAssignmentRate(), 0.0001f);
    }
}