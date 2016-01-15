package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 22:02
 */
public class EnergyTradeHistoryImplTest {

    private EnergyTradeHistoryImpl energyTradeHistory;

    private Date date0;
    private Date date1;
    private Date date2;
    private Date date3;

    @Before
    public void setUp() throws Exception {
        date0 = TimeUtilities.getDate(0);
        date1 = TimeUtilities.getDate(1);
        date2 = TimeUtilities.getDate(2);
        date3 = TimeUtilities.getDate(3);
        energyTradeHistory = new EnergyTradeHistoryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.CONSUM, 1000);
    }

    @Test
    public void testAssignmentQuarterHour() throws Exception {
        energyTradeHistory.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 100, 10.0f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 100, 11.0f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 100, 12.0f);
        assertEquals(100, energyTradeHistory.getEnergyUsed(date0));
        assertEquals(200, energyTradeHistory.getEnergyUsed(date1));
    }

    @Test
    public void testAssignmentFourHours() throws Exception {
        energyTradeHistory.addAssignedQuantity(date0, Duration.FOUR_HOURS, 100, 10.0f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 100, 20.0f);
        assertEquals(100, energyTradeHistory.getEnergyUsed(date0));
        assertEquals(200, energyTradeHistory.getEnergyUsed(date1));
        assertEquals(100, energyTradeHistory.getEnergyUsed(date2));
        assertEquals(100, energyTradeHistory.getEnergyUsed(date3));

        assertEquals(10f, energyTradeHistory.getHistoryElements(date0).get(0).getPrice(), 0.00001);
        assertEquals(10f, energyTradeHistory.getHistoryElements(date1).get(0).getPrice(), 0.00001);
        assertEquals(10f, energyTradeHistory.getHistoryElements(date1).get(1).getPrice(), 0.00001);

        assertEquals(900, energyTradeHistory.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
        assertEquals(800, energyTradeHistory.getRemainingCapacity(date1, Duration.QUARTER_HOUR));
        assertEquals(900, energyTradeHistory.getRemainingCapacity(date2, Duration.QUARTER_HOUR));
        assertEquals(900, energyTradeHistory.getRemainingCapacity(date3, Duration.QUARTER_HOUR));

        assertEquals(800, energyTradeHistory.getRemainingCapacity(date0, Duration.FOUR_HOURS));
    }
}