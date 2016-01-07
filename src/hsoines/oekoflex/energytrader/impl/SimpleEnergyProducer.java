package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.*;
import hsoines.oekoflex.energytrader.EnergyProducer;
import hsoines.oekoflex.supply.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

public class SimpleEnergyProducer implements EnergyProducer, MarketOperatorListener, OekoflexAgent {

    private final String name;
    private EOMMarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;

    public SimpleEnergyProducer(String name) {
        this.name = name;
    }

    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void makeBid(){
        lastBidPrice = (float) (300f * Math.random()) + 500;
        marketOperator.addSupply(new Supply(lastBidPrice, (int) (100 * Math.random()), this));
    }

    @Override
    public void setMarketOperator(final EOMMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
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