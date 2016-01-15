package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.summary.BidSummary;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class SimpleEnergyConsumer implements EOMTrader {
    private final String name;
    private EOMOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;

    public SimpleEnergyConsumer(String name) {
        this.name = name;
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.marketOperator = marketOperator;
    }

    public void makeBidEOM() {
        if (marketOperator != null){
            lastBidPrice = (float) (300f * Math.random()) + 500;
            marketOperator.addDemand(new Demand(lastBidPrice, (int) (100f * Math.random()), this));
        }
    }

    @Override
    public void notifyEOMClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
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
