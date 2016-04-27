package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.TradeRegistry;
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

    private TradeRegistryWithPrerun tradeRegistryWithPrerun;

    @Before
    public void setUp() throws Exception {
        tradeRegistryWithPrerun = new TradeRegistryWithPrerun(TradeRegistry.Type.CONSUM, 0, 1000, 2);
    }

    @Test
    public void testPrerun() throws Exception {
        for (int i = 0; i < 3 * 96; i++) {
            tradeRegistryWithPrerun.setCapacity(i, i);
        }
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
}