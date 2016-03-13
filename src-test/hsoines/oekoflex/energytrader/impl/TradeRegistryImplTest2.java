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
public class TradeRegistryImplTest2 {

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
        energyTradeRegistry.addAssignedQuantity(date0, Market.BALANCING_MARKET, 10f, 9f, 100, 0.5f, BidType.POWER_POSITIVE);
        energyTradeRegistry.addAssignedQuantity(date0, Market.BALANCING_MARKET, 10f, 9f, 100, 0.8f, BidType.POWER_POSITIVE);
        energyTradeRegistry.addAssignedQuantity(date0, Market.BALANCING_MARKET, 10f, 9f, 100, 1f, BidType.POWER_NEGATIVE);
        energyTradeRegistry.addAssignedQuantity(date0, Market.BALANCING_MARKET, 10f, 11f, 100, 0.5f, BidType.POWER_NEGATIVE);
        energyTradeRegistry.addAssignedQuantity(date1, Market.BALANCING_MARKET, 10f, 9f, 100, 0.5f, BidType.POWER_POSITIVE);
        energyTradeRegistry.addAssignedQuantity(date1, Market.BALANCING_MARKET, 10f, 9f, 100, 0.8f, BidType.POWER_POSITIVE);
        energyTradeRegistry.addAssignedQuantity(date1, Market.BALANCING_MARKET, 10f, 9f, 100, 1f, BidType.POWER_NEGATIVE);
    }


    @Test
    public void testAllEnergyUsed() throws Exception {
        assertEquals(130f, energyTradeRegistry.getPositiveQuantityUsed(date0), 0.00001f);
        assertEquals(150f, energyTradeRegistry.getNegativeQuantityUsed(date0), 0.00001f);
        assertEquals(280f, energyTradeRegistry.getQuantityUsed(date0), 0.00001f);
    }
}