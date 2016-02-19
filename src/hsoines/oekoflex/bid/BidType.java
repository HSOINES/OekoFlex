package hsoines.oekoflex.bid;

/**
 * User: jh
 * Date: 20/01/16
 * Time: 22:35
 */
public enum BidType {
    ENERGY_DEMAND(true), ENERGY_SUPPLY(false), ENERGY_SUPPLY_MUSTRUN(false), POWER_POSITIVE(true), POWER_NEGATIVE(false);

    private final boolean positive;

    BidType(final boolean positive) {
        this.positive = positive;
    }

    public boolean isPositive() {
        return positive;
    }
}
