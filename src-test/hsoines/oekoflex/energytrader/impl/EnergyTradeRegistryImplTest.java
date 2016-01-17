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
 * Date: 15/01/16
 * Time: 22:02
 */
public class EnergyTradeRegistryImplTest {

    private EnergyTradeRegistryImpl energyTradeHistory;

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
        energyTradeHistory = new EnergyTradeRegistryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.CONSUM, 1000);
    }

    @Test
    public void testAssignmentQuarterHour() throws Exception {
//        energyTradeHistory.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 100, 10.0f);
//        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 100, 11.0f);
//        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 100, 12.0f);
        energyTradeHistory.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 1f, 1f, 100, 1f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 1f, 1f, 100, 1f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 1f, 1f, 100, 1f);
        assertEquals(100, energyTradeHistory.getEnergyUsed(date0));
        assertEquals(200, energyTradeHistory.getEnergyUsed(date1));
    }

    @Test
    public void testAssignmentFourHours() throws Exception {
        energyTradeHistory.addAssignedQuantity(date0, Duration.FOUR_HOURS, 10f, 10f, 100, 1f);
        energyTradeHistory.addAssignedQuantity(date1, Duration.QUARTER_HOUR, 10f, 10f, 100, 1f);
        assertEquals(100, energyTradeHistory.getEnergyUsed(date0));
        assertEquals(200, energyTradeHistory.getEnergyUsed(date1));
        assertEquals(100, energyTradeHistory.getEnergyUsed(date2));
        assertEquals(100, energyTradeHistory.getEnergyUsed(date3));

        assertEquals(10f, energyTradeHistory.getEnergyTradeElements(date0).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeHistory.getEnergyTradeElements(date1).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeHistory.getEnergyTradeElements(date1).get(1).getAssignedPrice(), 0.00001);

        assertEquals(900, energyTradeHistory.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
        assertEquals(800, energyTradeHistory.getRemainingCapacity(date1, Duration.QUARTER_HOUR));
        assertEquals(900, energyTradeHistory.getRemainingCapacity(date2, Duration.QUARTER_HOUR));
        assertEquals(900, energyTradeHistory.getRemainingCapacity(date3, Duration.QUARTER_HOUR));

        assertEquals(800, energyTradeHistory.getRemainingCapacity(date0, Duration.FOUR_HOURS));
    }
}