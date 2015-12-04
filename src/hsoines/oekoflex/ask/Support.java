package hsoines.oekoflex.ask;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:27
 */
public final class Support {
    private final float price;
    private final int quantity;

    public float getPrice() {
        return price;
    }

    public float getQuantity() {
        return quantity;
    }

    public Support(float price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }

    public static class AscendingComparator implements Comparator<Support> {
        @Override
        public int compare(Support o1, Support o2) {
                return Float.compare(o1.getPrice(), o2.getPrice());
        }
    }

}
