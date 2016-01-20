package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
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
        energySlotList = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, 1000);
        date0 = TimeUtil.getDate(0);
        date1 = TimeUtil.getDate(1);
        date2 = TimeUtil.getDate(2);
        date3 = TimeUtil.getDate(3);
    }

    @Test
    public void testSlotAssignCapacity() throws Exception {
        Date date = new Date(0);
        assertEquals(1000, energySlotList.getRemainingCapacity(date, Market.EOM_MARKET));
        energySlotList.addAssignedQuantity(date, Market.EOM_MARKET, 10f, 12.3f, 100, 1, BidType.DEMAND);
        assertEquals(900, energySlotList.getRemainingCapacity(date, Market.EOM_MARKET));
    }

    @Test
    public void testSlotAssigning() throws Exception {
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 12f, 100, 1, BidType.DEMAND);
        assertEquals(900, energySlotList.getRemainingCapacity(date0, Market.EOM_MARKET));
    }

    @Test(expected = IllegalStateException.class)
    public void testSlotAssignedException() {
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 10f, 500, 1f, BidType.DEMAND);
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 10f, 500, 1f, BidType.DEMAND);
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 10f, 1, 1f, BidType.DEMAND);
    }

    @Test
    public void testSlotAssigning2() throws Exception {
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 10f, 300, 1f, BidType.DEMAND);
        assertEquals(700, energySlotList.getRemainingCapacity(date0, Market.EOM_MARKET));
        energySlotList.addAssignedQuantity(date0, Market.EOM_MARKET, 10f, 10f, 700, 1f, BidType.DEMAND);
        assertEquals(0, energySlotList.getRemainingCapacity(date0, Market.EOM_MARKET));
    }
}