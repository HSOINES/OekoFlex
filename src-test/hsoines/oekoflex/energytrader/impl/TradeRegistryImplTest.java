package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 22:02
 */
public class TradeRegistryImplTest {

    public static final int INITIALCAPACITY = 1000;
    private TradeRegistryImpl energyTradeRegistry;

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
        energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.CONSUM, INITIALCAPACITY, 1000);
    }

    @Test
    public void testAssignmentQuarterHour() throws Exception {
        energyTradeRegistry.addAssignedQuantity(date0, Market.SPOT_MARKET, 1f, 1f, 100, 1f, BidType.ENERGY_DEMAND);
        energyTradeRegistry.addAssignedQuantity(date1, Market.SPOT_MARKET, 1f, 1f, 100, 1f, BidType.ENERGY_DEMAND);
        energyTradeRegistry.addAssignedQuantity(date1, Market.SPOT_MARKET, 1f, 1f, 100, 1f, BidType.ENERGY_DEMAND);
        assertEquals(100, energyTradeRegistry.getNegativeQuantityUsed(date0), 0.00001f);
        assertEquals(200, energyTradeRegistry.getNegativeQuantityUsed(date1), 0.00001f);
    }

    @Test
    public void testAssignmentFourHours() throws Exception {
        energyTradeRegistry.addAssignedQuantity(date0, Market.BALANCING_MARKET, 10f, 10f, 100, 1f, BidType.ENERGY_DEMAND);
        energyTradeRegistry.addAssignedQuantity(date1, Market.SPOT_MARKET, 10f, 10f, 100, 1f, BidType.ENERGY_DEMAND);
        assertEquals(100, energyTradeRegistry.getNegativeQuantityUsed(date0), 0.00001f);
        assertEquals(200, energyTradeRegistry.getNegativeQuantityUsed(date1), 0.00001f);
        assertEquals(100, energyTradeRegistry.getNegativeQuantityUsed(date2), 0.00001f);
        assertEquals(100, energyTradeRegistry.getNegativeQuantityUsed(date3), 0.00001f);

        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date0).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date1).get(0).getAssignedPrice(), 0.00001);
        assertEquals(10f, energyTradeRegistry.getEnergyTradeElements(date1).get(1).getAssignedPrice(), 0.00001);

        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date0, Market.SPOT_MARKET), 0.00001f);
        assertEquals(800, energyTradeRegistry.getRemainingCapacity(date1, Market.SPOT_MARKET), 0.00001f);
        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date2, Market.SPOT_MARKET), 0.00001f);
        assertEquals(900, energyTradeRegistry.getRemainingCapacity(date3, Market.SPOT_MARKET), 0.00001f);

        assertEquals(800, energyTradeRegistry.getRemainingCapacity(date0, Market.BALANCING_MARKET), 0.00001f);
    }

}