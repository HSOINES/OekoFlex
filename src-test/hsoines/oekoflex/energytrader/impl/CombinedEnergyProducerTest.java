package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.tools.RepastTestInitializer;
import org.junit.Before;
import org.mockito.Mockito;
import repast.simphony.context.Context;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 20:03
 */
public class CombinedEnergyProducerTest {

    private CombinedEnergyProducer energyProducer;
    private EnergyOnlyMarketOperator energyOnlyMarketOperator;
    private RegelEnergieMarketOperator regelenergieMarketOperator;

    @Before
    public void setUp() throws Exception {
        Context context = RepastTestInitializer.init();
        energyProducer = new CombinedEnergyProducer("energyProducer");
        energyOnlyMarketOperator = Mockito.mock(EnergyOnlyMarketOperator.class);
        energyProducer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
        regelenergieMarketOperator = Mockito.mock(RegelEnergieMarketOperator.class);
        energyProducer.setRegelenergieMarketOperator(regelenergieMarketOperator);
    }
//
//    @Test
//    public void testName() throws Exception {
////        ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
////        schedule.execute(); -> läuft so nicht
//        energyProducer.makeSupply();
//        assertEquals(1, energyOnlyMarketOperator.getTotalClearedQuantity());
//    }
}