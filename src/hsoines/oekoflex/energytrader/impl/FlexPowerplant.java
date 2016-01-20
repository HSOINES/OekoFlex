package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

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
    private final EnergyTradeRegistry energyTradeRegistry;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public FlexPowerplant(String name, int capacity, float costs, float supplyDelay) {
        this.name = name;
        this.costs = costs;
        this.supplyDelay = supplyDelay;
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, capacity);
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        int supplyCapacity = getSupplyCapacity(currentDate, Market.EOM_MARKET);
        eomMarketOperator.addSupply(new Supply(costs * 1f, supplyCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date currentDate = TimeUtil.getCurrentDate();
        int supplyCapacity = getSupplyCapacity(currentDate, Market.REGELENERGIE_MARKET);
        regelenergieMarketOperator.addSupply(new Supply(costs * 1.5f, supplyCapacity, this));

    }

    int getSupplyCapacity(final Date currentDate, final Market market) {
        int lastProducedCapacity = Math.max(energyTradeRegistry.getQuantityUsed(TimeUtil.precedingDate(currentDate)), 200);
        int remainingCapacity = energyTradeRegistry.getRemainingCapacity(currentDate, market);
        return (int) Math.min(lastProducedCapacity * supplyDelay, remainingCapacity);
    }

    @Override
    public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator regelenergieMarketOperator) {
        this.regelenergieMarketOperator = regelenergieMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
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
        return energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }
}
