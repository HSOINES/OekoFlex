package hsoines.oekoflex.util;

import hsoines.oekoflex.domain.SequenceDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.essentials.RepastEssentials;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 07:58
 */
public final class TimeUtil {
    private static final Log log = LogFactory.getLog(TimeUtil.class);
    public static final float HOUR_PER_TICK = .25f;

    private static long quarterHoursUntilSimulationStart;
    public static final int QUARTER_HOUR_IN_MILLIS = 15 * 60 * 1000;
    public static Date startDate;
    public static OekoflexDateFormat dateFormat = new OekoflexDateFormat();

    private static long internalTick = Long.MIN_VALUE;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        try {
            startDate = TimeUtil.dateFormat.parse("2016-01-01 00:00:00");
        } catch (ParseException e) {
            log.error(e.toString(), e);
        }
        long diffMillis = startDate.getTime();
        quarterHoursUntilSimulationStart = (diffMillis / TimeUtil.QUARTER_HOUR_IN_MILLIS);
    }

    public static boolean isEnergyTimeZone(Market market) {
        long tick = getTick(getCurrentDate());
        return isEnergyTimeZone(market, tick);
    }

    public static Date getCurrentDate(){
        double tickCount;
        if (internalTick != Integer.MIN_VALUE) {
            tickCount = internalTick;
        } else {
            tickCount = RepastEssentials.GetTickCount();
        }
        return getDate((long) tickCount);
    }

    public static Date getDate(long tickCount){
        return new Date((tickCount + quarterHoursUntilSimulationStart) * QUARTER_HOUR_IN_MILLIS);
    }

    public static long getTick(final Date date) {
        long ticks = date.getTime() / QUARTER_HOUR_IN_MILLIS;
        return ticks - quarterHoursUntilSimulationStart;
    }

    static boolean isEnergyTimeZone(final Market market, final long tick) {
        switch (market) {
            case SPOT_MARKET:
                return true;
            case BALANCING_MARKET:
                return tick % SequenceDefinition.BalancingMarketInterval == 0;
            default:
                log.error("unknown EnergyTimeZone: " + market);
                return false;
        }
    }


    public static long getCurrentTick() {
        return getTick(getCurrentDate());
    }

    public static Date getDateWithMinutesOffset(final int minutesOffset) {
        Date date = new Date(startDate.getTime() + minutesOffset * 60 * 1000);
        return date;
    }

    public static Date precedingDate(final Date currentDate) {
        return new Date(currentDate.getTime() - QUARTER_HOUR_IN_MILLIS);
    }

    public static Date getSucceedingDate(final Date currentDate) {
        return new Date(currentDate.getTime() + QUARTER_HOUR_IN_MILLIS);
    }

    /*
    only for test purposes!
    simulation can be controlled by this methods

        usage:

        nextTick();
        dosomemarketclearing
        nextTick();

        in tearDown:
        reset();
     */

    public static void nextTick() {
        internalTick++;
    }

    public static void reset() {
        internalTick = Integer.MIN_VALUE;
    }

    public static void startAt(long start) {
        internalTick = start;
    }

}
