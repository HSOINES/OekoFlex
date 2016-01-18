package hsoines.oekoflex.domain;

/**
 * User: jh
 * Date: 08/01/16
 * Time: 12:25
 */
public final class SequenceDefinition {
    //Priorities
    public static final int RegelenergieMarketBidPriority = 100;
    public static final int RegelenergieMarketClearingPriority = 99;
    public static final int EOMBidPriority = 50;
    public static final int EOMClearingPriority = 49;
    public static final int ReportingPriority = 1;

    //Intervals
    public static final int RegelenergieMarketInterval = 16;
    public static final int EOMInterval = 1;

    //starts
    public static final int SimulationStart = 0;

}
