package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.EnergyOnlyMarketOperator;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EnergyOnlyMarketTrader;
import hsoines.oekoflex.energytrader.EnergyProducer;
import hsoines.oekoflex.util.TimeUtilities;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class ParametrizableEnergyProducer implements EnergyProducer, MarketOperatorListener, EnergyOnlyMarketTrader {

    private final String name;
    private EnergyOnlyMarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float bidPrice;
    private final int supplyCapacity;
    private final int supplyDelay;
    private float bidQuantity;

    public ParametrizableEnergyProducer(String name) {
        this.name = name;
        Parameters p = RunEnvironment.getInstance().getParameters();

        supplyCapacity = (int) p.getValue("supplyCapacityAgent1");
        supplyDelay = (int) p.getValue("supplyDelayAgent1");

        bidPrice = (float)Math.random() * 500;
        bidQuantity = 200;
    }

    public void makeSupply() {
        if (lastAssignmentRate > .5) {
            bidPrice += 10;
            bidQuantity += supplyDelay;
            if (bidQuantity > supplyCapacity) {
                bidQuantity = supplyCapacity;
            }
        } else {
            bidPrice -= 10;            
            bidQuantity -= supplyDelay;
            if (bidQuantity < 20) {
            	bidQuantity = 20;
            }
        }
        marketOperator.addSupply(new Supply(bidPrice, bidQuantity, this, TimeUtilities.getCurrentDate()));
    }

    @Override
    public void setEnergieOnlyMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
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
