package hsoines.oekoflex.strategies;

import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 21:07
 */
public final class VaryWithAssignmentPriceStrategy implements PriceStrategy {
    private final float startPrice;

    public VaryWithAssignmentPriceStrategy(final float startPrice) {
        this.startPrice = startPrice;
    }

    @Override
    public float getPrice(final Date date) {
        return startPrice;
    }
}
