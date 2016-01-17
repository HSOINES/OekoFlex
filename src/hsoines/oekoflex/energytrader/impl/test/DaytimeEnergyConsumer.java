package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.strategies.DaytimePriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;

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
    private EnergyTradeRegistry energyTradeRegistry;
    private final PriceStrategy priceStrategy;
    private int lastQuantity;

    public DaytimeEnergyConsumer(String name, int quantity, float priceAtDay, float decreaseAtNight) {
        this.name = name;
        this.quantity = quantity;
        priceStrategy = new DaytimePriceStrategy(priceAtDay, decreaseAtNight);
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.CONSUM, quantity);
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.marketOperator = marketOperator;
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
        if (marketOperator != null) {
            lastBidPrice = priceStrategy.getPrice(date);
            int offeredQuantity = energyTradeRegistry.getRemainingCapacity(date, Duration.QUARTER_HOUR);
            marketOperator.addDemand(new Demand(lastBidPrice, offeredQuantity, this));
        }
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate, final Duration duration) {
        Date date = TimeUtilities.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        this.lastQuantity = bid.getQuantity();
        energyTradeRegistry.addAssignedQuantity(date, Duration.QUARTER_HOUR, bid.getPrice(), clearedPrice, bid.getQuantity(), rate);
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return null;
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
