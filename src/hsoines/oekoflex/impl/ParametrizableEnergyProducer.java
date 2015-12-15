package hsoines.oekoflex.impl;

import hsoines.oekoflex.*;
import hsoines.oekoflex.supply.Supply;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;

public class ParametrizableEnergyProducer implements EnergyProducer, MarketOperatorListener, OekoflexAgent {

    private final String name;
    private MarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float bidPrice;
    private final float supplyCapacity;
    private final float supplyDelay;
    private float bidQuantity;

    public ParametrizableEnergyProducer(String name) {
        this.name = name;
        Parameters p = RunEnvironment.getInstance().getParameters();

        supplyCapacity = (float) p.getValue("supplyCapacityAgent1");
        supplyDelay = (float) p.getValue("supplyDelayAgent1");

        bidPrice = (float) (1000 * Math.random());
        bidQuantity = (int) (supplyCapacity * Math.random());
    }

    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void makeBid() {
        if (lastAssignmentRate > .5) {
            bidPrice += 10;
            bidQuantity += supplyDelay;
            if (bidQuantity > supplyCapacity) {
                bidQuantity = supplyCapacity;
            }
        } else {
            bidPrice -= 10;
            bidQuantity -= supplyDelay;
            if (bidQuantity < 0) {
                return;
            }
        }
        marketOperator.addSupply(new Supply(bidPrice, bidQuantity, this));
    }

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
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
        return bidPrice;
    }

    @Override
    public String getName() {
        return name;
    }
}
