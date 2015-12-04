package hsoines.oekoflex.impl;

import hsoines.oekoflex.EnergyProducer;
import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.ask.Support;
import repast.simphony.engine.schedule.ScheduledMethod;

public class SimpleEnergyProducer implements EnergyProducer {

    private MarketOperator marketOperator;

    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void makeBid(){
        marketOperator.addSupport(new Support(1234f, (int) (3000 * Math.random())));
    }

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
}
