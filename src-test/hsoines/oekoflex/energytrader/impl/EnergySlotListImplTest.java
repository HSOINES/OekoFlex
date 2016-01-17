package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.impl.test.EnergyTradeRegistryImpl;
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

    private EnergyTradeRegistryImpl energySlotList;
    private Date date0;
    private Date date1;
    private Date date2;
    private Date date3;

    @Before
    public void setUp() throws Exception {
        energySlotList = new EnergyTradeRegistryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.PRODUCE, 1000);
        date0 = TimeUtilities.getDate(0);
        date1 = TimeUtilities.getDate(1);
        date2 = TimeUtilities.getDate(2);
        date3 = TimeUtilities.getDate(3);
    }

    @Test
    public void testSlotAssignCapacity() throws Exception {
        Date date = new Date(0);
        assertEquals(1000, energySlotList.getRemainingCapacity(date, Duration.QUARTER_HOUR));
        energySlotList.addAssignedQuantity(date, Duration.QUARTER_HOUR, 10f, 12.3f, 100, 1);
        assertEquals(900, energySlotList.getRemainingCapacity(date, Duration.QUARTER_HOUR));
    }

    @Test
    public void testSlotAssigning() throws Exception {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 12f, 100, 1);
        assertEquals(900, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
    }

    @Test(expected = IllegalStateException.class)
    public void testSlotAssignedException() {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 10f, 500, 1f);
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 10f, 500, 1f);
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 10f, 1, 1f);
    }

    @Test
    public void testSlotAssigning2() throws Exception {
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 10f, 300, 1f);
        assertEquals(700, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
        energySlotList.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 10f, 10f, 700, 1f);
        assertEquals(0, energySlotList.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
    }
}