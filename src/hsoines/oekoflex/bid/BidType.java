package hsoines.oekoflex.bid;

/**
 * User: jh
 * Date: 20/01/16
 * Time: 22:35
 */
public enum BidType {
    ENERGY_DEMAND(false), 
    ENERGY_SUPPLY(true), ENERGY_SUPPLY_MUSTRUN(true), ENERGY_SUPPLY_MUSTRUN_COMPLEMENT(true), 
    POWER_POSITIVE(true), POWER_NEGATIVE(false), START_VALUE(true), NULL_BID(false);

    private final boolean positive;

    BidType(final boolean positive) {
        this.positive = positive;
    }
    public boolean isPositiveAmount() {
        return positive;
    }
}
