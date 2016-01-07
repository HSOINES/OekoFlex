package hsoines.oekoflex.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.essentials.RepastEssentials;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 07:58
 */
public final class TimeUtilities {
    private static final Log log = LogFactory.getLog(TimeUtilities.class);

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    public static boolean isEnergyTimeZone(EnergyTimeZone energyTimeZone) {
        long tick = getTick(getCurrentDate());
        return isEnergyTimeZone(energyTimeZone, tick);
    }

    public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final int QUARTER_HOUR_IN_MILLIS = 15 * 60 * 1000;

    public static Date getCurrentDate(){
        double tickCount = RepastEssentials.GetTickCount();
        return getDate((long) tickCount);
    }

    public static Date getDate(long tickCount){
        return new Date((tickCount - 1) * QUARTER_HOUR_IN_MILLIS);
    }

    public static long getTick(final Date date) {
        long ticks = date.getTime() / QUARTER_HOUR_IN_MILLIS;
        return ticks + 1;
    }

    static boolean isEnergyTimeZone(final EnergyTimeZone energyTimeZone, final long tick) {
        switch (energyTimeZone) {
            case QUARTER_HOUR:
                return true;
            case FOUR_HOURS:
                return tick % 16 == 0;
            default:
                log.error("unknown EnergyTimeZone: " + energyTimeZone);
                return false;
        }
    }


}
