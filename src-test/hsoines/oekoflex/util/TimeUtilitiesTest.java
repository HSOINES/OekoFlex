package hsoines.oekoflex.util;

import org.junit.Test;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.essentials.RepastEssentials;

import java.util.Date;

import static org.junit.Assert.*;

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
}