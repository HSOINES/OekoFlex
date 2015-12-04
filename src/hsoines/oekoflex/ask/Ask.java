package hsoines.oekoflex.ask;

import hsoines.oekoflex.bid.Bid;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:27
 */
public final class Ask {
    private final float price;
    private final int amount;

    public float getPrice() {
        return price;
    }

    public float getAmount() {
        return amount;
    }

    public Ask(float price, int amount) {
        this.price = price;
        this.amount = amount;
    }

    public static class AscendingComparator implements Comparator<Ask> {
        @Override
        public int compare(Ask o1, Ask o2) {
                return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }

}
