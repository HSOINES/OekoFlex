package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
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
    private EOMOperator EOMOperator;
    private RegelEnergieMarketOperator regelenergieMarketOperator;

    @Before
    public void setUp() throws Exception {
        Context context = RepastTestInitializer.init();
        energyProducer = new CombinedEnergyProducer("energyProducer");
        EOMOperator = Mockito.mock(EOMOperator.class);
        energyProducer.setEOMOperator(EOMOperator);
        regelenergieMarketOperator = Mockito.mock(RegelEnergieMarketOperator.class);
        energyProducer.setRegelenergieMarketOperator(regelenergieMarketOperator);
    }

    @Test
    public void testAssignEnergy() throws Exception {
        RegelEnergieMarketOperatorImpl testOperator = new RegelEnergieMarketOperatorImpl("test", "run/summary-logs/test", 1000);
        CombinedEnergyProducer testProducer = new CombinedEnergyProducer("test_producer");
        testProducer.setQuantityPercentageOnRegelMarkt(0.6f);
        testProducer.setPriceRegelMarkt(1f);
        testProducer.setRegelenergieMarketOperator(testOperator);

        testProducer.makeBidRegelenergie();
        testOperator.clearMarket();


    }
}