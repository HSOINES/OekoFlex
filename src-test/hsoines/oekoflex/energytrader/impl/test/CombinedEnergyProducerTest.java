package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.marketoperator.impl.BalancingMarketOperatorImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import repast.simphony.context.Context;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 20:03
 */
public class CombinedEnergyProducerTest {

    private CombinedEnergyProducer energyProducer;
    private SpotMarketOperator SpotMarketOperator;
    private BalancingMarketOperator regelenergieMarketOperator;

    @Before
    public void setUp() throws Exception {
        Context context = RepastTestInitializer.init();
        energyProducer = new CombinedEnergyProducer("energyProducer");
        SpotMarketOperator = Mockito.mock(SpotMarketOperator.class);
        energyProducer.setSpotMarketOperator(SpotMarketOperator);
        regelenergieMarketOperator = Mockito.mock(BalancingMarketOperator.class);
        energyProducer.setBalancingMarketOperator(regelenergieMarketOperator);
    }

    @Test
    public void testAssignEnergy() throws Exception {
        BalancingMarketOperator testOperator = new BalancingMarketOperatorImpl("test", "run/summary-logs/test", 1000, 0);
        CombinedEnergyProducer testProducer = new CombinedEnergyProducer("test_producer");
        testProducer.setQuantityPercentageOnRegelMarkt(0.6f);
        testProducer.setPriceRegelMarkt(1f);
        testProducer.setBalancingMarketOperator(testOperator);

        testProducer.makeBidBalancingMarket();
        testOperator.clearMarket();


    }
}