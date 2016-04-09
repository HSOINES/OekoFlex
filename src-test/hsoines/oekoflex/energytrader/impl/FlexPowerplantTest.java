package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.marketoperator.impl.BalancingMarketOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 22:15
 */
public class FlexPowerplantTest {

    public static final float SHUTDOWN_COSTS = 100f;
    public static final float MARGINAL_COSTS = 50f;
    public static final int POWER_RAMP_DOWN = 200;
    public static final int POWER_RAMP_UP = 100;
    public static final int POWER_MIN = 2000;
    public static final int POWER_MAX = 5000;
    public static final int POSITIVE_DEMAND_BALANCING = 100;
    public static final int NEGATIVE_DEMAND_BALANCING = 100;
    private BalancingMarketOperatorImpl balancingMarketOperator;
    private FlexPowerplant flexpowerplant;
    private SpotMarketOperatorImpl eomOperator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        balancingMarketOperator = new BalancingMarketOperatorImpl("test", true, ".", POSITIVE_DEMAND_BALANCING, NEGATIVE_DEMAND_BALANCING);
        eomOperator = new SpotMarketOperatorImpl("test_eom_operator", ".", true);

        final File priceForwardOutFile = new File("run-config/test/price-forward/price-forward.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        flexpowerplant = new FlexPowerplant("flexpowerplant", "description",
                POWER_MAX, POWER_MIN, POWER_RAMP_UP, POWER_RAMP_DOWN, MARGINAL_COSTS, SHUTDOWN_COSTS,
                priceForwardCurve);
        flexpowerplant.setBalancingMarketOperator(balancingMarketOperator);
        flexpowerplant.setSpotMarketOperator(eomOperator);
    }

    @Test
    public void testBidOnTick1() {
        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 1000, null));
        flexpowerplant.makeBidEOM();
        eomOperator.clearMarket();

        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 1000, null));
        flexpowerplant.makeBidEOM();
        eomOperator.clearMarket();

        List<TradeRegistryImpl.EnergyTradeElement> currentAssignments = flexpowerplant.getCurrentAssignments();

        assertEquals(2, currentAssignments.size());

        TradeRegistryImpl.EnergyTradeElement energyMustRun = currentAssignments.get(0);
        assertEquals(BidType.ENERGY_SUPPLY_MUSTRUN, energyMustRun.getBidType());
        assertEquals(-.2f, energyMustRun.getOfferedPrice(), 0.0001f); // shutdown costs / must-run-quantity
        assertEquals(3000f, energyMustRun.getAssignedPrice(), 0.0001f); // max-price
        assertEquals(500f, energyMustRun.getOfferedQuantity(), 0.0001f); // min_power per 15min
        assertEquals(1f, energyMustRun.getRate(), 0.0001f); // full assigned

        TradeRegistryImpl.EnergyTradeElement energy = currentAssignments.get(1);
        assertEquals(BidType.ENERGY_SUPPLY, energy.getBidType());
        assertEquals(3000f, energy.getAssignedPrice(), 0.0001f);
        assertEquals(50f, energy.getOfferedPrice(), 0.0001f); //marginal costs 15min
        assertEquals(25, energy.getOfferedQuantity(), 0.0001f); // Ramp-Up per 15min
        assertEquals(1f, energy.getRate(), 0.0001f);

        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 1000, null));
        flexpowerplant.makeBidBalancingMarket();
        flexpowerplant.makeBidEOM();
        balancingMarketOperator.clearMarket();
        eomOperator.clearMarket();

        currentAssignments = flexpowerplant.getCurrentAssignments();
        assertEquals(4, currentAssignments.size());

        TradeRegistryImpl.EnergyTradeElement powerPositive = currentAssignments.get(0);
        assertEquals(BidType.POWER_POSITIVE, powerPositive.getBidType());
        assertEquals(18f, powerPositive.getOfferedPrice(), 0.0001f); // Summe PFC [1,16]
        assertEquals(18f, powerPositive.getAssignedPrice(), 0.0001f);
        assertEquals(33.333333f, powerPositive.getOfferedQuantity(), 0.0001f); // RampUp after 5 minutes
        assertEquals(1f, powerPositive.getRate(), 0.0001f);

        TradeRegistryImpl.EnergyTradeElement powerNegative = currentAssignments.get(1);
        assertEquals(BidType.POWER_NEGATIVE, powerNegative.getBidType());
        assertEquals(204f, powerNegative.getOfferedPrice(), 0.0001f); // 16*marginal_costs + negative prices in PFC [1,16]
        assertEquals(204f, powerNegative.getAssignedPrice(), 0.0001f);
        assertEquals(66.6666666f, powerNegative.getOfferedQuantity(), 0.0001f); // RampDown after 5 minutes
        assertEquals(1f, powerNegative.getRate(), 0.0001f);

        energyMustRun = currentAssignments.get(2);
        assertEquals(BidType.ENERGY_SUPPLY_MUSTRUN, energyMustRun.getBidType());
        assertEquals(-.2f, energyMustRun.getOfferedPrice(), 0.0001f); // shutdown costs / must-run-quantity
        assertEquals(3000f, energyMustRun.getAssignedPrice(), 0.0001f); // max-price
        assertEquals(500f, energyMustRun.getOfferedQuantity(), 0.0001f); // (min_power+ramp-up) / 4
        assertEquals(1f, energyMustRun.getRate(), 0.0001f); // full assigned

        energy = currentAssignments.get(3);
        assertEquals(BidType.ENERGY_SUPPLY, energy.getBidType());
        assertEquals(3000f, energy.getAssignedPrice(), 0.0001f);
        assertEquals(50, energy.getOfferedQuantity(), 0.0001f); // Ramp-Up per 15min
        assertEquals(1f, energy.getRate(), 0.0001f);

        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 1000, null));
        flexpowerplant.makeBidEOM();
        eomOperator.clearMarket();

        currentAssignments = flexpowerplant.getCurrentAssignments();
        assertEquals(4, currentAssignments.size());

        energyMustRun = currentAssignments.get(2);
        assertEquals(BidType.ENERGY_SUPPLY_MUSTRUN, energyMustRun.getBidType());
        assertEquals(-.193f, energyMustRun.getOfferedPrice(), 0.001f); // shutdown costs(100) / must-run-quantity(550)
        assertEquals(3000f, energyMustRun.getAssignedPrice(), 0.0001f); // max-price
        assertEquals(516.6666f, energyMustRun.getOfferedQuantity(), 0.0001f); // (min_power+ramp-up) / 4
        assertEquals(1f, energyMustRun.getRate(), 0.0001f); // full assigned

    }

    @Test
    public void testFlexPowerplantFirstRun() throws Exception {
        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 200, null));
        flexpowerplant.makeBidBalancingMarket();
        flexpowerplant.makeBidEOM();
        balancingMarketOperator.clearMarket();
        eomOperator.clearMarket();

        TimeUtil.nextTick();
        eomOperator.addDemand(new EnergyDemand(3000, 200, null));
        flexpowerplant.makeBidBalancingMarket();
        flexpowerplant.makeBidEOM();
        balancingMarketOperator.clearMarket();
        eomOperator.clearMarket();

        List<TradeRegistryImpl.EnergyTradeElement> currentAssignments = flexpowerplant.getCurrentAssignments();

        assertEquals(4, currentAssignments.size());

        TradeRegistryImpl.EnergyTradeElement powerPositive = currentAssignments.get(0);
        assertEquals(BidType.POWER_POSITIVE, powerPositive.getBidType());
        assertEquals(17f, powerPositive.getAssignedPrice(), 0.0001f);
        assertEquals(100 / 3f, powerPositive.getOfferedQuantity(), 0.0001f);
        assertEquals(1, powerPositive.getRate(), 0.0001f);

        TradeRegistryImpl.EnergyTradeElement energyMustRun = currentAssignments.get(2);
        assertEquals(BidType.ENERGY_SUPPLY_MUSTRUN, energyMustRun.getBidType());
        assertEquals(-.2f, energyMustRun.getOfferedPrice(), 0.0001f); // shutdown costs / must-run-quantity
        assertEquals(-.2f, energyMustRun.getAssignedPrice(), 0.0001f); // max-price
        assertEquals(500f, energyMustRun.getOfferedQuantity(), 0.0001f); // min_power per 15min
        assertEquals(.4f, energyMustRun.getRate(), 0.0001f); // full assigned

        TradeRegistryImpl.EnergyTradeElement energy = currentAssignments.get(3);
        assertEquals(BidType.ENERGY_SUPPLY, energy.getBidType());
        assertEquals(-.2f, energy.getAssignedPrice(), 0.0001f);
        assertEquals(50f, energy.getOfferedPrice(), 0.0001f); //marginal costs 15min
        assertEquals(25, energy.getOfferedQuantity(), 0.0001f); // Ramp-Up per 15min
        assertEquals(0f, energy.getRate(), 0.0001f);

    }

    //todo: must_run only partially assigned

}