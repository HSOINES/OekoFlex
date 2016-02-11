package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
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
public final class Storage implements EOMTrader, RegelenergieMarketTrader {
    private static final Log log = LogFactory.getLog(Storage.class);

    private final String name;
    private final int powerMax;
    private final int powerMin;
    private final float costs;
    private final float shutdownCosts;
    private final int capacity;
    private final float socMax;
    private final float socMin;
    private final int chargePower;
    private final int dischargePower;
    private int soc;
    private EOMOperator eomMarketOperator;
    private final TradeRegistry batteryTradeRegistry;
    private float lastAssignmentRate;
    private float lastClearedPrice;
    private RegelEnergieMarketOperator regelenergieMarketOperator;


    public Storage(final String name, final int powerMax, final int powerMin,
                   final float marginalCosts, final float shutdownCosts,
                   final int capacity, final float socMax, final float socMin,
                   final int chargePower, final int dischargePower) {
        this.name = name;
        this.powerMax = powerMax;
        this.powerMin = powerMin;
        costs = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        this.capacity = capacity;
        this.socMax = socMax;
        this.socMin = socMin;
        this.chargePower = chargePower;
        this.dischargePower = dischargePower;
        batteryTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, capacity);
        soc = 0;
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        float capacity = batteryTradeRegistry.getCapacity(currentDate);
        //eomMarketOperator.addSupply(new EnergySupply(costs * 1.1f, soc, this));
        //eomMarketOperator.addDemand(new EnergyDemand(costs * 0.9f, capacity - soc, this));

    }

    @Override
    public void makeBidRegelenergie() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);
        float cOpp = 200f;
        float bidNegative = 100f;
        float bidPositive = 300f;

        float pPreceding = batteryTradeRegistry.getQuantityUsed(precedingDate);

        float pNegative = Math.min(pPreceding - powerMin, dischargePower);
        //regelenergieMarketOperator.addNegativeSupply(new PowerNegative(bidNegative, pNegative, this));

        float pPositive = Math.min(powerMax - pPreceding, chargePower);
        //regelenergieMarketOperator.addPositiveSupply(new PowerPositive(bidPositive, pPositive, this));

    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        batteryTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        int assignedQuantity = (int) (bid.getQuantity() * rate);
        if (bid instanceof EnergyDemand) {
            soc += assignedQuantity;
        } else if (bid instanceof PowerPositive) {
            soc -= assignedQuantity * market.getTicks() / 4;
        } else if (bid instanceof PowerNegative) {
            soc += assignedQuantity * market.getTicks() / 4;
        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (soc < 0 || soc > capacity) {
            //  throw new IllegalStateException("batterylevel exceeds capacity: " + soc + ", MaxLevel: " + capacity);
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
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return batteryTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }

    public int getSoc() {

        return soc;
    }

    @Override
    public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator regelenergieMarketOperator) {
        this.regelenergieMarketOperator = regelenergieMarketOperator;
    }
}
