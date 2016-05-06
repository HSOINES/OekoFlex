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
 * Date: 27/04/16
 * Time: 23:00
 */
public class TradeRegistryWithPrerunTest {

    private TradeRegistry tradeRegistryWithPrerun;

    @Before
    public void setUp() throws Exception {
        tradeRegistryWithPrerun = new TradeRegistryImpl(TradeRegistry.Type.CONSUM, 0, 1000);
        for (int i = 0; i < 3 * 96; i++) {
            tradeRegistryWithPrerun.setCapacity(i, i);
        }
        tradeRegistryWithPrerun.duplicateCapacity(2 * 96);
    }

    @Test
    public void testRegistrySize() throws Exception {
        final TradeRegistryImpl tradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.CONSUM, 0, 1000);
        for (int i = 0; i < 5 * 96; i++) {
            tradeRegistry.setCapacity(i, i);
        }
        tradeRegistry.duplicateCapacity(96 * 3);
        assertEquals(8 * 96, tradeRegistry.getNTicks());
    }

    @Test
    public void testPrerun() throws Exception {
        Date date = TimeUtil.getDate(-192);
        assertEquals(96, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
        date = TimeUtil.getDate(-191);
        assertEquals(97, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
        date = TimeUtil.getDate(-96);
        assertEquals(192, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
        date = TimeUtil.getDate(-1);
        assertEquals(287, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
        date = TimeUtil.getDate(0);
        assertEquals(0, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
        date = TimeUtil.getDate(1);
        assertEquals(1, tradeRegistryWithPrerun.getCapacity(date), 0.001f);
    }

    @Test
    public void testAssignmentPositiveTicks() throws Exception {
        final int capacityAndTick = 3 * 96 - 1;
        Date date = TimeUtil.getDate(capacityAndTick);
        final float quantityUsed = 100f;
        tradeRegistryWithPrerun.addAssignedQuantity(date, Market.SPOT_MARKET, 10f, 10f, quantityUsed, 1f, BidType.ENERGY_SUPPLY);
        assertEquals(quantityUsed, tradeRegistryWithPrerun.getQuantityUsed(date), 0.0001f);
        assertEquals(capacityAndTick, tradeRegistryWithPrerun.getCapacity(date), 0.0001f);
        assertEquals(capacityAndTick - quantityUsed, tradeRegistryWithPrerun.getRemainingCapacity(date, Market.SPOT_MARKET), 0.0001f);
    }

    @Test
    public void testAssignmentNegativeTicks() throws Exception {
        final int tick1 = -96;
        final int tick2 = 192;
        Date date1 = TimeUtil.getDate(tick1);
        Date date2 = TimeUtil.getDate(tick2);
        final float quantityUsed = 20f;
        tradeRegistryWithPrerun.addAssignedQuantity(date1, Market.SPOT_MARKET, 10f, 10f, quantityUsed, 1f, BidType.ENERGY_SUPPLY);
        tradeRegistryWithPrerun.addAssignedQuantity(date2, Market.SPOT_MARKET, 10f, 10f, quantityUsed, 1f, BidType.ENERGY_SUPPLY);
        assertEquals(quantityUsed, tradeRegistryWithPrerun.getQuantityUsed(date1), 0.0001f);
        assertEquals(192, tradeRegistryWithPrerun.getCapacity(date1), 0.0001f);
        assertEquals(192 - quantityUsed, tradeRegistryWithPrerun.getRemainingCapacity(date1, Market.SPOT_MARKET), 0.0001f);
    }


}