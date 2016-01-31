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
    private final int powerMax;
    private final int powerMin;
    private final int rampUp;
    private final int rampDown;
    private final float costs;
    private final float shutdownCosts;
    private final int capacity;
    private int soc;
    private EOMOperator eomMarketOperator;
    private final EnergyTradeRegistry batteryEnergyTradeRegistry;
    private float lastAssignmentRate;
    private float lastClearedPrice;


    public Storage(final String name, final int powerMax, final int powerMin, final int rampUp, final int rampDown, final float marginalCosts, final float shutdownCosts, final int capacity) {
        this.name = name;
        this.powerMax = powerMax;
        this.powerMin = powerMin;
        this.rampUp = rampUp;
        this.rampDown = rampDown;
        costs = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        this.capacity = capacity;
        batteryEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, capacity);
        soc = 0;
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        int capacity = batteryEnergyTradeRegistry.getCapacity(currentDate);
        eomMarketOperator.addSupply(new PositiveSupply(costs * 1.1f, soc, this));
        eomMarketOperator.addDemand(new Demand(costs * 0.9f, capacity - soc, this));
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        batteryEnergyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        int assignedQuantity = (int) (bid.getQuantity() * rate);
        if (bid instanceof Demand) {
            soc += assignedQuantity;
        } else if (bid instanceof PositiveSupply) {
            soc -= assignedQuantity;
        } else {
            log.error("not impemented.");
        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (soc < 0 || soc > capacity) {
            throw new IllegalStateException("batterylevel exceeds capacity: " + soc + ", MaxLevel: " + capacity);
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

    public int getSoc() {

        return soc;
    }
}
