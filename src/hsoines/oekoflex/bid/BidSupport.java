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
    
    /***
     * 
     * @param price		price of this power in [Euro/MW] or energy in [Euro/MWh]
	 * @param quantity	amount of power in [MW] or energy in [Euro/MWh]
	 * @param marketOperatorListener listener of a market which listens to this specific bid -> energy only listener
     */
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

    /**
     *  Sorts by price, in case prices are the same sorts by quantity
     *  smaller prices before bigger ones, if both prices are the same the bigger quantity is first
     */
    public static class SupplyComparator implements Comparator<Bid> {

        @Override
        public int compare(Bid o1, Bid o2) {
            final int compare = Float.compare(o1.getPrice(), o2.getPrice());
            if (compare == 0) {
                return Float.compare(o2.getQuantity(), o1.getQuantity());
            }
            return compare;
        }
    }

    /**
     * 
     *
     */
    public static class DemandComparator implements Comparator<Bid> {
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
