package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;

import java.util.Date;

import static org.mockito.Mockito.*;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 11:14
 */
public class RegelEnergieMarketImplTest {

    private RegelEnergieMarketOperatorImpl operator;
    private MarketOperatorListener listener;

    @Before
    public void setUp() throws Exception {
        operator = new RegelEnergieMarketOperatorImpl("test", "run/summary-logs/test", 10000);
        listener = mock(MarketOperatorListener.class);

        RepastTestInitializer.init();
    }

    @Test
    public void testClearingWithTooLittleSupplies() throws Exception {
        operator.addSupply(new PositiveSupply(10, 1000, listener));
        operator.addSupply(new PositiveSupply(20, 1000, listener));
        operator.addSupply(new PositiveSupply(30, 1000, listener));
        operator.addSupply(new PositiveSupply(40, 1000, listener));

        operator.clearMarket();

        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(10f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(20f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(30f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(40f), Matchers.eq(1f));
        verify(listener, times(4)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.anyFloat(), Matchers.anyFloat());
    }

    @Test
    public void testFourTimesNotification() throws Exception {
        operator.addSupply(new PositiveSupply(10, 1000, listener));

        operator.clearMarket();

        Date dateWithMinutesOffset = TimeUtil.getDateWithMinutesOffset(-15);
        verify(listener, times(1)).notifyClearingDone(argThat(new DateMatcher(dateWithMinutesOffset)), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<PositiveSupply>any(), Matchers.eq(10f), Matchers.eq(1f));
    }

    @Test
    public void testClearingWithExactlyQuantity() throws Exception {
        operator.addSupply(new PositiveSupply(10, 2500, listener));
        operator.addSupply(new PositiveSupply(20, 2500, listener));
        operator.addSupply(new PositiveSupply(30, 2500, listener));
        operator.addSupply(new PositiveSupply(40, 2500, listener));

        operator.clearMarket();

        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(10f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(20f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(30f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(40f), Matchers.eq(1f));
        verify(listener, times(4)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.anyFloat(), Matchers.anyFloat());
    }

    @Test
    public void testClearingWithRatedQuantity() throws Exception {
        operator.addSupply(new PositiveSupply(10, 2500, listener));
        operator.addSupply(new PositiveSupply(20, 2500, listener));
        operator.addSupply(new PositiveSupply(30, 2500, listener));
        operator.addSupply(new PositiveSupply(40, 5000, listener));

        operator.clearMarket();

        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(10f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(20f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(30f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(40f), Matchers.eq(.5f));

        verify(listener, times(4)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.anyFloat(), Matchers.anyFloat());

    }

    @Test
    public void testClearingWithManyMoreSupplies() throws Exception {
        operator.addSupply(new PositiveSupply(10, 2500, listener));
        operator.addSupply(new PositiveSupply(20, 2500, listener));
        operator.addSupply(new PositiveSupply(30, 2500, listener));
        operator.addSupply(new PositiveSupply(40, 5000, listener));
        operator.addSupply(new PositiveSupply(50, 5000, listener));
        operator.addSupply(new PositiveSupply(50, 5000, listener));
        operator.addSupply(new PositiveSupply(50, 5000, listener));
        operator.addSupply(new PositiveSupply(50, 5000, listener));

        operator.clearMarket();

        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(10f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(20f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(30f), Matchers.eq(1f));
        verify(listener, times(1)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.eq(40f), Matchers.eq(.5f));
        verify(listener, times(8)).notifyClearingDone(Matchers.any(), Matchers.eq(Market.REGELENERGIE_MARKET), Matchers.<Bid>any(), Matchers.anyFloat(), Matchers.anyFloat());
    }


    @After
    public void tearDown() throws Exception {

    }

    class DateMatcher extends ArgumentMatcher<Date> {
        private final Date date;

        public DateMatcher(Date date) {
            this.date = date;
        }

        public boolean matches(Object arg) {
            Date thing = (Date) arg;
            return thing.equals(date);
        }
    }
}