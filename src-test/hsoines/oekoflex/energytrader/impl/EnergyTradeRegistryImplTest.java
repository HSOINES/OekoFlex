package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 22:02
 */
public class EnergyTradeRegistryImplTest {

    public static final int INITIALCAPACITY = 1000;
    private EnergyTradeRegistryImpl energyTradeRegistry;

    private Date date0;
    private Date date1;
    private Date date2;
    private Date date3;

    @Before
    public void setUp() throws Exception {
        date0 = TimeUtil.getDate(0);
        date1 = TimeUtil.getDate(1);
        date2 = TimeUtil.getDate(2);
        date3 = TimeUtil.getDate(3);
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.CONSUM, INITIALCAPACITY);
    }

    @Test
    public void testAssignmentQuarterHour() throws Exception {
        energyTradeRegistry.addAssignedQuantity(date0, Market.EOM_MARKET, 1f, 1f, 100, 1f);
        energyTradeRegistry.addAssignedQuantity(date1, Market.EOM_MARKET, 1f, 1f, 100, 1f);
        energyTradeRegistry.addAssignedQuantity(date1, Market.EOM_MARKET, 1f, 1f, 100, 1f);
        assertEquals(100, energyTradeRegistry.getEnergyUsed(date0));
        assertEquals(200, energyTradeRegistry.getEnergyUsed(date1));
    }

    @Test
    public void testAssignmentFourHours() throws Exception {
        energyTradeRegistry.addAssignedQuantity(date0, Market.REGELENERGIE_MARKET, 10f, 10f, 100, 1f);
        energyTradeRegistry.addAssignedQuantity(date1, Market.EOM_MARKET, 10f, 10f, 100, 1f);
        assertEquals(100, energyTradeRegistry.getEnergyUsed(date0));
        assertEquals(200, energyTradeRegistry.getEnergyUsed(date1));
        assertEquals(100, energyTradeRegistry.getEnergyUsed(date2));
        assertEquals(100, energyTradeRegistry.getEnergyUsed(date3));

        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date0).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date1).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date1).get(1).getAssignedPrice(), 0.00001);

        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date0, Market.EOM_MARKET));
        assertEquals(800, energyTradeRegistry.getRemainingCapacity(date1, Market.EOM_MARKET));
        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date2, Market.EOM_MARKET));
        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date3, Market.EOM_MARKET));

        assertEquals(800, energyTradeRegistry.getRemainingCapacity(date0, Market.REGELENERGIE_MARKET));
    }

    @Test
    public void testQuantityInRegistry() throws Exception {
        energyTradeRegistry.addAssignedQuantity(date0, Market.REGELENERGIE_MARKET, 10f, 10f, 100, 1f);
        List<EnergyTradeRegistryImpl.EnergyTradeElement> energyTradeElements = energyTradeRegistry.getEnergyTradeElements(date0);
        assertEquals(INITIALCAPACITY, energyTradeElements.get(0).getCapacity());

    }
}