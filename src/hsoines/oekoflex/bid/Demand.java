package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.EOMOperatorListener;

import java.util.Comparator;

public class Demand implements Bid, MarketOperatorListenerProvider {
    private final float price;
    private final int quantity;
    private final EOMOperatorListener marketOperatorListener;

    public Demand(float price, int quantity, EOMOperatorListener marketOperatorListener) {
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
    public String getTypeString() {
        return "Demand";
    }

    public void accept(BidVisitor bidVisitor) {
        bidVisitor.visit(this);
    }

    @Override
    public EOMOperatorListener getMarketOperatorListener() {
        return marketOperatorListener;
    }


    public static class DescendingComparator implements Comparator<Demand> {
        @Override
        public int compare(Demand o1, Demand o2) {
            return Float.compare(o2.getPrice(), o1.getPrice());
        }
    }
}
