package hsoines.oekoflex.ask;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:27
 */
public final class Ask {
    private final float price;
    private final float amount;

    public float getPrice() {
        return price;
    }

    public float getAmount() {
        return amount;
    }

    public Ask(float price, float amount) {
        this.price = price;
        this.amount = amount;
    }
}
