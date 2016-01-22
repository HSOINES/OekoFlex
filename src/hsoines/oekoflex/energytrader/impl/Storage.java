package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 16:14
 */
public final class Storage implements EOMTrader, MarketOperatorListener {
    private static final Log log = LogFactory.getLog(Storage.class);

    private final String name;
    private final int batteryCapacity;
    private final float costs;
    private final float spread;
    private int batteryLevel;
    private EOMOperator eomMarketOperator;
    private final EnergyTradeRegistry batteryEnergyTradeRegistry;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public Storage(String name, int batteryCapacity, final float costs, float spread) {
        this.name = name;
        this.batteryCapacity = batteryCapacity;
        this.costs = costs;
        this.spread = spread;
        batteryEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, batteryCapacity);
        batteryLevel = 0;
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        int capacity = batteryEnergyTradeRegistry.getCapacity(currentDate);
        eomMarketOperator.addSupply(new PositiveSupply(costs * (1 + spread), batteryLevel, this));
        eomMarketOperator.addDemand(new Demand(costs * (1 - spread), capacity - batteryLevel, this));
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        batteryEnergyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        int assignedQuantity = (int) (bid.getQuantity() * rate);
        if (bid instanceof Demand) {
            batteryLevel += assignedQuantity;
        } else if (bid instanceof PositiveSupply) {
            batteryLevel -= assignedQuantity;
        } else {
            log.error("not impemented.");
        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (batteryLevel < 0 || batteryLevel > batteryCapacity) {
            throw new IllegalStateException("batterylevel exceeds capacity: " + batteryLevel + ", MaxLevel: " + batteryCapacity);
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
        return batteryEnergyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }

    public int getBatteryLevel() {

        return batteryLevel;
    }
}
