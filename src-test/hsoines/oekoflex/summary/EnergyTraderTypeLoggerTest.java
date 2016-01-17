package hsoines.oekoflex.summary;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.energytrader.impl.test.EnergyTradeRegistryImpl;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.Duration;
import org.junit.Before;
import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 23:19
 */
public class EnergyTraderTypeLoggerTest {

    private EnergyTraderTypeLogger energyTraderTypeLogger;

    @Before
    public void setUp() throws Exception {
        energyTraderTypeLogger = new EnergyTraderTypeLogger(new DefaultContext<>());
    }

    @Test
    public void testIt() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        MyEOMTrader myEOMTrader = new MyEOMTrader(countDownLatch);
        MyRegelenergieMarketTrader myRegelenergieMarketTrader = new MyRegelenergieMarketTrader(countDownLatch);
        energyTraderTypeLogger.addIfNecessary(myEOMTrader);
        energyTraderTypeLogger.addIfNecessary(myRegelenergieMarketTrader);

        energyTraderTypeLogger.execute();
        try {
            countDownLatch.await(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("countdownlatch must be already at 0 here.");
        }
    }

    private static class MyEOMTrader implements EOMTrader {
        private final CountDownLatch countDownLatch;

        public MyEOMTrader(final CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public float getLastClearedPrice() {
            return 0;
        }

        @Override
        public float getLastAssignmentRate() {
            return 0;
        }

        @Override
        public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
            countDownLatch.countDown();
            return null;
        }


        @Override
        public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate, final Duration duration) {

        }

        @Override
        public void setEOMOperator(final EOMOperator marketOperator) {

        }

        @Override
        public void makeBidEOM() {

        }

    }

    private static class MyRegelenergieMarketTrader implements RegelenergieMarketTrader {
        private final CountDownLatch countDownLatch;

        public MyRegelenergieMarketTrader(final CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }


        @Override
        public String getName() {
            return null;
        }

        @Override
        public float getLastClearedPrice() {
            return 0;
        }

        @Override
        public float getLastAssignmentRate() {
            return 0;
        }

        @Override
        public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
            countDownLatch.countDown();
            return null;
        }

        @Override
        public void makeBidRegelenergie() {

        }

        @Override
        public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator marketOperator) {

        }

        @Override
        public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate, final Duration duration) {

        }
    }
}