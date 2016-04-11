package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.*;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 16:14
 *
 * soc: state of charge, in percent
 *
 */
public final class Storage implements EOMTrader, BalancingMarketTrader {
    private static final Log log = LogFactory.getLog(Storage.class);

    private final String name;
    private final String description;
    private final float marginalCosts;
    private final float shutdownCosts;
    private final int energyCapacity;
    private final float socMax;
    private final float socMin;
    private final int chargePower;
    private final int dischargePower;
    private final PriceForwardCurve priceForwardCurve;
    private float soc;
    private SpotMarketOperator eomMarketOperator;
    private TradeRegistry storageEnergyTradeHistory;
    private float lastAssignmentRate;
    private float lastClearedPrice;
    private BalancingMarketOperator balancingMarketOperator;
    private TradeRegistryImpl storagePowerTradeHistory;


    public Storage(final String name, final String description,
                   final float marginalCosts, final float shutdownCosts,
                   final int energyCapacity, final float socMax, final float socMin,
                   final int chargePower, final int dischargePower,
                   PriceForwardCurve priceForwardCurve) {
        this.name = name;
        this.description = description;
        this.marginalCosts = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        this.energyCapacity = energyCapacity;
        this.socMax = socMax;
        this.socMin = socMin;
        this.chargePower = chargePower;
        this.dischargePower = dischargePower;
        this.priceForwardCurve = priceForwardCurve;
        init();
    }

    public void init() {
        storageEnergyTradeHistory = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, energyCapacity, 1000);
        storagePowerTradeHistory = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE_AND_CONSUM, energyCapacity, 1000);
        soc = socMin;
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.eomMarketOperator = spotMarketOperator;
    }

    @Override
    public void makeBidBalancingMarket() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        //float priceForwardMin = priceForwardCurve.getMinimum(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());
        //float priceForwardMax = priceForwardCurve.getMaximum(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());

        float mSpreadEOM = priceForwardCurve.getSpread(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());
        float duration = Market.BALANCING_MARKET.getTicks() * TimeUtil.HOUR_PER_TICK;

        float ePreceding = soc * energyCapacity;
        float eMin = socMin * energyCapacity;
        float eMax = socMax * energyCapacity;
        float eDischarge = dischargePower * duration;
        float eCharge = chargePower * duration;

        float ePositive = Math.min(ePreceding - eMin, eDischarge);
        float eNegative = Math.min(eMax - ePreceding, eCharge);

        float bidPositive = mSpreadEOM * ePositive;
        float bidNegative = mSpreadEOM * eNegative;
        if (mSpreadEOM >= marginalCosts) {
            balancingMarketOperator.addPositiveSupply(new PowerPositive(bidPositive, ePositive / duration, this));
            balancingMarketOperator.addNegativeSupply(new PowerNegative(bidNegative, eNegative / duration, this));
        } else {
        	log.debug("mSpread < marginalCosts: " + mSpreadEOM + " < " + marginalCosts);
        }
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        float capacity = storageEnergyTradeHistory.getCapacity(currentDate);

        float tFullLoad = capacity * (socMax - soc) / dischargePower;     // 5.43
        float fEmpty = capacity * (soc - socMin); // 5.44


        //eomMarketOperator.addSupply(new EnergySupply(marginalCosts * 1.1f, soc, this));
        //eomMarketOperator.addDemand(new EnergyDemand(marginalCosts * 0.9f, energyCapacity - soc, this));

    }

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.DayInterval, priority = SequenceDefinition.BalancingMarketBidPriority)
    public void calculateEOMActionsForDay() {
        long startTick = TimeUtil.getCurrentTick();
        float dischargeEnergy = soc - socMin;
        float chargeEnergy = socMax - soc;
        int ticksToMinEnergy = (int) Math.floor(dischargeEnergy / (dischargePower * TimeUtil.HOUR_PER_TICK));
        int ticksToMaxEnergy = (int) Math.floor(chargeEnergy / (chargePower * TimeUtil.HOUR_PER_TICK));

        List<Long> ticksWithLowPrice = priceForwardCurve.getTicksWithLowestPrices(ticksToMinEnergy, startTick, SequenceDefinition.DayInterval);
        float priceForwardDayMax = priceForwardCurve.getMaximum(TimeUtil.getCurrentTick(), SequenceDefinition.DayInterval);

    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        int assignedQuantity = (int) (bid.getQuantity() * rate);
        if (bid instanceof EnergyDemand) {
            soc += assignedQuantity;
            storageEnergyTradeHistory.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        } else if (bid instanceof EnergySupply) {
            soc -= assignedQuantity;
            storageEnergyTradeHistory.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        } else if (bid instanceof PowerPositive || bid instanceof PowerNegative){
            storagePowerTradeHistory.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());

        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (soc < socMin || soc > socMax) {
            throw new IllegalStateException("batterylevel exceeds energyCapacity: " + soc + ", MaxLevel: " + energyCapacity);
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
        List<TradeRegistryImpl.EnergyTradeElement> energyTradeElements = storageEnergyTradeHistory.getEnergyTradeElements(TimeUtil.getCurrentDate());
        List<TradeRegistryImpl.EnergyTradeElement> powerTradeElements = storagePowerTradeHistory.getEnergyTradeElements(TimeUtil.getCurrentDate());

        List<TradeRegistryImpl.EnergyTradeElement> result = new ArrayList<>();
        result.addAll(energyTradeElements);
        result.addAll(powerTradeElements);
        return result;
    }

    @Override
    public String getName() {
        return name;
    }

    public float getSoc() {
        return soc;
    }

    @Override
    public void setBalancingMarketOperator(final BalancingMarketOperator balancingMarketOperator) {
        this.balancingMarketOperator = balancingMarketOperator;
    }

    @Override
    public String getDescription() {
        return description;
    }

    void setSOC(float v) {
      soc = v;
    }
}
