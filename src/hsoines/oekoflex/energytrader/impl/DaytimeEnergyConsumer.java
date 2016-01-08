package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.strategies.DaytimePriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.summary.BidSummary;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class DaytimeEnergyConsumer implements MarketOperatorListener, EOMTrader {
    private final String name;
    private final int quantity;
    private EnergyOnlyMarketOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private EnergySlotList consumeSlots;
    private final PriceStrategy priceStrategy;
    private BidSummary bidSummary;

    public DaytimeEnergyConsumer(String name, int quantity, float priceAtDay, float decreaseAtNight) {
        this.name = name;
        this.quantity = quantity;
        priceStrategy = new DaytimePriceStrategy(priceAtDay, decreaseAtNight);
        consumeSlots = new EnergySlotListImpl(EnergySlotList.SlotType.CONSUM, 500);
    }

    @Override
    public void setEnergieOnlyMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
    
    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
    	if (marketOperator != null){
            lastBidPrice = priceStrategy.getPrice(date);
            int offeredQuantity = consumeSlots.addOfferedQuantity(date, quantity, EnergyTimeZone.QUARTER_HOUR);
            marketOperator.addDemand(new Demand(lastBidPrice, offeredQuantity, this));
        }
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        Date date = TimeUtilities.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        consumeSlots.addAssignedQuantity(date, (int) Math.floor(rate * bid.getQuantity()));
        if (bidSummary != null) {
            bidSummary.add(clearedPrice, rate, bid, currentDate);
        }
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
    public String getName() {
        return name;
    }

    @Override
    public void setBidSummary(final BidSummary bidSummary) {
        this.bidSummary = bidSummary;
    }
}
