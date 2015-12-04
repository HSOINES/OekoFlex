package hsoines.oekoflex.impl;

import hsoines.oekoflex.ask.Ask;
import repast.simphony.engine.schedule.ScheduledMethod;
import hsoines.oekoflex.EnergyConsumer;
import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.bid.Bid;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class SimpleEnergyConsumer implements EnergyConsumer {
    private MarketOperator marketOperator;

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
    
    @ScheduledMethod(start = 1, interval = 1)
    public void makeAsk(){
    	if (marketOperator != null){
    		marketOperator.addBid(new Bid(123f, 444 ));
    	}
    }
}
