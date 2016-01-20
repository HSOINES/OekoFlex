package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.impl.BidType;

import java.util.Comparator;

public class Demand implements Bid {
    private final float price;
    private final int quantity;
    private final MarketOperatorListener marketOperatorListener;

    public Demand(float price, int quantity, MarketOperatorListener marketOperatorListener) {
        this.price = price;
        this.quantity = quantity;
        this.marketOperatorListener = marketOperatorListener;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public BidType getBidType() {
        return BidType.DEMAND;
    }

    @Override
    public MarketOperatorListener getMarketOperatorListener() {
        return marketOperatorListener;
    }


    public static class DescendingComparator implements Comparator<Demand> {
        @Override
        public int compare(Demand o1, Demand o2) {
            return Float.compare(o2.getPrice(), o1.getPrice());
        }
    }
}
