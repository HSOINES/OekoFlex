package hsoines.oekoflex.builder.traderfactories;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.io.File;

/**
 * User: jh
 * Date: 19/02/16
 * Time: 08:39
 */
public class TotalLoadFactoryTest {
    @Test
    public void testLoadSample() throws Exception {
        BasicConfigurator.configure();
        File configDir = new File("run-config/scenario1");
        TotalLoadFactory.build(configDir, new DefaultContext<>(), null);

    }
}