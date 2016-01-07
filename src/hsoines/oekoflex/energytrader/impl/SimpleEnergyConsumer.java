package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.*;
import hsoines.oekoflex.energytrader.EnergyConsumer;
import repast.simphony.engine.schedule.ScheduledMethod;
import hsoines.oekoflex.demand.Demand;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class SimpleEnergyConsumer implements EnergyConsumer, MarketOperatorListener, OekoflexAgent {
    private final String name;
    private EOMMarketOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;

    public SimpleEnergyConsumer(String name) {
        this.name = name;
    }

    @Override
    public void setMarketOperator(final EOMMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
    
    public void makeDemand(){
    	if (marketOperator != null){
            lastBidPrice = (float) (300f * Math.random()) + 500;
            marketOperator.addDemand(new Demand(lastBidPrice, (int)(100f * Math.random()), this));
    	}
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid) {
        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    public float getLastClearedPrice() {
        return clearedPrice;
    }

    public float getLastBidPrice() {
        return lastBidPrice;
    }


    @Override
    public String getName() {
        return name;
    }
}
