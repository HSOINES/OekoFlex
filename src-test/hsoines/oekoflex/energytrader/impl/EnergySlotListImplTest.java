package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.util.EnergyTimeZone;
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

    private EnergySlotListImpl energySlotList;
    private Date date0;
    private Date date1;
    private Date date2;
    private Date date3;

    @Before
    public void setUp() throws Exception {
        energySlotList = new EnergySlotListImpl(EnergySlotList.SlotType.PRODUCE, 1000);
        date0 = TimeUtilities.getDate(0);
        date1 = TimeUtilities.getDate(1);
        date2 = TimeUtilities.getDate(2);
        date3 = TimeUtilities.getDate(3);
    }

    @Test
    public void testSlotAssignCapacity() throws Exception {
        Date date = new Date(0);
        assertEquals(1000, energySlotList.getSlotAssignCapacity(date));
        energySlotList.addAssignedQuantity(date, 100);
        assertEquals(900, energySlotList.getSlotAssignCapacity(date));
    }

    @Test
    public void testMultipleTicks() throws Exception {

        energySlotList.addOfferedQuantity(date0, 100);
        energySlotList.addOfferedQuantity(date1, 100);
        energySlotList.addOfferedQuantity(date2, 200);
        energySlotList.addOfferedQuantity(date3, 300);
        assertEquals(700, energySlotList.getSlotOfferCapacity(date0, EnergyTimeZone.FOUR_HOURS));
    }

    @Test
    public void testSlotAssigning() throws Exception {
        energySlotList.addAssignedQuantity(date0, 100);
        assertEquals(900, energySlotList.getSlotAssignCapacity(date0));
    }

    @Test(expected = IllegalStateException.class)
    public void testSlotAssignedException() {
        energySlotList.addAssignedQuantity(date0, 500);
        energySlotList.addAssignedQuantity(date0, 500);
        energySlotList.addAssignedQuantity(date0, 500);
    }
}