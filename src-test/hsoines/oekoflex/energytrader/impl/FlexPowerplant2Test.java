package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.tools.TestBalancingMarketOperator;
import hsoines.oekoflex.energytrader.tools.TestSpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/06/16
 * Time: 21:22
 */
public class FlexPowerplant2Test {
    public static final float START_STOP_COSTS = 100f;
    public static final float MARGINAL_COSTS = 2f;
    public static final int POWER_RAMP_DOWN = 200;
    public static final int POWER_RAMP_UP = 100;
    public static final int POWER_MIN = 2000;
    public static final int POWER_MAX = 2400;
    public static final int POSITIVE_DEMAND_BALANCING = 100;
    public static final int NEGATIVE_DEMAND_BALANCING = 100;
    public static final float EFFICIENCY = .25f;
    private TestBalancingMarketOperator testBalancingMarketOperator;
    private FlexPowerplant2 flexpowerplant;
    private TestSpotMarketOperator testEomOperator;
    private PriceForwardCurve priceForwardCurve;
    private float emissionRate;
    private float efficiency;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        RepastTestInitializer.init();
        testEomOperator = new TestSpotMarketOperator();
        testBalancingMarketOperator = new TestBalancingMarketOperator();

        final File priceForwardOutFile = new File("src-test/resources/price-forward-flex.csv");
        priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        flexpowerplant = new FlexPowerplant2("flexpowerplant", "description",
                POWER_MAX, POWER_MIN, POWER_RAMP_UP, POWER_RAMP_DOWN, START_STOP_COSTS,
                priceForwardCurve, MARGINAL_COSTS);
        flexpowerplant.setBalancingMarketOperator(testBalancingMarketOperator);
        flexpowerplant.setSpotMarketOperator(testEomOperator);

    }

    @Test
    public void testSpotMarketBid() throws Exception {
        TimeUtil.startAt(0);
        assertEquals(256, priceForwardCurve.getPriceSummation(TimeUtil.getCurrentTick(), 16), 0.00001f);
        testBalancingMarketOperator.makeBid(flexpowerplant).checkPowerPos(33.3333f, 57.6f).checkPowerNeg(0, 0).notifyRatePos(1).notifyRateNeg(0);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 16.666667f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2066.66667f);

        TimeUtil.startAt(1);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 33.33333f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2133.3333f);
        TimeUtil.startAt(2);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 50}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2200);
        TimeUtil.startAt(3);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 66.6666f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2266.6666f);
        TimeUtil.startAt(4);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{516.6666f, 66.6666f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2333.3333f);
        TimeUtil.startAt(5);
        testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{533.3333f, 58.3333f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2366.6666f);
        for (int i = 6; i < 16; i++) {
            TimeUtil.startAt(i);
            testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{541.6666f, 50f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2366.6666f);
        }
        TimeUtil.startAt(16);
        //PFC = -9
        testBalancingMarketOperator.makeBid(flexpowerplant).checkPowerPos(33.3333f, 2641.6064f).checkPowerNeg(66.6666f, 0).notifyRatePos(1).notifyRateNeg(0);

    }

    @Test
    public void testMarginalCosts() throws Exception {
        final int variableCosts = 3;
        final int fuelCosts = 6;
        final int co2CertificateCosts = 7;
        emissionRate = .6f;
        efficiency = .9f;
        assertEquals(14.333333f, FlexPowerplant2.calculateMarginalCosts(variableCosts, fuelCosts, co2CertificateCosts, emissionRate, efficiency), 0.00001f);
    }

    @Test
    public void testName() throws Exception {
    }

}