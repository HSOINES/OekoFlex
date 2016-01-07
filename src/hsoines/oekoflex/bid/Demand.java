package hsoines.oekoflex.bid;

import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.MarketOperatorListenerProvider;

import java.util.Comparator;
import java.util.Date;

public class Demand implements Bid, MarketOperatorListenerProvider {
    private final float price;
    private final float quantity;
    private final MarketOperatorListener marketOperatorListener;
    private final Date date;

    public Demand(float price, float quantity, MarketOperatorListener marketOperatorListener, final Date date) {
        this.price = price;
        this.quantity = quantity;
        this.marketOperatorListener = marketOperatorListener;
        this.date = date;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    @Override
    public Date getDate() {
        return date;
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
