package hsoines.oekoflex.util;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 13:52
 */
public enum Market {
    EOM_MARKET(1), REGELENERGIE_MARKET(16);

    private final int ticks;

    Market(final int ticks) {
        this.ticks = ticks;
    }

    public int getTicks() {
        return ticks;
    }
}
