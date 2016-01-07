package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EnergyOnlyMarketTrader;
import hsoines.oekoflex.energytrader.EnergyProducer;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.summary.BidSummary;

import java.util.Date;

public class SimpleEnergyProducer implements EnergyProducer, MarketOperatorListener, EnergyOnlyMarketTrader {

    private final String name;
    private EnergyOnlyMarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;

    public SimpleEnergyProducer(String name) {
        this.name = name;
    }

    @Override
    public void makeSupply(){
        lastBidPrice = (float) (300f * Math.random()) + 500;
        marketOperator.addSupply(new Supply(lastBidPrice, (int) (100 * Math.random()), this));
    }

    @Override
    public void setEnergieOnlyMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
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
    public void setBidSummary(final BidSummary bidSummary) {

    }

    @Override
    public String getName() {
        return name;
    }
}
