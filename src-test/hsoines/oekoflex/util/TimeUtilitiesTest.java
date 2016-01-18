package hsoines.oekoflex.util;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:16
 */
public class TimeUtilitiesTest {
    public TimeUtilitiesTest() {
        RepastTestInitializer.init();
    }

    @Test
    public void testConversion() throws Exception {
        int ticks = (int) (Math.random() * 100000);
        Date date = TimeUtilities.getDate(ticks);
        long convertedTicks = TimeUtilities.getTick(date);
        assertEquals(ticks, convertedTicks);
    }

    @Test
    public void testTimeZone() throws Exception {
        assertEquals(true, TimeUtilities.isEnergyTimeZone(Market.EOM_MARKET, 0l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(Market.EOM_MARKET, 1l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(Market.REGELENERGIE_MARKET, 0l));
        assertEquals(true, TimeUtilities.isEnergyTimeZone(Market.REGELENERGIE_MARKET, SequenceDefinition.RegelenergieMarketInterval));
        assertEquals(false, TimeUtilities.isEnergyTimeZone(Market.REGELENERGIE_MARKET, 1l));
    }

    @Test
    public void testDateDifference() throws Exception {
        Date startDate = TimeUtilities.dateFormat.parse("2016-01-01 00:00:00");
        long diffMillis = startDate.getTime();
        int quarterHours = (int) (diffMillis / TimeUtilities.QUARTER_HOUR_IN_MILLIS);
        System.out.println(quarterHours);
    }

    @Test
    public void testDateStart() throws Exception {
        Date date = TimeUtilities.dateFormat.parse("2015-12-31 23:45:00");   //tick = -1
        Date testDate = TimeUtilities.getCurrentDate();
        assertEquals(date.getTime(), testDate.getTime());

        Date date2 = TimeUtilities.dateFormat.parse("2016-01-01 04:00:00");

        assertEquals(16, TimeUtilities.getTick(date2));
    }
}