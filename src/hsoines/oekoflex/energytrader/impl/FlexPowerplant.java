package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.NegativeSupply;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 16:14
 */
public final class FlexPowerplant implements EOMTrader, RegelenergieMarketTrader, MarketOperatorListener {
    private final String name;
    private final float costs;
    private final float supplyDelay;
    private EOMOperator eomMarketOperator;
    private final EnergyTradeRegistry positiveEnergyTradeRegistry;
    private final EnergyTradeRegistry negativeEnergyTradeRegistry;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public FlexPowerplant(String name, int capacity, float costs, float supplyDelay, final File profileFile) throws IOException {
        this(name, capacity, costs, supplyDelay);
        readFile(profileFile);
    }

    public FlexPowerplant(String name, int capacity, float costs, float supplyDelay) throws IOException {
        this.name = name;
        this.costs = costs;
        this.supplyDelay = supplyDelay;
        positiveEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, capacity);
        negativeEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, capacity);
    }

    private void readFile(final File profileFile) throws IOException {
        Reader reader = new FileReader(profileFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            int tick = Integer.parseInt(parameters.get("tick"));
            int positiveQuantity = Integer.parseInt(parameters.get("positiveQuantity"));
            int negativeQuantity = Integer.parseInt(parameters.get("negativeQuantity"));
            positiveEnergyTradeRegistry.setCapacity(tick, positiveQuantity);
            negativeEnergyTradeRegistry.setCapacity(tick, negativeQuantity);
        }
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        int supplyCapacity = getSupplyCapacity(currentDate, Market.EOM_MARKET);
        eomMarketOperator.addSupply(new PositiveSupply(costs * 1f, supplyCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date currentDate = TimeUtil.getCurrentDate();
        int supplyCapacity = getSupplyCapacity(currentDate, Market.REGELENERGIE_MARKET);
        regelenergieMarketOperator.addPositiveSupply(new PositiveSupply(costs * 1.5f, supplyCapacity, this));
        regelenergieMarketOperator.addNegativeSupply(new NegativeSupply(costs * .5f, (int) (0.1 * supplyCapacity), this));
    }

    @Override
    public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator regelenergieMarketOperator) {
        this.regelenergieMarketOperator = regelenergieMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        switch (bid.getBidType()) {
            case POSITIVE_SUPPLY:
                positiveEnergyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
                break;
            case NEGATIVE_SUPPLY:
                negativeEnergyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
                break;
        }
        if (market.equals(Market.EOM_MARKET)) {
            this.lastClearedPrice = clearedPrice;
            this.lastAssignmentRate = rate;
        }
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
        List<EnergyTradeRegistryImpl.EnergyTradeElement> positiveEnergyTradeElements = positiveEnergyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        List<EnergyTradeRegistryImpl.EnergyTradeElement> negativeEnergyTradeElements = negativeEnergyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        positiveEnergyTradeElements.addAll(negativeEnergyTradeElements);
        return positiveEnergyTradeElements;
    }

    @Override
    public String getName() {
        return name;
    }

    int getSupplyCapacity(final Date currentDate, final Market market) {     //test implementierung
        int lastProducedCapacity = Math.max(positiveEnergyTradeRegistry.getQuantityUsed(TimeUtil.precedingDate(currentDate)), 200);
        int remainingCapacity = positiveEnergyTradeRegistry.getRemainingCapacity(currentDate, market);
        return (int) Math.min(lastProducedCapacity * supplyDelay, remainingCapacity);
    }
}
