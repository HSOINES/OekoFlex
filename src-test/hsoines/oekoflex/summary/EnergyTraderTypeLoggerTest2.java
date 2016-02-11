package hsoines.oekoflex.summary;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.impl.test.CombinedEnergyProducer;
import hsoines.oekoflex.energytrader.impl.test.DaytimeEnergyConsumer;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.Market;
import org.junit.Before;
import org.junit.Test;
import repast.simphony.context.Context;

import java.util.Date;

/**
 * User: jh
 * Date: 16/01/16
 * Time: 01:09
 */
public class EnergyTraderTypeLoggerTest2 {

    private EnergyTraderTypeLogger energyTraderTypeLogger;

    @Before
    public void setUp() throws Exception {
        Context<OekoflexAgent> context = RepastTestInitializer.init();
        DaytimeEnergyConsumer test = new DaytimeEnergyConsumer("test", 1000, 12f, 0.8f);
        context.add(test);
        CombinedEnergyProducer combinedTest = new CombinedEnergyProducer("combinedTest");
        context.add(combinedTest);
        energyTraderTypeLogger = new EnergyTraderTypeLogger(context, "run/summary-logs/test");

        test.notifyClearingDone(new Date(), Market.EOM_MARKET, new EnergyDemand(10f, 1000, null), 10.0f, 1f);
        combinedTest.notifyClearingDone(new Date(), Market.EOM_MARKET, new PowerPositive(12f, 200, null), 1f, .4f);

    }

    @Test
    public void testName() throws Exception {
        //must write log!
        energyTraderTypeLogger.execute();
    }
}