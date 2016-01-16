package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SimpleEnergyProducer implements EOMTrader {

    private final String name;
    private EOMOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private int lastQuantity;

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
        this.lastQuantity = bid.getQuantity();
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeHistoryImpl.EnergyTradeHistoryElement> getCurrentAssignments() {
        return Collections.singletonList(new EnergyTradeHistoryImpl.EnergyTradeHistoryElement(lastClearedPrice, TimeUtilities.getCurrentTick(), lastQuantity, 2000));
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }


    @Override
    public String getName() {
        return name;
    }
}
