package hsoines.oekoflex.supply;

import hsoines.oekoflex.Bid;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.MarketOperatorListenerProvider;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:27
 */
public final class Supply implements Bid, MarketOperatorListenerProvider {
    private final float price;
    private final int quantity;
    private final MarketOperatorListener marketOperatorListener;

    public Supply(float price, int quantity, MarketOperatorListener marketOperatorListener) {
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

    public static class AscendingComparator implements Comparator<Supply> {
        @Override
        public int compare(Supply o1, Supply o2) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }

}
