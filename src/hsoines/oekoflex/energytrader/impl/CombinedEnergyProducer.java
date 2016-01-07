package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.*;
import hsoines.oekoflex.energytrader.EnergyOnlyMarketTrader;
import hsoines.oekoflex.energytrader.EnergyProducer;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.supply.Supply;
import hsoines.oekoflex.util.TimeUtilities;

public class CombinedEnergyProducer implements EnergyProducer, MarketOperatorListener, OekoflexAgent, RegelenergieMarketTrader, EnergyOnlyMarketTrader {

    private final String name;
    private EnergyOnlyMarketOperator energyOnlyMarketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private RegelEnergieMarketOperator regelEnergieMarketOperator;

    public CombinedEnergyProducer(String name) {
        this.name = name;
    }

    @Override
    public void makeSupply(){
        if (TimeUtilities.getEnergyTimeZone() == TimeUtilities.EnergyTimeZone.FOUR_HOURS) {

        }
        lastBidPrice = (float) (300f * Math.random()) + 500;
        energyOnlyMarketOperator.addSupply(new Supply(lastBidPrice, (int) (100 * Math.random()), this));

    }

    @Override
    public void setMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.energyOnlyMarketOperator = marketOperator;
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public void setMarketOperator(final RegelEnergieMarketOperator marketOperator) {
        regelEnergieMarketOperator = marketOperator;
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }

    @Override
    public float getLastBidPrice() {
        return lastBidPrice;
    }

    @Override
    public String getName() {
        return name;
    }
}
