package hsoines.oekoflex.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:16
 */
public class TimeUtilitiesTest {
    @Test
    public void testConversion() throws Exception {
        int ticks = (int) (Math.random() * 100000);
        Date date = TimeUtilities.getDate(ticks);
        long convertedTicks = TimeUtilities.getTick(date);
        assertEquals(ticks, convertedTicks);
    }

    @Test
    public void testTimeZone() throws Exception {
        assertEquals(true, TimeUtilities.isEnergyTimeZone(TimeUtilities.EnergyTimeZone.QUARTER_HOUR, 0l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(TimeUtilities.EnergyTimeZone.QUARTER_HOUR, 1l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(TimeUtilities.EnergyTimeZone.FOUR_HOURS, 0l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(TimeUtilities.EnergyTimeZone.FOUR_HOURS, 16l));
        assertEquals(false, TimeUtilities.isEnergyTimeZone(TimeUtilities.EnergyTimeZone.FOUR_HOURS, 1l));

    }
}