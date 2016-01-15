package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.summary.BidSummary;

import java.util.Date;

public class SimpleEnergyProducer implements EOMTrader {

    private final String name;
    private EOMOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;

    public SimpleEnergyProducer(String name) {
        this.name = name;
    }

    @Override
    public void makeBidEOM() {
        lastBidPrice = (float) (300f * Math.random()) + 500;
        marketOperator.addSupply(new Supply(lastBidPrice, (int) (100 * Math.random()), this));
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
