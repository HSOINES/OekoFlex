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
public class TimeUtilTest {
    public TimeUtilTest() {
        RepastTestInitializer.init();
    }

    @Test
    public void testConversion() throws Exception {
        int ticks = (int) (Math.random() * 100000);
        Date date = TimeUtil.getDate(ticks);
        long convertedTicks = TimeUtil.getTick(date);
        assertEquals(ticks, convertedTicks);
    }

    @Test
    public void testTimeZone() throws Exception {
        assertEquals(true, TimeUtil.isEnergyTimeZone(Market.EOM_MARKET, 0l));
        assertEquals(true, TimeUtil.isEnergyTimeZone(Market.EOM_MARKET, 1l));
        assertEquals(true, TimeUtil.isEnergyTimeZone(Market.REGELENERGIE_MARKET, 0l));
        assertEquals(true, TimeUtil.isEnergyTimeZone(Market.REGELENERGIE_MARKET, SequenceDefinition.RegelenergieMarketInterval));
        assertEquals(false, TimeUtil.isEnergyTimeZone(Market.REGELENERGIE_MARKET, 1l));
    }

    @Test
    public void testDateDifference() throws Exception {
        Date startDate = TimeUtil.dateFormat.parse("2016-01-01 00:00:00");
        long diffMillis = startDate.getTime();
        int quarterHours = (int) (diffMillis / TimeUtil.QUARTER_HOUR_IN_MILLIS);
        System.out.println(quarterHours);
    }

    @Test
    public void testDateStart() throws Exception {
        Date date = TimeUtil.dateFormat.parse("2015-12-31 23:45:00");   //tick = -1
        Date testDate = TimeUtil.getCurrentDate();
        assertEquals(date.getTime(), testDate.getTime());

        Date date2 = TimeUtil.dateFormat.parse("2016-01-01 04:00:00");

        assertEquals(16, TimeUtil.getTick(date2));
    }
}