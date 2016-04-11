package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 18:23
 */
public class StorageBalancingMarketTest {

    public static final int DISCHARGE_POWER = 100;
    public static final float MARGINAL_COSTS = 1f;
    public static final int CHARGE_POWER = 150;
    public static final int ENERGY_CAPACITY = 1000;
    public static final float SOC_MAX = .9f;
    public static final float SOC_MIN = .2f;
    private Storage storage;
    private SpotMarketOperatorImpl operator;
    private TestingBalancingMarketOperator balancingMarketOperator;

    @Before
    public void setUp() throws Exception {
        //RepastTestInitializer.init();
        final File priceForwardOutFile = new File("src-test/resources/price-forward.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        storage = new Storage("test", "description", MARGINAL_COSTS, 0.1f, ENERGY_CAPACITY, SOC_MAX, SOC_MIN, CHARGE_POWER, DISCHARGE_POWER, priceForwardCurve);
        balancingMarketOperator = new TestingBalancingMarketOperator();
        storage.setBalancingMarketOperator(balancingMarketOperator);
    }

    @Test
    public void testSOCMin() throws Exception {
        TimeUtil.nextTick();
        storage.makeBidBalancingMarket();
        PowerNegative lastPowerNegative = balancingMarketOperator.getLastPowerNegative();
        //=ChargePower
        assertEquals(CHARGE_POWER, lastPowerNegative.getQuantity(), 0.0001f);
        // Spread in tick [0,15] = 10 => ChargePower * Zeit * Spread
        assertEquals(CHARGE_POWER * 4 * 10, lastPowerNegative.getPrice(), 0.0001f);

        assertEquals(0, balancingMarketOperator.getLastPowerPositive().getQuantity(), 0.0001f);
    }

    @Test
    public void testSOCMiddle() throws Exception {
        storage.setSOC(.5f);
        TimeUtil.nextTick();
        storage.makeBidBalancingMarket();
        PowerNegative lastPowerNegative = balancingMarketOperator.getLastPowerNegative();
        //=Distance from soc to soc_max / 4 hours:
        assertEquals(400f/4f, lastPowerNegative.getQuantity(), 0.0001f);
        // Spread in tick [0,15] = 10 => ChargePower * Zeit * Spread
        assertEquals(400f/4f * 4 * 10, lastPowerNegative.getPrice(), 0.0001f);

        PowerPositive lastPowerPositive = balancingMarketOperator.getLastPowerPositive();
        //=Distance from soc to soc_min / 4 hours:
        assertEquals(300f/4f, lastPowerPositive.getQuantity(), 0.0001f);
        // Spread in tick [0,15] = 10 => DischargePower * Zeit * Spread
        assertEquals(300f/4f * 4 * 10, lastPowerPositive.getPrice(), 0.0001f);

    }

    @Test
    public void testSOCMax() throws Exception {
        storage.setSOC(SOC_MAX);
        TimeUtil.nextTick();
        storage.makeBidBalancingMarket();
        PowerNegative lastPowerNegative = balancingMarketOperator.getLastPowerNegative();
        assertEquals(0, lastPowerNegative.getQuantity(), 0.0001f);

        PowerPositive lastPowerPositive = balancingMarketOperator.getLastPowerPositive();
        //DischargePower
        assertEquals(DISCHARGE_POWER, lastPowerPositive.getQuantity(), 0.0001f);
        // Spread in tick [0,15] = 10 => DischargePower * Zeit * Spread
        assertEquals(DISCHARGE_POWER * 4 * 10, lastPowerPositive.getPrice(), 0.0001f);
    }

    private static class TestingBalancingMarketOperator implements BalancingMarketOperator {

        private PowerNegative lastPowerNegative;
        private PowerPositive lastPowerPositive;

        @Override
        public void addPositiveSupply(PowerPositive supply) {
            lastPowerPositive = supply;
        }

        @Override
        public void addNegativeSupply(PowerNegative supply) {
            lastPowerNegative = supply;
        }

        @Override
        public void clearMarket() {

        }

        @Override
        public long getTotalClearedPositiveQuantity() {
            return 0;
        }

        @Override
        public long getTotalClearedNegativeQuantity() {
            return 0;
        }

        @Override
        public float getLastPositiveAssignmentRate() {
            return 0;
        }

        @Override
        public float getLastClearedNegativeMaxPrice() {
            return 0;
        }

        @Override
        public float getLastNegativeAssignmentRate() {
            return 0;
        }

        @Override
        public float getLastClearedPositiveMaxPrice() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        public PowerNegative getLastPowerNegative() {
            return lastPowerNegative;
        }

        public PowerPositive getLastPowerPositive() {
            return lastPowerPositive;
        }
    }
}