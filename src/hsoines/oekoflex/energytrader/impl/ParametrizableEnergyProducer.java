package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.summary.BidSummary;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.util.Date;

public class ParametrizableEnergyProducer implements EOMTrader {

    private final String name;
    private EOMOperator marketOperator;
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

    public void makeBidEOM() {
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
        marketOperator.addSupply(new Supply(bidPrice, bidQuantity, this));
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.marketOperator = marketOperator;
    }

    @Override
    public void notifyEOMClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
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
    public EnergyTradeHistory getProducedEnergyTradeHistory() {
        return null;
    }

    @Override
    public void setEOMBidSummary(final BidSummary bidSummary) {

    }

    @Override
    public String getName() {
        return name;
    }
}
