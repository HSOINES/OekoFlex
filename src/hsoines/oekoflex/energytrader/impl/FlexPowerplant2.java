package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.*;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
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
 * <p>
 * marginalCosts: Euro/MWh
 * powerMin: Minimum Power of PowerPlant
 * powerRampUp/Down: MWh/15min
 */
public final class FlexPowerplant2 implements EOMTrader, BalancingMarketTrader, MarketOperatorListener {
    private static final Log log = LogFactory.getLog(FlexPowerplant2.class);

    static final float LATENCY = 3f;
    static final float FACTOR_BALANCING_CALL = .2f;

    private final String name;
    private final String description;
    private final int powerMax;
    private final int powerMin;
    private final float shutdownCosts;
    private final PriceForwardCurve priceForwardCurve;
    private final int powerRampUp;
    private final int powerRampDown;
    private final float marginalCosts;
    private SpotMarketOperator eomMarketOperator;
    private TradeRegistry energyTradeRegistry;
    private TradeRegistry powerTradeRegistry;
    private BalancingMarketOperator balancingMarketOperator;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public FlexPowerplant2(final String name, final String description,
                           final int powerMax, final int powerMin,
                           final int powerRampUp, final int powerRampDown,
                           final float marginalCosts, final float shutdownCosts,
                           final PriceForwardCurve priceForwardCurve) {
        this.name = name;
        this.description = description;
        this.powerMax = powerMax;
        this.powerMin = powerMin;
        this.powerRampUp = powerRampUp;
        this.powerRampDown = powerRampDown;
        this.marginalCosts = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        this.priceForwardCurve = priceForwardCurve;
        init();
    }

    public void init() {
        energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax, 1000, powerMin * TimeUtil.HOUR_PER_TICK);
        powerTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax, 1000);
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.eomMarketOperator = spotMarketOperator;
    }

    @Override
    public void makeBidBalancingMarket() {
        Date currentDate = TimeUtil.getCurrentDate();
        makeBidBalancingMarket(TimeUtil.getTick(currentDate));
    }

    @Override
    public void makeBidBalancingMarket(long currentTick) {
        Date currentDate = TimeUtil.getDate(currentTick);
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        float pPreceding = (energyTradeRegistry.getQuantityUsed(precedingDate) / TimeUtil.HOUR_PER_TICK);
        if (pPreceding - powerMin < -0.001f) {
            if (pPreceding < 0.001f) {
                log.info("powerplant stopped.");
                return;
            } else {
                throw new IllegalStateException("Power not in acceptable range: " + pPreceding + ". PowerMin: " + powerMin);
            }
        }

        float pfcCostsAverage = priceForwardCurve.getPriceSummation(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks()) / Market.BALANCING_MARKET.getTicks();
        final float durationInHours = Market.BALANCING_MARKET.getDurationInHours();

        float pNeg = Math.min(pPreceding - powerMin, powerRampDown / LATENCY);
        if (pNeg > 0) {
            final float priceNegative = -Math.min(((pfcCostsAverage - marginalCosts) * durationInHours * powerMin) / pNeg, 0)
                    - FACTOR_BALANCING_CALL * durationInHours * marginalCosts;
            balancingMarketOperator.addNegativeSupply(new PowerNegative(priceNegative, pNeg, this));
        }

        float pPos = Math.min(powerMax - pPreceding, powerRampUp / LATENCY);
        if (pPos > 0) {
            final float pricePositive = Math.max((pfcCostsAverage - marginalCosts) * durationInHours, 0)
                    - Math.min(((pfcCostsAverage - marginalCosts) * durationInHours * powerMin) / pPos, 0)
                    + FACTOR_BALANCING_CALL * durationInHours * marginalCosts;
            balancingMarketOperator.addPositiveSupply(new PowerPositive(pricePositive, pPos, this));
        }
    }

    @Override
    public void makeBidEOM() {
        long currentTick = TimeUtil.getCurrentTick();
        makeBidEOM(currentTick);
    }

    @Override
    public void makeBidEOM(long currentTick) {
        float t = TimeUtil.HOUR_PER_TICK;

        Date currentDate = TimeUtil.getDate(currentTick);
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        float pPositiveCommited = powerTradeRegistry.getPositiveQuantityUsed(currentDate);
        float pNegativeCommited = powerTradeRegistry.getNegativeQuantityUsed(currentDate);
        float ePreceding = energyTradeRegistry.getQuantityUsed(precedingDate);
        if (ePreceding / TimeUtil.HOUR_PER_TICK - powerMin < -0.001f) {
            if (ePreceding == 0) {
                ePreceding = (powerMin - powerRampUp) * TimeUtil.HOUR_PER_TICK;
                log.info("PowerPlant was down... trying start.");
            } else {
                throw new IllegalStateException("Power not in acceptable range: " + ePreceding / TimeUtil.HOUR_PER_TICK +
                        ". MinPower:  " + powerMin + ". Plant: " + getName());
            }
        }

        float eMustRun = Math.max((powerMin + pNegativeCommited) * t, ePreceding - powerRampDown * t);
        float priceMustRun = -shutdownCosts / eMustRun + marginalCosts;
        eomMarketOperator.addSupply(new EnergySupplyMustRun(priceMustRun, eMustRun, this));

        float eFlex = Math.min((powerMax - pPositiveCommited) * t, ePreceding + powerRampUp * t) - eMustRun;
        float priceFlex = marginalCosts;
        eomMarketOperator.addSupply(new EnergySupply(priceFlex, eFlex, this));
    }

    @Override
    public void setBalancingMarketOperator(final BalancingMarketOperator balancingMarketOperator) {
        this.balancingMarketOperator = balancingMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        switch (bid.getBidType()) {
            case ENERGY_SUPPLY_MUSTRUN:
                if (rate < 0.0001f) {
                    return;
                }
                if (1 - rate > 0.00001f) {
                    log.error("rate of MUSTRUN < 1: " + rate + ", Plant: " + getName());
                    energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), 0, bid.getQuantity(), 1 - rate, BidType.ENERGY_SUPPLY_MUSTRUN_COMPLEMENT);
                }
            case ENERGY_SUPPLY:
                energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
                break;
            case POWER_NEGATIVE:
            case POWER_POSITIVE:
                powerTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
                break;
        }
        if (market.equals(Market.SPOT_MARKET)) {
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
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        List<TradeRegistryImpl.EnergyTradeElement> powerTradeElements = powerTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        List<TradeRegistryImpl.EnergyTradeElement> energyTradeElements = energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        powerTradeElements.addAll(energyTradeElements);
        return powerTradeElements;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
