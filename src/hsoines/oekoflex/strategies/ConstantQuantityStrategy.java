package hsoines.oekoflex.strategies;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 17:39
 */
public final class ConstantQuantityStrategy implements QuantityStrategy {
    private final int quantity;

    public ConstantQuantityStrategy(final int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }
}
