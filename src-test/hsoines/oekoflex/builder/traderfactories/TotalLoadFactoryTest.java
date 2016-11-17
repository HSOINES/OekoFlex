package hsoines.oekoflex.builder.traderfactories;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.io.File;

/**
 * 
 */
public class TotalLoadFactoryTest {
    @Test
    public void testLoadSample() throws Exception {
        BasicConfigurator.configure();
        File configDir = new File("run-config/scenario1");
        TotalLoadFactory.build(configDir, new DefaultContext<>(), null, 0);
    }
}