package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

import java.util.Comparator;

/**
 * User: jh
 * Date: 22/01/16
 * Time: 22:27
 */
public abstract class Supply implements Bid {
    protected final float price;
    private final int quantity;
    private final MarketOperatorListener marketOperatorListener;

    public Supply(final float price, final int quantity, final MarketOperatorListener marketOperatorListener) {
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
     public int getQuantity() {
         return quantity;
     }


    public static class AscendingComparator implements Comparator<PositiveSupply> {

        @Override
        public int compare(PositiveSupply o1, PositiveSupply o2) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }
}
