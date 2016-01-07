package hsoines.oekoflex.util;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 13:52
 */
public enum EnergyTimeZone {
    QUARTER_HOUR(1), FOUR_HOURS(16);

    private final int ticks;

    EnergyTimeZone(final int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
