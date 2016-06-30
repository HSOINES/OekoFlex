package hsoines.oekoflex.summary;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.summary.impl.EnergyTraderTypeLogger;
import hsoines.oekoflex.util.Market;
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
        energyTraderTypeLogger = new EnergyTraderTypeLogger(new DefaultContext<>(), "run/summary-logs/test");
    }

    @Test
    public void testIt() {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        MyEOMTrader myEOMTrader = new MyEOMTrader(countDownLatch);
        MyBalancingMarketTrader myRegelenergieMarketTrader = new MyBalancingMarketTrader(countDownLatch);
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
        public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
            countDownLatch.countDown();
            return null;
        }

        @Override
        public String getDescription() {
            return "";
        }


        @Override
        public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

        }

        public void init() {

        }

        @Override
        public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {

        }

        @Override
        public void makeBidEOM() {

        }

        @Override
        public void makeBidEOM(final long currentTick) {

        }


        @Override
        public float getCurrentPower() {
            throw new IllegalStateException("not implemented");
        }


    }

    private static class MyBalancingMarketTrader implements BalancingMarketTrader {
        private final CountDownLatch countDownLatch;

        public MyBalancingMarketTrader(final CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }


        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public float getLastAssignmentRate() {
            return 0;
        }

        @Override
        public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
            countDownLatch.countDown();
            return null;
        }

        @Override
        public void makeBidBalancingMarket() {

        }

        @Override
        public void makeBidBalancingMarket(final long currentTick) {

        }

        @Override
        public void setBalancingMarketOperator(final BalancingMarketOperator balancingMarketOperator) {

        }

        @Override
        public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

        }
    }
}