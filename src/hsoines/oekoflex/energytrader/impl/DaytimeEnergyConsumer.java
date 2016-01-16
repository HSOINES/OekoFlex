package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.strategies.DaytimePriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class DaytimeEnergyConsumer implements EOMTrader {
    private final String name;
    private final int quantity;
    private EOMOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private hsoines.oekoflex.energytrader.EnergyTradeHistory energyTradeHistory;
    private final PriceStrategy priceStrategy;
    private int lastQuantity;

    public DaytimeEnergyConsumer(String name, int quantity, float priceAtDay, float decreaseAtNight) {
        this.name = name;
        this.quantity = quantity;
        priceStrategy = new DaytimePriceStrategy(priceAtDay, decreaseAtNight);
        energyTradeHistory = new EnergyTradeHistoryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.CONSUM, 500);
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
    
    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
    	if (marketOperator != null){
            lastBidPrice = priceStrategy.getPrice(date);
            int offeredQuantity = energyTradeHistory.getRemainingCapacity(date, Duration.QUARTER_HOUR);
            marketOperator.addDemand(new Demand(lastBidPrice, offeredQuantity, this));
        }
    }

    @Override
    public void notifyEOMClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        Date date = TimeUtilities.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        this.lastQuantity = bid.getQuantity();
        energyTradeHistory.addAssignedQuantity(date, Duration.QUARTER_HOUR, (int) (rate * bid.getQuantity()), clearedPrice);
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeHistoryImpl.EnergyTradeHistoryElement> getCurrentAssignments() {
        return Collections.singletonList(new EnergyTradeHistoryImpl.EnergyTradeHistoryElement(clearedPrice, TimeUtilities.getCurrentTick(), lastQuantity, 2000));
    }

    public float getLastClearedPrice() {
        return clearedPrice;
    }

    public float getLastBidPrice() {
        return lastBidPrice;
    }



    @Override
    public String getName() {
        return name;
    }

}
