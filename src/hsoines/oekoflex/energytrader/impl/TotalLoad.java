package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
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
public final class TotalLoad implements EOMTrader {
    private static final Log log = LogFactory.getLog(TotalLoad.class);
    public static final float FIXED_PRICE = 3000f;
    private final TradeRegistryImpl energyTradeRegistry;
    private final String name;
    private final String description;

    private SpotMarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    public TotalLoad(final String name, final String description, final File csvFile) throws IOException {
        this.name = name;
        this.description = description;
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
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.marketOperator = spotMarketOperator;
    }

    @Override
    public void makeBidEOM() {
        float remainingCapacity = energyTradeRegistry.getRemainingCapacity(TimeUtil.getCurrentDate(), Market.SPOT_MARKET);
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

    @Override
    public String getDescription() {
        return description;
    }
}
