package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.energytrader.impl.test.EnergyTradeRegistryImpl;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.util.Duration;
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
    public static final float PRICE = 1f;
    private final EnergyTradeRegistryImpl energyTradeRegistry;

    private EOMOperator marketOperator;

    public FixedDemandConsumer(final File csvFile) throws IOException {
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeHistory.Type.CONSUM, 0);
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
        int remainingCapacity = energyTradeRegistry.getRemainingCapacity(TimeUtilities.getCurrentDate(), Duration.QUARTER_HOUR);
        marketOperator.addDemand(new Demand(PRICE, remainingCapacity, this));
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate, final Duration duration) {
        energyTradeRegistry.addAssignedQuantity(currentDate, Duration.QUARTER_HOUR, bid.getPrice(), clearedPrice, bid.getQuantity(), rate);
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
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
