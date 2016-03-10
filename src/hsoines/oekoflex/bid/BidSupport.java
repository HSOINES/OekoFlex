package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

import java.util.Comparator;

/**
 * User: jh
 * Date: 22/01/16
 * Time: 22:27
 */
public abstract class BidSupport implements Bid {
    protected final float price;
    private final float quantity;
    private final MarketOperatorListener marketOperatorListener;

    public BidSupport(final float price, final float quantity, final MarketOperatorListener marketOperatorListener) {
        this.price = price;
        this.quantity = quantity;
        this.marketOperatorListener = marketOperatorListener;
    }

    @Override
    public MarketOperatorListener getMarketOperatorListener() {
        return marketOperatorListener;
    }

    @Override
    public float getPrice() {
         return price;
     }

     @Override
     public float getQuantity() {
         return quantity;
     }


    public static class SupplySorter implements Comparator<Bid> {

        @Override
        public int compare(Bid o1, Bid o2) {
            final int compare = Float.compare(o1.getPrice(), o2.getPrice());
            if (compare == 0) {
                return Float.compare(o2.getQuantity(), o1.getQuantity());
            }
            return compare;
        }
    }

    public static class DemandSorter implements Comparator<Bid> {
        @Override
        public int compare(Bid o1, Bid o2) {
            final int compare = Float.compare(o2.getPrice(), o1.getPrice());
            if (compare == 0) {
                return Float.compare(o2.getQuantity(), o1.getQuantity());
            }
            return compare;
        }
    }
}
