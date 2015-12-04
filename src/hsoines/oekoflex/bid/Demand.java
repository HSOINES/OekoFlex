package hsoines.oekoflex.bid;

import java.util.Comparator;

public class Demand {
    private final float price;
    private final int quantity;

    public Demand(float price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public static class DescendingComparator implements Comparator<Demand> {
        @Override
        public int compare(Demand o1, Demand o2) {
            return Float.compare(o2.getPrice(), o1.getPrice());
        }
    }
}
