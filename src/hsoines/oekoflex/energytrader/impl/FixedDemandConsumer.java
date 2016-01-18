package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtilities;
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
public final class FixedDemandConsumer implements EOMTrader {
    private static final Log log = LogFactory.getLog(FixedDemandConsumer.class);
    public static final float FIXED_PRICE = 3000f;
    private final EnergyTradeRegistryImpl energyTradeRegistry;
    private final String name;

    private EOMOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    public FixedDemandConsumer(final String name, final File csvFile) throws IOException {
        this.name = name;
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.CONSUM, 0);
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
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.marketOperator = marketOperator;
    }

    @Override
    public void makeBidEOM() {
        int remainingCapacity = energyTradeRegistry.getRemainingCapacity(TimeUtilities.getCurrentDate(), Market.EOM_MARKET);
        marketOperator.addDemand(new Demand(FIXED_PRICE, remainingCapacity, this));
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate);
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
    public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return energyTradeRegistry.getEnergyTradeElements(TimeUtilities.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }
}
