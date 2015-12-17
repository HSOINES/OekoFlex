package hsoines.oekoflex.util;

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

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
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
}
