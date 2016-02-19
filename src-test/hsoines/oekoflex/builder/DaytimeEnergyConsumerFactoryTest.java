package hsoines.oekoflex.builder;

import hsoines.oekoflex.builder.traderfactories.DaytimeEnergyConsumerFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.io.File;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 22:26
 */
public class DaytimeEnergyConsumerFactoryTest {
    @Test
    public void testLoadSample() throws Exception {
        BasicConfigurator.configure();
        File configDir = new File("run-config/scenario1");
        DaytimeEnergyConsumerFactory.build(configDir, new DefaultContext<>(), null);

    }

}