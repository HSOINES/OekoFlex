package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 13:25
 */
public class EnergySlotListImplTest {

    private EnergyTradeHistoryImpl energySlotList;
    private Date date0;
    private Date date1;
    private Date date2;
    private Date date3;

    @Before
    public void setUp() throws Exception {
        energySlotList = new EnergyTradeHistoryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.PRODUCE, 1000);
        date0 = TimeUtilities.getDate(0);
        date1 = TimeUtilities.getDate(1);
        date2 = TimeUtilities.getDate(2);
        date3 = TimeUtilities.getDate(3);
    }

    @Test
    public void testSlotAssignCapacity() throws Exception {
        Date date = new Date(0);
        assertEquals(1000, energySlotList.getRemainingCapacity(date, Duration.QUARTER_HOUR));
        energySlotList.addAssignedQuantity(date, Duration.QUARTER_HOUR, 100, 12.3f);
        assertEquals(900, energySlotList.getRemainingCapacity(date, Duration.QUARTER_HOUR));
    }

    @Test
    public void testSlotAssigning() throws Exception {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 100, 12.3f);
        assertEquals(900, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
    }

    @Test(expected = IllegalStateException.class)
    public void testSlotAssignedException() {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 500, 12.3f);
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 500, 12.3f);
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 1, 12.3f);
    }

    @Test
    public void testSlotAssigning2() throws Exception {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 300, 12.3f);
        assertEquals(700, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 700, 12.3f);
        assertEquals(0, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
    }
}