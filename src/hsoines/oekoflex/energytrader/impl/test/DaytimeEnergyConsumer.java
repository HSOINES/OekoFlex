package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
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
    private final float quantity;
    private EOMOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private TradeRegistry tradeRegistry;
    private final PriceStrategy priceStrategy;
    private float lastQuantity;

    public DaytimeEnergyConsumer(String name, float quantity2, float priceAtDay, float decreaseAtNight) {
        this.name = name;
        this.quantity = quantity2;
        priceStrategy = new DaytimePriceStrategy(priceAtDay, decreaseAtNight);
        tradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.CONSUM, quantity2);
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.marketOperator = eomOperator;
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtil.getCurrentDate();
        if (marketOperator != null) {
            lastBidPrice = priceStrategy.getPrice(date);
            float offeredQuantity = tradeRegistry.getRemainingCapacity(date, Market.EOM_MARKET);
            marketOperator.addDemand(new EnergyDemand(lastBidPrice, offeredQuantity, this));
        }
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        Date date = TimeUtil.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        this.lastQuantity = bid.getQuantity();
        tradeRegistry.addAssignedQuantity(date, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return tradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
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
