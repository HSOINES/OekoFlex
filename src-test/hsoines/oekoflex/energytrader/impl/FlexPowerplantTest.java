package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Before;
import org.junit.Test;

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
    public static final int POSITIVE_DEMAND_REM = 100;
    public static final int NEGATIVE_DEMAND_REM = 100;
    private RegelEnergieMarketOperatorImpl regelEnergieMarketOperator;
    private FlexPowerplant flexpowerplant;
    private EOMOperatorImpl eomOperator;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        regelEnergieMarketOperator = new RegelEnergieMarketOperatorImpl("test", ".", POSITIVE_DEMAND_REM, NEGATIVE_DEMAND_REM);
        eomOperator = new EOMOperatorImpl("test_eom_operator", ".");
        eomOperator.addDemand(new EnergyDemand(3000, 200, null));

        flexpowerplant = new FlexPowerplant("flexpowerplant", "description", POWER_MAX, POWER_MIN, POWER_RAMP_UP, POWER_RAMP_DOWN, MARGINAL_COSTS, SHUTDOWN_COSTS);
        flexpowerplant.setRegelenergieMarketOperator(regelEnergieMarketOperator);
        flexpowerplant.setEOMOperator(eomOperator);
    }

    @Test
    public void testFlexPowerplant() throws Exception {
        flexpowerplant.makeBidRegelenergie();
        flexpowerplant.makeBidEOM();

        regelEnergieMarketOperator.clearMarket();
        eomOperator.clearMarket();

        List<TradeRegistryImpl.EnergyTradeElement> currentAssignments = flexpowerplant.getCurrentAssignments();

        assertEquals(4, currentAssignments.size());

        TradeRegistryImpl.EnergyTradeElement powerPositive = currentAssignments.get(0);
        assertEquals(BidType.POWER_POSITIVE, powerPositive.getBidType());
        assertEquals(50f, powerPositive.getAssignedPrice(), 0.00001f);   //price???
        assertEquals(100, powerPositive.getOfferedQuantity(), 0.00001f);
        assertEquals(1, powerPositive.getRate(), 0.00001f);

        TradeRegistryImpl.EnergyTradeElement powerNegative = currentAssignments.get(1);
        assertEquals(BidType.POWER_NEGATIVE, powerNegative.getBidType());
        assertEquals(50f, powerNegative.getAssignedPrice(), 0.00001f);  //price???
        assertEquals(0, powerNegative.getOfferedQuantity(), 0.00001f);
        assertEquals(1, powerNegative.getRate(), 0.00001f);

        TradeRegistryImpl.EnergyTradeElement energyMustRun = currentAssignments.get(2);
        assertEquals(BidType.ENERGY_SUPPLY_MUSTRUN, energyMustRun.getBidType());
        assertEquals(-.2f, energyMustRun.getAssignedPrice(), 0.00001f); // shutdown costs / must-run-quantity
        assertEquals(500f, energyMustRun.getOfferedQuantity(), 0.00001f);
        assertEquals(.4f, energyMustRun.getRate(), 0.00001f);

        TradeRegistryImpl.EnergyTradeElement energy = currentAssignments.get(3);
        assertEquals(BidType.ENERGY_SUPPLY, energy.getBidType());
        assertEquals(-.2f, energy.getAssignedPrice(), 0.00001f);
        assertEquals(25, energy.getOfferedQuantity(), 0.00001f);
        assertEquals(0f, energy.getRate(), 0.00001f);

    }

    //todo: must_run_cut

}