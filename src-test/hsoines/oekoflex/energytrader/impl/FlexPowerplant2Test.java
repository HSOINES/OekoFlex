package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/06/16
 * Time: 21:22
 */
public class FlexPowerplant2Test {
    public static final float SHUTDOWN_COSTS = 50000f;
    public static final float MARGINAL_COSTS = 2f;
    public static final int POWER_RAMP_DOWN = 200;
    public static final int POWER_RAMP_UP = 100;
    public static final int POWER_MIN = 2000;
    public static final int POWER_MAX = 2400;
    public static final int POSITIVE_DEMAND_BALANCING = 100;
    public static final int NEGATIVE_DEMAND_BALANCING = 100;
    public static final float EFFICIENCY = .25f;
    private TestBalancingMarketOperator balancingMarketOperator;
    private FlexPowerplant2 flexpowerplant;
    private TestSpotMarketOperator eomOperator;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        RepastTestInitializer.init();
        eomOperator = new TestSpotMarketOperator();
        balancingMarketOperator = new TestBalancingMarketOperator();

        final File priceForwardOutFile = new File("src-test/resources/price-forward-flex.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        flexpowerplant = new FlexPowerplant2("flexpowerplant", "description",
                POWER_MAX, POWER_MIN, EFFICIENCY, POWER_RAMP_UP, POWER_RAMP_DOWN, MARGINAL_COSTS, SHUTDOWN_COSTS,
                priceForwardCurve);
        flexpowerplant.setBalancingMarketOperator(balancingMarketOperator);
        flexpowerplant.setSpotMarketOperator(eomOperator);
    }

    @Test
    public void testSpotMarketBid() throws Exception {
        float pFlex;
        TimeUtil.startAt(0);

        float pPos0 = POWER_RAMP_UP / FlexPowerplant2.LATENCY;
        float pNeg = 0;
        // tick = 0
        // PFC Price = 256;
        float posPrice = (256/16 - MARGINAL_COSTS) * 4 + FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        float negPrice = -FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        checkBalancingMarketBids(pPos0, pNeg, posPrice, negPrice, 1,0);
        float eMustRun = POWER_MIN * TimeUtil.HOUR_PER_TICK;
        pFlex = POWER_RAMP_UP - pPos0;
        checkEOMBids(eMustRun, pFlex, 1, 1);
        //->POWER: 2100

        // tick = 1
        float pPos1 = POWER_RAMP_UP / FlexPowerplant2.LATENCY;
        float pNeg1 = POWER_RAMP_DOWN / FlexPowerplant2.LATENCY;
        // PFC Price = -16;
        posPrice = -(-16/16 - MARGINAL_COSTS) * 4 * POWER_MIN / pPos1 + FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        negPrice = -(-16/16 - MARGINAL_COSTS) * 4 * POWER_MIN / pNeg1 - FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        checkBalancingMarketBids(pPos0, pNeg, posPrice, negPrice,0, 0);
        eMustRun = (POWER_MIN) * TimeUtil.HOUR_PER_TICK;
        pFlex = 100 - pPos0;
        checkEOMBids(eMustRun, pFlex, 1, 1);
        //->POWER: 2200

        // tick = 2
        flexpowerplant.makeBidBalancingMarket();
        eMustRun = POWER_MIN * TimeUtil.HOUR_PER_TICK;
        pFlex = (POWER_RAMP_UP + POWER_RAMP_DOWN) - pPos0;
        checkEOMBids(eMustRun, pFlex, 1, 1);
        //->POWER: 2300

        // tick = 3
        flexpowerplant.makeBidBalancingMarket();
        pFlex = POWER_RAMP_DOWN + POWER_RAMP_UP  - pPos0;
        eMustRun = (2300 - POWER_RAMP_DOWN) * TimeUtil.HOUR_PER_TICK;
        checkEOMBids(eMustRun, pFlex, 1, 1);
        //->POWER: 2400

        // tick = 4
        // PFC Price = 2;
        pPos0 = 0;
        pNeg = POWER_RAMP_DOWN / FlexPowerplant2.LATENCY;
        posPrice = -1;//dont care
        negPrice = -FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        checkBalancingMarketBids(pPos0, pNeg, posPrice, negPrice, 0, 0);
        pFlex = POWER_RAMP_DOWN;
        eMustRun = (2400 - POWER_RAMP_DOWN) * TimeUtil.HOUR_PER_TICK;
        checkEOMBids(eMustRun, pFlex, 1, 1);
        //->POWER: 2400

        // tick = 5
        // PFC Price = -16;
        pPos0 = 0;
        pNeg = POWER_RAMP_DOWN / FlexPowerplant2.LATENCY;
        posPrice = -1; //dont care
        negPrice = -(-16/16 - MARGINAL_COSTS) * 4 * POWER_MIN / pNeg - FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        checkBalancingMarketBids(pPos0, pNeg, posPrice, negPrice, 0, 0);
        pFlex = POWER_RAMP_DOWN;
        eMustRun = (2400 - POWER_RAMP_DOWN) * TimeUtil.HOUR_PER_TICK;
        checkEOMBids(eMustRun, pFlex, 1, 0);
        //->POWER: 2200

        // tick = 6
        // PFC Price = -16;
        pPos0 = POWER_RAMP_UP / FlexPowerplant2.LATENCY;
        pNeg = POWER_RAMP_DOWN / FlexPowerplant2.LATENCY;
        posPrice = -(-16/16 - MARGINAL_COSTS) * 4 * POWER_MIN / pPos0 + FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        negPrice = -(-16/16 - MARGINAL_COSTS) * 4 * POWER_MIN / pNeg - FlexPowerplant2.FACTOR_BALANCING_CALL * MARGINAL_COSTS * 4;
        checkBalancingMarketBids(pPos0, pNeg, posPrice, negPrice, 0, 0);
        pFlex = POWER_RAMP_DOWN + POWER_RAMP_UP - pPos0;
        eMustRun = (2200 - POWER_RAMP_DOWN) * TimeUtil.HOUR_PER_TICK;
        checkEOMBids(eMustRun, pFlex, 1, 0);
        //->POWER: 2000

        // tick = 7
        pFlex = POWER_RAMP_UP;
        eMustRun = (2000) * TimeUtil.HOUR_PER_TICK;
        checkEOMBids(eMustRun, pFlex, 1, 0);
        //->POWER: 2000
    }

    private void checkBalancingMarketBids(final float pPos, final float pNeg, final float posPrice, final float negPrice,
                                          float assignedPos, float assignedNeg) {
        flexpowerplant.makeBidBalancingMarket();
        int i = (int) TimeUtil.getCurrentTick();

        if (pPos > 0) {
            final PowerPositive powerPositive = balancingMarketOperator.getPowerPositive(i);
            flexpowerplant.notifyClearingDone(TimeUtil.getDate(i), Market.BALANCING_MARKET, powerPositive, -1, assignedPos);
            assertEquals(pPos, powerPositive.getQuantity(), 0.001f);
            assertEquals(posPrice, powerPositive.getPrice(), 0.001f);
        } else {
            balancingMarketOperator.addPositiveSupply(new PowerPositive(0, 0, null));
        }

        if (pNeg > 0) {
            final PowerNegative powerNegative = balancingMarketOperator.getPowerNegative(i);
            flexpowerplant.notifyClearingDone(TimeUtil.getDate(i), Market.BALANCING_MARKET, powerNegative, -1, assignedNeg);
            assertEquals(pNeg, powerNegative.getQuantity(), 0.001f);
            assertEquals(negPrice, powerNegative.getPrice(), 0.001f);
        } else {
            balancingMarketOperator.addNegativeSupply(new PowerNegative(0, 0, null));
        }
    }

    private void checkEOMBids(final float eMustRun, final float pFlex, final float mustRunRate, final float flexRate) {
        flexpowerplant.makeBidEOM();
        int i = (int) TimeUtil.getCurrentTick() * 2;
        final EnergySupply energySupply0 = eomOperator.getEnergySupply(i);
        assertEquals(eMustRun, energySupply0.getQuantity(), 0.001f);
        assertEquals(-SHUTDOWN_COSTS / eMustRun + MARGINAL_COSTS, energySupply0.getPrice(), 0.001f);
        final EnergySupply energySupply1 = eomOperator.getEnergySupply(i + 1);
        assertEquals(pFlex * TimeUtil.HOUR_PER_TICK, energySupply1.getQuantity(), 0.001f);
        assertEquals(MARGINAL_COSTS, energySupply1.getPrice(), 0.001f);
        flexpowerplant.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, energySupply0, 0f, mustRunRate);
        flexpowerplant.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, energySupply1, 0f, flexRate);
        TimeUtil.nextTick();
    }
}