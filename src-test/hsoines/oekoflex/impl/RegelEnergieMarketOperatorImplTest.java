package hsoines.oekoflex.impl;

import hsoines.oekoflex.Bid;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.supply.Supply;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import static org.mockito.Mockito.*;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 11:14
 */
public class RegelEnergieMarketOperatorImplTest {

    private RegelenergieMarketOperatorImpl operator;
    private MarketOperatorListener listener;

    @Before
    public void setUp() throws Exception {
        operator = new RegelenergieMarketOperatorImpl("operator", 10000);
        listener = mock(MarketOperatorListener.class);
    }

    @Test
    public void testClearingWithTooLittleSupplies() throws Exception {
        operator.addSupply(new Supply(10, 1000, listener));
        operator.addSupply(new Supply(20, 1000, listener));
        operator.addSupply(new Supply(30, 1000, listener));
        operator.addSupply(new Supply(40, 1000, listener));

        operator.clearMarket();

        verify(listener).notifyClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(40f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener, times(4)).notifyClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any());
    }

    @Test
    public void testClearingWithExactlyQuantity() throws Exception {
        operator.addSupply(new Supply(10, 2500, listener));
        operator.addSupply(new Supply(20, 2500, listener));
        operator.addSupply(new Supply(30, 2500, listener));
        operator.addSupply(new Supply(40, 2500, listener));

        operator.clearMarket();

        verify(listener).notifyClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(40f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener, times(4)).notifyClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any());
    }

    @Test
    public void testClearingWithRatedQuantity() throws Exception {
        operator.addSupply(new Supply(10, 2500, listener));
        operator.addSupply(new Supply(20, 2500, listener));
        operator.addSupply(new Supply(30, 2500, listener));
        operator.addSupply(new Supply(40, 5000, listener));

        operator.clearMarket();

        verify(listener).notifyClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(40f), Matchers.eq(.5f), Matchers.<Bid>any());

        verify(listener, times(4)).notifyClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any());

    }

    @Test
    public void testClearingWithManyMoreSupplies() throws Exception {
        operator.addSupply(new Supply(10, 2500, listener));
        operator.addSupply(new Supply(20, 2500, listener));
        operator.addSupply(new Supply(30, 2500, listener));
        operator.addSupply(new Supply(40, 5000, listener));
        operator.addSupply(new Supply(50, 5000, listener));
        operator.addSupply(new Supply(50, 5000, listener));
        operator.addSupply(new Supply(50, 5000, listener));
        operator.addSupply(new Supply(50, 5000, listener));

        operator.clearMarket();

        verify(listener).notifyClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any());
        verify(listener).notifyClearingDone(Matchers.eq(40f), Matchers.eq(.5f), Matchers.<Bid>any());
        verify(listener, times(4)).notifyClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any());

    }

    @After
    public void tearDown() throws Exception {

    }
}