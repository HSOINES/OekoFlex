package hsoines.oekoflex.impl;

import hsoines.oekoflex.Bid;
import hsoines.oekoflex.MarketOperatorListener;
import repast.simphony.engine.schedule.ScheduledMethod;
import hsoines.oekoflex.EnergyConsumer;
import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.bid.Demand;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class SimpleEnergyConsumer implements EnergyConsumer, MarketOperatorListener {
    private MarketOperator marketOperator;

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
    
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void makeAsk(){
    	if (marketOperator != null){
    		marketOperator.addDemand(new Demand(123f, 444, this));
    	}
    }

    @Override
    public void notifyExectionRate(final float rate, final Bid bid) {

    }
}
