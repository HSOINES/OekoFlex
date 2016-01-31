package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.NegativeSupply;
import hsoines.oekoflex.bid.PositiveSupply;
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
    private final int powerMax;
    private final int powerMin;
    private final float shutdownCosts;
    private final int rampUp;
    private final int rampDown;
    private final float marginalCosts;
    private EOMOperator eomMarketOperator;
    private final EnergyTradeRegistry positiveEnergyTradeRegistry;
    private final EnergyTradeRegistry negativeEnergyTradeRegistry;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private float lastAssignmentRate;
    private float lastClearedPrice;

    public FlexPowerplant(final String name, final int powerMax, final int powerMin, final int rampUp, final int rampDown, final float marginalCosts, final float shutdownCosts) {
        this.name = name;
        this.powerMax = powerMax;
        this.powerMin = powerMin;
        this.rampUp = rampUp;
        this.rampDown = rampDown;
        this.marginalCosts = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        positiveEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, powerMax);
        negativeEnergyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, powerMax);
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.eomMarketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);
        int pCommitedNegativ = negativeEnergyTradeRegistry.getQuantityUsed(currentDate);
        int pPreceding = positiveEnergyTradeRegistry.getQuantityUsed(precedingDate) - negativeEnergyTradeRegistry.getQuantityUsed(precedingDate);  //todo: preceding power calculated by positive and negative energy traded???
        int mustRun = 0;
        if (pPreceding > rampUp) {
            mustRun = Math.min(powerMin + pCommitedNegativ, pPreceding - rampDown);
        }
        eomMarketOperator.addSupply(new PositiveSupply(marginalCosts, mustRun, this));
//        eomMarketOperator.addSupply(new PositiveSupply(marginalCosts * 1.5f, powerMax - mustRun - pCommitedNegativ, this));    //???
    }

    @Override
    public void makeBidRegelenergie() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        int pPreceding = positiveEnergyTradeRegistry.getQuantityUsed(precedingDate) - negativeEnergyTradeRegistry.getQuantityUsed(precedingDate);  //todo: preceding power calculated by positive and negative energy traded???

        //test impl
        int pNeg = Math.abs(Math.min(pPreceding - powerMin, rampDown));
        regelenergieMarketOperator.addNegativeSupply(new NegativeSupply(marginalCosts, pNeg, this));

        int pPos = Math.abs(Math.min(powerMax - pPreceding, rampUp));
        regelenergieMarketOperator.addPositiveSupply(new PositiveSupply(marginalCosts, pPos, this));
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

}
