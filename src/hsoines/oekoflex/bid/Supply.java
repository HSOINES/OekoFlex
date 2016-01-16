package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.EOMOperatorListener;

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
    private final EOMOperatorListener marketOperatorListener;

    public Supply(float price, int quantity, EOMOperatorListener marketOperatorListener) {
        this.price = price;
        this.quantity = quantity;
        this.marketOperatorListener = marketOperatorListener;
    }

    @Override
    public EOMOperatorListener getMarketOperatorListener() {
        return marketOperatorListener;
    }

    @Override
    public void accept(final BidVisitor bidVisitor) {
        //todo
    }

    @Override
    public float getPrice() {
         return price;
     }

     @Override
     public int getQuantity() {
         return quantity;
     }


    @Override
    public String getTypeString() {
        return "Supply";
    }

    public static class AscendingComparator implements Comparator<Supply> {
        @Override
        public int compare(Supply o1, Supply o2) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }

}
