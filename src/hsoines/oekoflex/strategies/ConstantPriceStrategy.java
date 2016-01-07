package hsoines.oekoflex.strategies;

import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 17:37
 */
public final class ConstantPriceStrategy implements PriceStrategy {
    private final float price;

    public ConstantPriceStrategy(final float price) {
        this.price = price;
    }

    @Override
    public float getPrice(final Date date) {
        return price;
    }

    ;
}
