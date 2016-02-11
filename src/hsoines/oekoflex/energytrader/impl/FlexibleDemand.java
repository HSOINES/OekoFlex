package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 17/01/16
 * Time: 23:03
 */
public final class FlexibleDemand implements EOMTrader {
    private static final Log log = LogFactory.getLog(FlexibleDemand.class);
    public static final float FIXED_PRICE = 3000f;
    private final TradeRegistryImpl energyTradeRegistry;
    private final String name;

    private EOMOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    public FlexibleDemand(final String name, final File csvFile) throws IOException {
        this.name = name;
        energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.CONSUM, 0);
        FileReader reader = new FileReader(csvFile);
        CSVParser parser = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : parser) {
            try {
                long tick = Long.parseLong(parameters.get("tick"));
                int demand = Integer.parseInt(parameters.get("demand"));
                energyTradeRegistry.setCapacity(tick, demand);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.marketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        int remainingCapacity = energyTradeRegistry.getRemainingCapacity(TimeUtil.getCurrentDate(), Market.EOM_MARKET);
        marketOperator.addDemand(new EnergyDemand(FIXED_PRICE, remainingCapacity, this));
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }
}
