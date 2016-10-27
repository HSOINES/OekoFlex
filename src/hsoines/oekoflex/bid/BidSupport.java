package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

import java.util.Comparator;

/**
 * 
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

    /**
     * @return the specific market listener, either EOM or BPM listener
     */
    @Override
    public MarketOperatorListener getMarketOperatorListener() {
        return marketOperatorListener;
    }
    
    /**
     * @return the price [Euro/MW] or [Euro/MWh] depending on type of bid
     */
    @Override
    public float getPrice() {
         return price;
     }
    
    /**
     * @return the amount/quantity [MW] or [MWh] depending on type of bid
     */
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
