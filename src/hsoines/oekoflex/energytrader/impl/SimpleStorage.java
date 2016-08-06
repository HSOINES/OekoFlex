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
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 16:14
 * <p>
 * soc: state of charge, in percent
 * Test-Storage
 */
public final class SimpleStorage implements EOMTrader, BalancingMarketTrader {
    private static final Log log = LogFactory.getLog(SimpleStorage.class);

    private final String name;
    private final String description;
    private final float marginalCosts;
    private final float shutdownCosts;
    private final float energyCapacity;
    private final float socMax;
    private final float socMin;
    private final int chargePower;
    private final int dischargePower;
    private final PriceForwardCurve priceForwardCurve;
    private final boolean shuffle;
    private float soc;
    private SpotMarketOperator eomMarketOperator;
    private TradeRegistry storageEnergyTradeHistory;
    private float lastAssignmentRate;
    private float lastClearedPrice;
    private BalancingMarketOperator balancingMarketOperator;
    private TradeRegistryImpl storagePowerTradeHistory;
    private TickStrategy tickStrategy;
    private final List<Long> sellTicks;


    public SimpleStorage(final String name, final String description,
                         final float marginalCosts, final float shutdownCosts,
                         final int energyCapacity, final float socMax, final float socMin,
                         final int chargePower, final int dischargePower,
                         PriceForwardCurve priceForwardCurve,
                         boolean shuffle) {
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
        this.shuffle = shuffle;
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
        if (sellTicks.contains(currentTick)) {
            //todo nur die menge verkaufen, die auch eingekauft wurde! dies kann an der oberen grenze variieren.
            float dischargeEnergy = dischargePower * .25f;//sollte nicht noetig sein: Math.min(dischargePower * .25f, (soc - socMin)*energyCapacity);
            if (soc - dischargeEnergy / energyCapacity > socMin) {
                eomMarketOperator.addSupply(new EnergySupply(-3000, dischargeEnergy, this));
            }
            sellTicks.remove(currentTick);
        } else if (soc + chargePower * 0.25 / energyCapacity < socMax) {
            float minMargin = .5f;  //todo: parameter
            final List<Long> ticksWithHighestPrices = priceForwardCurve.getTicksWithHighestPrices(24, currentTick, 96); //todo: 24/96 ?
            if (shuffle) Collections.shuffle(ticksWithHighestPrices);   // todo
            for (Long tickWithHighestPrice : ticksWithHighestPrices) {
                float priceOnSellTick = priceForwardCurve.getPriceOnTick(tickWithHighestPrice);
                if (priceOnSellTick - minMargin > priceForwardCurve.getPriceOnTick(currentTick)) {
                    if (!sellTicks.contains(tickWithHighestPrice)) {
                        sellTicks.add(tickWithHighestPrice);
                        float chargeEnergy = chargePower * .25f;
                        eomMarketOperator.addDemand(new EnergyDemand(3000, chargeEnergy, this));
                        break;
                    }
                }
            }
        } else {
            log.info("soc boundary: " + soc);
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
            throw new IllegalStateException("not used");
        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (soc < socMin || soc > socMax) {
            throw new IllegalStateException("batterylevel exceeds energyCapacity: " + soc + ", MaxLevel: " + energyCapacity + ", assigned: " + assignedQuantity);
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
        //List<TradeRegistryImpl.EnergyTradeElement> powerTradeElements = storagePowerTradeHistory.getEnergyTradeElements(TimeUtil.getCurrentDate());

        //List<TradeRegistryImpl.EnergyTradeElement> result = new ArrayList<>();
        //result.addAll(energyTradeElements);
        //result.addAll(powerTradeElements);
        return energyTradeElements;
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
    public float getCurrentPower() {
        throw new IllegalStateException("not implemented");
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
