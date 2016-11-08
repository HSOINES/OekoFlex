//package hsoines.oekoflex.energytrader.tools;
//
//import hsoines.oekoflex.bid.PowerNegative;
//import hsoines.oekoflex.bid.PowerPositive;
//import hsoines.oekoflex.energytrader.BalancingMarketTrader;
//import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
//import hsoines.oekoflex.util.Market;
//import hsoines.oekoflex.util.TimeUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
///**
// * User: jh
// * Date: 16/06/16
// * Time: 12:31
// */
//public class TestBalancingMarketOperator implements BalancingMarketOperator {
//
//    private PowerNegative lastPowerNegative;
//    private PowerPositive lastPowerPositive;
//    private final List<PowerNegative> powerNegatives;
//    private final List<PowerPositive> powerPositives;
//    private BalancingMarketTrader balancingMarketTrader;
//
//    public TestBalancingMarketOperator() {
//        TimeUtil.startAt(0);
//        powerNegatives = new ArrayList<>();
//        powerPositives = new ArrayList<>();
//    }
//
//    @Override
//    public void addPositiveSupply(PowerPositive powerPositive) {
//        lastPowerPositive = powerPositive;
//        powerPositives.add(powerPositive);
//    }
//
//    @Override
//    public void addNegativeSupply(PowerNegative powerNegative) {
//        lastPowerNegative = powerNegative;
//        powerNegatives.add(powerNegative);
//    }
//
//    @Override
//    public void clearMarket() {
//
//    }
//
//    @Override
//    public float getTotalClearedPositiveQuantity() {
//        return 0;
//    }
//
//    @Override
//    public float getTotalClearedNegativeQuantity() {
//        return 0;
//    }
//
//    @Override
//    public float getLastPositiveAssignmentRate() {
//        return 0;
//    }
//
//    @Override
//    public float getLastClearedNegativeMaxPrice() {
//        return 0;
//    }
//
//    @Override
//    public float getLastNegativeAssignmentRate() {
//        return 0;
//    }
//
//    @Override
//    public float getLastClearedPositiveMaxPrice() {
//        return 0;
//    }
//
//    @Override
//    public String getName() {
//        return null;
//    }
//
//    public PowerNegative getLastPowerNegative() {
//        return lastPowerNegative;
//    }
//
//    public PowerPositive getLastPowerPositive() {
//        return lastPowerPositive;
//    }
//
//    public PowerNegative getPowerNegative(int i) {
//        return powerNegatives.get(i);
//    }
//
//    public PowerPositive getPowerPositive(int i) {
//        return powerPositives.get(i);
//    }
//
//    public TestBalancingMarketOperator checkPowerPos(final float posEnergy, final float posPrice) {
//        if (posEnergy > 0) {
//            assertEquals(posEnergy, getLastPowerPositive().getQuantity(), 0.001f);
//            assertEquals(posPrice, getLastPowerPositive().getPrice(), 0.001f);
//        } else {
//            assertNull(getLastPowerPositive());
//        }
//        return this;
//    }
//
//    public TestBalancingMarketOperator checkPowerNeg(final float negEnergy, final float negPrice) {
//        if (negEnergy > 0) {
//            assertEquals(negEnergy, getLastPowerNegative().getQuantity(), 0.001f);
//            assertEquals(negPrice, getLastPowerNegative().getPrice(), 0.001f);
//        } else {
//            assertNull(getLastPowerNegative());
//        }
//        return this;
//    }
//
//    public TestBalancingMarketOperator notifyRatePos(final int rate) {
//        if (rate > 0) {
//            balancingMarketTrader.notifyClearingDone(TimeUtil.getCurrentDate(), Market.BALANCING_MARKET, lastPowerPositive, 0.0f, rate);
//        }
//        return this;
//    }
//
//    public TestBalancingMarketOperator notifyRateNeg(final int rate) {
//        if (rate > 0) {
//            balancingMarketTrader.notifyClearingDone(TimeUtil.getCurrentDate(), Market.BALANCING_MARKET, lastPowerNegative, 0.0f, rate);
//        }
//        return this;
//    }
//
//    public TestBalancingMarketOperator makeBid(final BalancingMarketTrader balancingMarketTrader) {
//        lastPowerNegative = null;
//        lastPowerPositive = null;
//        this.balancingMarketTrader = balancingMarketTrader;
//        balancingMarketTrader.makeBidBalancingMarket();
//
//        return this;
//    }
//}
