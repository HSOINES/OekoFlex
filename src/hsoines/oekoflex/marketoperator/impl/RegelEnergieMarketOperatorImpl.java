package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.NegativeSupply;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.summary.LoggerFile;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.NumberFormatUtil;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 20:45
 */
public final class RegelEnergieMarketOperatorImpl implements RegelEnergieMarketOperator {
    private static final Log log = LogFactory.getLog(RegelEnergieMarketOperatorImpl.class);

    private final String name;
    private final int positiveQuantity;
    private final int negativeQuantity;
    private final List<PositiveSupply> positiveSupplies = new ArrayList<>();
    private final List<NegativeSupply> negativeSupplies = new ArrayList<>();
    private long totalClearedPositiveQuantity;
    private long totalClearedNegativeQuantity;
    private float lastClearedPositiveMaxPrice;
    private float lastPositiveAssignmentRate;
    private float lastNegativeAssignmentRate;
    private LoggerFile logger;

    public RegelEnergieMarketOperatorImpl(String name, String logDirName) throws IOException {
        this.name = name;
        Parameters p = RunEnvironment.getInstance().getParameters();
        this.positiveQuantity = (int) p.getValue("rigidDemandEnergyOnlyMarket"); //todo
        this.negativeQuantity = (int) p.getValue("rigidDemandEnergyOnlyMarket"); //todo
        init(logDirName);
    }

    public RegelEnergieMarketOperatorImpl(final String name, String logDirName, int positiveQuantity, int negativeQuantity) throws IOException {
        this.positiveQuantity = positiveQuantity;
        this.name = name;
        this.negativeQuantity = negativeQuantity;
        init(logDirName);
    }

    private void init(final String logDirName) throws IOException {
        logger = new LoggerFile(this.getClass().getSimpleName(), logDirName);
        logger.log("tick;traderType;traderName;offeredPrice;offeredQuantity;assignedQuantity");
    }

    @Override
    public void addPositiveSupply(final PositiveSupply supply) {
        positiveSupplies.add(supply);
    }

    @Override
    public void addNegativeSupply(final NegativeSupply supply) {
        negativeSupplies.add(supply);
    }

    @Override
    public void clearMarket() {
        AssignmentRateAndClearedQuantity positiveAssignmentRateAndClearedQuantity = doClearMarketFor(positiveSupplies, positiveQuantity);
        totalClearedPositiveQuantity = positiveAssignmentRateAndClearedQuantity.getClearedQuantity();
        lastPositiveAssignmentRate = positiveAssignmentRateAndClearedQuantity.getAssignmentRate();
        AssignmentRateAndClearedQuantity negativeAssignmentRateAndClearedQuantity = doClearMarketFor(positiveSupplies, negativeQuantity);
        totalClearedNegativeQuantity = negativeAssignmentRateAndClearedQuantity.getClearedQuantity();
        lastNegativeAssignmentRate = negativeAssignmentRateAndClearedQuantity.getAssignmentRate();
    }

    AssignmentRateAndClearedQuantity doClearMarketFor(final List<PositiveSupply> supplies, int quantity) {
        supplies.sort((o1, o2) -> Float.compare(o1.getPrice(), o2.getPrice()));
        int totalClearedQuantity = 0;
        float lastAssignmentRate = 0;
        for (Supply supply : this.positiveSupplies) {
            MarketOperatorListener marketOperatorListener = supply.getMarketOperatorListener();
            if (totalClearedQuantity + supply.getQuantity() < quantity) {
                totalClearedQuantity += supply.getQuantity();
                lastAssignmentRate = 1;
                doNotify(supply, marketOperatorListener, 1);
            } else if (totalClearedQuantity >= quantity) {
                doNotify(supply, marketOperatorListener, 0);
            } else {
                lastAssignmentRate = (quantity - totalClearedQuantity) / (float) supply.getQuantity();
                doNotify(supply, marketOperatorListener, lastAssignmentRate);
                totalClearedQuantity += supply.getQuantity() * lastAssignmentRate;
            }
        }
        log.info("Clearing done.");
        supplies.clear();
        final int finalTotalClearedQuantity = totalClearedQuantity;
        final float finalLastAssignmentRate = lastAssignmentRate;
        return new AssignmentRateAndClearedQuantity() {
            @Override
            public int getClearedQuantity() {
                return finalTotalClearedQuantity;
            }

            @Override
            public float getAssignmentRate() {
                return finalLastAssignmentRate;
            }
        };
    }

    @Override
    public long getTotalClearedPositiveQuantity() {
        log.info("Cleared Quantity:" + totalClearedPositiveQuantity);
        return totalClearedPositiveQuantity;
    }

    @Override
    public long getTotalClearedNegativeQuantity() {
        log.info("Cleared Quantity:" + totalClearedNegativeQuantity);
        return totalClearedNegativeQuantity;
    }

    @Override
    public float getLastPositiveAssignmentRate() {
        return lastPositiveAssignmentRate;
    }

    void doNotify(final Supply supply, final MarketOperatorListener marketOperatorListener, float assignRate) {
        long tick = TimeUtil.getCurrentTick();
        marketOperatorListener.notifyClearingDone(TimeUtil.getDate(tick), Market.REGELENERGIE_MARKET, supply, supply.getPrice(), assignRate);
        lastClearedPositiveMaxPrice = supply.getPrice();

        logger.log(String.valueOf(tick) + ";"
                + supply.getMarketOperatorListener().getClass().getSimpleName() + ";"
                + supply.getMarketOperatorListener().getName() + ";"
                + NumberFormatUtil.format(supply.getPrice()) + ";"
                + supply.getQuantity() + ";"
                + (int) (supply.getQuantity() * assignRate) + ";");
    }

    @Override
    public String getName() {
        return name;
    }

    public float getLastClearedPositiveMaxPrice() {
        return lastClearedPositiveMaxPrice;
    }

    private interface AssignmentRateAndClearedQuantity {
        int getClearedQuantity();

        float getAssignmentRate();
    }
}
