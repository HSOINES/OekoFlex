package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.EOMOperatorListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.Schedule;

import java.util.Date;

import static org.mockito.Mockito.*;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 11:14
 */
public class RegelEnergieMarketOperatorImplTest {

    private RegelEnergieMarketOperatorImpl operator;
    private EOMOperatorListener listener;

    @Before
    public void setUp() throws Exception {
        operator = new RegelEnergieMarketOperatorImpl(10000);
        listener = mock(EOMOperatorListener.class);

        Schedule schedule = new Schedule();
        RunEnvironment.init(schedule, null, null, true);
        Context context = new DefaultContext();
        RunState.init().setMasterContext(context);
    }

    @Test
    public void testClearingWithTooLittleSupplies() throws Exception {
        operator.addSupply(new Supply(10, 1000, listener));
        operator.addSupply(new Supply(20, 1000, listener));
        operator.addSupply(new Supply(30, 1000, listener));
        operator.addSupply(new Supply(40, 1000, listener));

        operator.clearMarket();

        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(40f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(64)).notifyEOMClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any(), Matchers.any());
    }

    @Test
    public void testFourTimesNotification() throws Exception {
        operator.addSupply(new Supply(10, 1000, listener));

        operator.clearMarket();

        verify(listener, times(1)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), argThat(new DateMatcher(new Date(0))));
        verify(listener, times(1)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), argThat(new DateMatcher(new Date(15 * 60 * 1000))));
        verify(listener, times(1)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), argThat(new DateMatcher(new Date(30 * 60 * 1000))));
        verify(listener, times(1)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), argThat(new DateMatcher(new Date(45 * 60 * 1000))));
    }

    @Test
    public void testClearingWithExactlyQuantity() throws Exception {
        operator.addSupply(new Supply(10, 2500, listener));
        operator.addSupply(new Supply(20, 2500, listener));
        operator.addSupply(new Supply(30, 2500, listener));
        operator.addSupply(new Supply(40, 2500, listener));

        operator.clearMarket();

        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(40f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(64)).notifyEOMClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any(), Matchers.any());
    }

    @Test
    public void testClearingWithRatedQuantity() throws Exception {
        operator.addSupply(new Supply(10, 2500, listener));
        operator.addSupply(new Supply(20, 2500, listener));
        operator.addSupply(new Supply(30, 2500, listener));
        operator.addSupply(new Supply(40, 5000, listener));

        operator.clearMarket();

        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(40f), Matchers.eq(.5f), Matchers.<Bid>any(), Matchers.any());

        verify(listener, times(64)).notifyEOMClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any(), Matchers.any());

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

        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(10f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(20f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(30f), Matchers.eq(1f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(SequenceDefinition.RegelenergieMarketInterval)).notifyEOMClearingDone(Matchers.eq(40f), Matchers.eq(.5f), Matchers.<Bid>any(), Matchers.any());
        verify(listener, times(64)).notifyEOMClearingDone(Matchers.anyFloat(), Matchers.anyFloat(), Matchers.<Bid>any(), Matchers.any());
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