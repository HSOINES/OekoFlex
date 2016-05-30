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
 * <p>
 * soc: state of charge, in percent
 */
public final class SimpleStorage implements EOMTrader, BalancingMarketTrader {
    private static final Log log = LogFactory.getLog(SimpleStorage.class);

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
    private TickStrategy tickStrategy;
    private final List<Integer> sellTicks;


    public SimpleStorage(final String name, final String description,
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
        sellTicks = new ArrayList<>();
        init();
    }

    public void init() {
        storageEnergyTradeHistory = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, energyCapacity, 1000);
        soc = socMin;
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.eomMarketOperator = spotMarketOperator;
    }

    @Override
    public void makeBidBalancingMarket() {
        makeBidBalancingMarket(TimeUtil.getCurrentTick());
    }

    @Override
    public void makeBidBalancingMarket(final long currentTick) {

    }

    @Override
    public void makeBidEOM() {
        Long currentTick = TimeUtil.getCurrentTick();
        makeBidEOM(currentTick);
    }

    @Override
    public void makeBidEOM(final long currentTick) {
        priceForwardCurve.getTicksWithHighestPrices(24, currentTick, 12);


        if (tickStrategy.getTicksWithHighestPrice().contains(currentTick)) {
            eomMarketOperator.addSupply(new EnergySupply(-3000f, dischargePower * TimeUtil.HOUR_PER_TICK, this));
        }
        if (tickStrategy.getTicksWithLowestPrice().contains(currentTick)) {
            eomMarketOperator.addDemand(new EnergyDemand(3000f, chargePower * TimeUtil.HOUR_PER_TICK, this));
        }
    }

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.DayInterval, priority = SequenceDefinition.BalancingMarketBidPriority)
    public void calculateEOMActionsForDay() {
        long startTick = TimeUtil.getCurrentTick();
        float dischargeEnergy = (soc - socMin) * energyCapacity;
        float chargeEnergy = (socMax - soc) * energyCapacity;
        int ticksToMinEnergy = (int) Math.floor(dischargeEnergy / (dischargePower * TimeUtil.HOUR_PER_TICK));
        int ticksToMaxEnergy = (int) Math.floor(chargeEnergy / (chargePower * TimeUtil.HOUR_PER_TICK));

        if (ticksToMaxEnergy > 48) ticksToMaxEnergy = 48;
        if (ticksToMinEnergy > 48) ticksToMinEnergy = 48;

        List<Long> ticksWithLowestPrice = priceForwardCurve.getTicksWithLowestPrices(ticksToMaxEnergy, startTick, SequenceDefinition.DayInterval);
        List<Long> ticksWithHighestPrice = priceForwardCurve.getTicksWithHighestPrices(ticksToMinEnergy, startTick, SequenceDefinition.DayInterval);

        tickStrategy = new TickStrategy(ticksWithLowestPrice, ticksWithHighestPrice);
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        float assignedQuantity = bid.getQuantity() * rate;
        if (bid instanceof EnergyDemand) {
            soc += assignedQuantity / energyCapacity;
            storageEnergyTradeHistory.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        } else if (bid instanceof EnergySupply) {
            soc -= assignedQuantity / energyCapacity;
            storageEnergyTradeHistory.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        } else if (bid instanceof PowerPositive || bid instanceof PowerNegative) {
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

    private class TickStrategy {
        private final List<Long> ticksWithLowestPrice;
        private final List<Long> ticksWithHighestPrice;

        public List<Long> getTicksWithLowestPrice() {
            return ticksWithLowestPrice;
        }

        public List<Long> getTicksWithHighestPrice() {
            return ticksWithHighestPrice;
        }

        public TickStrategy(List<Long> ticksWithLowestPrice, List<Long> ticksWithHighestPrice) {
            this.ticksWithLowestPrice = ticksWithLowestPrice;
            this.ticksWithHighestPrice = ticksWithHighestPrice;
        }
    }
}