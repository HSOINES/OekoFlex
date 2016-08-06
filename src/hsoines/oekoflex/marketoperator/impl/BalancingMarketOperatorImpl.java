package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.BidSupport;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.summary.LoggerFile;
import hsoines.oekoflex.summary.impl.LoggerFileImpl;
import hsoines.oekoflex.summary.impl.NullLoggerFile;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.NumberFormatUtil;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 20:45
 * Räumt Regelenergiemarkt für a) positive b) negative Leistungen
 */
public final class BalancingMarketOperatorImpl implements BalancingMarketOperator {
    private static final Log log = LogFactory.getLog(BalancingMarketOperatorImpl.class);

    private final String name;
    private final int positiveQuantity;
    private final int negativeQuantity;
    private final List<BidSupport> positiveSupplies = new ArrayList<>();
    private final List<BidSupport> negativeSupplies = new ArrayList<>();

    private float totalClearedPositiveQuantity;
    private float totalClearedNegativeQuantity;

    private float lastClearedPositiveMaxPrice;
    private float lastClearedNegativeMaxPrice;

    private float lastPositiveAssignmentRate;
    private float lastNegativeAssignmentRate;

    private LoggerFile logger;

    public BalancingMarketOperatorImpl(String name, final boolean loggingActivated, String logDirName, final int positiveDemandREM, final int negativeDemandREM) throws IOException {
        this.name = name;
        this.positiveQuantity = positiveDemandREM;
        this.negativeQuantity = negativeDemandREM;
        if (loggingActivated) {
            initLogging(logDirName);
        } else {
            logger = new NullLoggerFile();
        }
    }

    private void initLogging(final String logDirName) throws IOException {
        logger = new LoggerFileImpl(this.getClass().getSimpleName(), logDirName);
        logger.log("tick;traderType;traderName;bidType;offeredPrice;offeredQuantity;assignedQuantity");
    }

    @Override
    public void addPositiveSupply(final PowerPositive supply) {
        if (supply.getQuantity() < 0.00001) {
            return;
        }
        positiveSupplies.add(supply);
    }

    @Override
    public void addNegativeSupply(final PowerNegative supply) {
        if (supply.getQuantity() < 0.00001) {
            return;
        }
        negativeSupplies.add(supply);
    }

    @Override
    public void clearMarket() {
        log.trace("positive clearing.");
        ClearingData positiveClearingData = doClearMarketFor(positiveSupplies, positiveQuantity);
        totalClearedPositiveQuantity = positiveClearingData.getClearedQuantity();
        lastPositiveAssignmentRate = positiveClearingData.getAssignmentRate();
        lastClearedPositiveMaxPrice = positiveClearingData.getLastClearedMaxPrice();
        log.trace("negative clearing.");
        ClearingData negativeClearingData = doClearMarketFor(negativeSupplies, negativeQuantity);
        totalClearedNegativeQuantity = negativeClearingData.getClearedQuantity();
        lastNegativeAssignmentRate = negativeClearingData.getAssignmentRate();
        lastClearedNegativeMaxPrice = negativeClearingData.getLastClearedMaxPrice();
    }

    ClearingData doClearMarketFor(final List<BidSupport> supplies, float quantity) {
        supplies.sort(new BidSupport.SupplySorter());
        float totalClearedQuantity = 0;
        float lastAssignmentRate = 0;
        float lastClearedPrice = 0;
        for (BidSupport bidSupport : supplies) {
            MarketOperatorListener marketOperatorListener = bidSupport.getMarketOperatorListener();
            if (totalClearedQuantity + bidSupport.getQuantity() < quantity) {
                totalClearedQuantity += bidSupport.getQuantity();
                lastAssignmentRate = 1;
                doNotify(bidSupport, marketOperatorListener, 1);
                lastClearedPrice = bidSupport.getPrice();
            } else if (totalClearedQuantity >= quantity) {
                doNotify(bidSupport, marketOperatorListener, 0);
            } else {
                lastAssignmentRate = (quantity - totalClearedQuantity) / bidSupport.getQuantity();
                doNotify(bidSupport, marketOperatorListener, lastAssignmentRate);
                totalClearedQuantity += bidSupport.getQuantity() * lastAssignmentRate;
                lastClearedPrice = bidSupport.getPrice();
            }
        }
        log.trace("Clearing done.");
        supplies.clear();
        final float finalTotalClearedQuantity = totalClearedQuantity;
        final float finalLastAssignmentRate = lastAssignmentRate;
        final float finalLastClearedPrice = lastClearedPrice;
        log.trace("total cleared quantity: " + finalTotalClearedQuantity + ", lasst assignment rate: " + lastAssignmentRate + ", last cleared price: " + lastClearedPrice);
        return new ClearingData() {
            @Override
            public float getClearedQuantity() {
                return finalTotalClearedQuantity;
            }

            @Override
            public float getLastClearedMaxPrice() {
                return finalLastClearedPrice;
            }

            @Override
            public float getAssignmentRate() {
                return finalLastAssignmentRate;
            }
        };
    }

    @Override
    public float getTotalClearedPositiveQuantity() {
        return totalClearedPositiveQuantity;
    }

    @Override
    public float getTotalClearedNegativeQuantity() {
        return totalClearedNegativeQuantity;
    }

    @Override
    public float getLastPositiveAssignmentRate() {
        return lastPositiveAssignmentRate;
    }

    @Override
    public float getLastClearedNegativeMaxPrice() {
        return lastClearedNegativeMaxPrice;
    }

    @Override
    public float getLastNegativeAssignmentRate() {
        return lastNegativeAssignmentRate;
    }

    void doNotify(final BidSupport bidSupport, final MarketOperatorListener marketOperatorListener, float assignRate) {
        long tick = TimeUtil.getCurrentTick();
        marketOperatorListener.notifyClearingDone(TimeUtil.getDate(tick), Market.BALANCING_MARKET, bidSupport, bidSupport.getPrice(), assignRate);

        logger.log(String.valueOf(tick) + ";"
                + bidSupport.getMarketOperatorListener().getClass().getSimpleName() + ";"
                + bidSupport.getMarketOperatorListener().getName() + ";"
                + bidSupport.getBidType() + ";"
                + NumberFormatUtil.format(bidSupport.getPrice()) + ";"
                + NumberFormatUtil.format(bidSupport.getQuantity()) + ";"
                + NumberFormatUtil.format(bidSupport.getQuantity() * assignRate) + ";");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getLastClearedPositiveMaxPrice() {
        return lastClearedPositiveMaxPrice;
    }


    @ScheduledMethod(start = ScheduledMethod.END)
    public void stop() {
        logger.close();
    }


    private interface ClearingData {
        float getClearedQuantity();

        float getLastClearedMaxPrice();

        float getAssignmentRate();
    }

}
