package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
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

    private RegelEnergieMarketOperatorImpl regelEnergieMarketOperator;
    private FlexPowerplant flexpowerplant;

    @Before
    public void setUp() throws Exception {
        RepastTestInitializer.init();
        regelEnergieMarketOperator = new RegelEnergieMarketOperatorImpl("test", ".", 10000, 100000);
        flexpowerplant = new FlexPowerplant("flexpowerplant", 5000, 2000, 100, 100, 10f, 100f);
        flexpowerplant.setRegelenergieMarketOperator(regelEnergieMarketOperator);

    }

    @Test
    public void testFlexPowerplant() throws Exception {
        flexpowerplant.makeBidRegelenergie();
        regelEnergieMarketOperator.clearMarket();

        List<EnergyTradeRegistryImpl.EnergyTradeElement> currentAssignments = flexpowerplant.getCurrentAssignments();

        assertEquals(2, currentAssignments.size());
        assertEquals(BidType.POSITIVE_SUPPLY, currentAssignments.get(0).getBidType());
        assertEquals(BidType.NEGATIVE_SUPPLY, currentAssignments.get(1).getBidType());
        assertEquals(10f, currentAssignments.get(0).getAssignedPrice(), 0.00001f);   // test impl
        assertEquals(10f, currentAssignments.get(1).getAssignedPrice(), 0.00001f);   // test impl
        assertEquals(100, currentAssignments.get(0).getOfferedQuantity());   // test impl
        assertEquals(2000, currentAssignments.get(1).getOfferedQuantity());   //test impl
    }
}