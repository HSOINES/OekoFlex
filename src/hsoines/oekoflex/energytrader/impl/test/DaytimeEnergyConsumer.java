package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.energytrader.impl.EnergyTradeRegistryImpl;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.strategies.DaytimePriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

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
        Date date = TimeUtil.getCurrentDate();
        if (marketOperator != null) {
            lastBidPrice = priceStrategy.getPrice(date);
            int offeredQuantity = energyTradeRegistry.getRemainingCapacity(date, Market.EOM_MARKET);
            marketOperator.addDemand(new Demand(lastBidPrice, offeredQuantity, this));
        }
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        Date date = TimeUtil.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        this.lastQuantity = bid.getQuantity();
        energyTradeRegistry.addAssignedQuantity(date, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate);
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
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
