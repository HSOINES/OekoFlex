package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
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
    private final int powerMax;
    private final int powerMin;
    private final float shutdownCosts;
    private final int powerRampUp;
    private final int powerRampDown;
    private final float marginalCosts;
    private EOMOperator eomMarketOperator;
    private final TradeRegistry energyTradeRegistry;
    private final TradeRegistry powerTradeRegistry;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public FlexPowerplant(final String name, final int powerMax, final int powerMin, final int powerRampUp, final int powerRampDown, final float marginalCosts, final float shutdownCosts) {
        this.name = name;
        this.powerMax = powerMax;
        this.powerMin = powerMin;
        this.powerRampUp = powerRampUp;
        this.powerRampDown = powerRampDown;
        this.marginalCosts = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax);
        powerTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax);
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);
        //todo: einzeln positiv und negativ betrachten
        int pCommited = powerTradeRegistry.getQuantityUsed(currentDate);
        float ePreceding = energyTradeRegistry.getQuantityUsed(precedingDate);
        float pMustRun = 0;
        if (ePreceding > powerRampDown * TimeUtil.HOUR_PER_TICK) {
            pMustRun = Math.min(powerMin + pCommited < 0 ? -pCommited : 0, ePreceding / TimeUtil.HOUR_PER_TICK - powerRampDown);
        }
        float eMustRun = pMustRun * TimeUtil.HOUR_PER_TICK;
        eomMarketOperator.addSupply(new EnergySupply(shutdownCosts / eMustRun, eMustRun, this)); 

        //einzeln positiv und negativ betrachten
        float pFlex = Math.min(powerMax - pCommited > 0 ? pCommited : 0 - pMustRun, ePreceding * TimeUtil.HOUR_PER_TICK + powerRampUp);
        eomMarketOperator.addSupply(new EnergySupply(marginalCosts, pFlex * TimeUtil.HOUR_PER_TICK, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        int pPreceding = (int) (energyTradeRegistry.getQuantityUsed(precedingDate) / TimeUtil.HOUR_PER_TICK);

        int pNeg = Math.abs(Math.min(pPreceding - powerMin, powerRampDown));
        regelenergieMarketOperator.addNegativeSupply(new PowerNegative(marginalCosts, pNeg, this));   //price???

        int pPos = Math.abs(Math.min(powerMax - pPreceding, powerRampUp));
        regelenergieMarketOperator.addPositiveSupply(new PowerPositive(marginalCosts, pPos, this));   //price???
    }

    @Override
    public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator regelenergieMarketOperator) {
        this.regelenergieMarketOperator = regelenergieMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        switch (bid.getBidType()) {
            case ENERGY_SUPPLY:
                energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
                break;
            case POWER_NEGATIVE:
            case POWER_POSITIVE:
                powerTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
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
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        List<TradeRegistryImpl.EnergyTradeElement> positiveEnergyTradeElements = energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        List<TradeRegistryImpl.EnergyTradeElement> negativeEnergyTradeElements = powerTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        positiveEnergyTradeElements.addAll(negativeEnergyTradeElements);
        return positiveEnergyTradeElements;
    }

    @Override
    public String getName() {
        return name;
    }

}
