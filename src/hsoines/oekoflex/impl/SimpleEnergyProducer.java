package hsoines.oekoflex.impl;

import hsoines.oekoflex.EnergyProducer;
import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.bid.Bid;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.util.ContextUtils;

public class SimpleEnergyProducer implements EnergyProducer {

    private MarketOperator marketOperator;

    @ScheduledMethod(start = 1, interval = 1)
    public void sendBid(){
        Context<Object> context = ContextUtils.getContext(this);

        marketOperator.registerBid(new Bid(1234f, (float) (3000f * Math.random())));
    }

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
}
