package hsoines.oekoflex.bid;

import java.util.Comparator;

public class Bid {
    private final float price;
    private final int amount;

    public Bid(float price, int amount) {
        this.price = price;
        this.amount = amount;
    }

    public float getPrice() {
        return price;
    }

    public float getAmount() {
        return amount;
    }

    public static class AscendingComparator implements Comparator<Bid> {
        @Override
        public int compare(Bid o1, Bid o2) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }
}
