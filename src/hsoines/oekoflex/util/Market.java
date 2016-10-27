package hsoines.oekoflex.util;

/**
 * Definitionen zu MarktInfos
 */
public enum Market {
    SPOT_MARKET(1),       //EOM
    BALANCING_MARKET(16),  //Regelenergie
    START_VALUE(0);

    private final int ticks;

    Market(final int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }

    public float getDurationInHours() {
        return getTicks() * TimeUtil.HOUR_PER_TICK;
    }
}
