package hsoines.oekoflex.builder.traderfactories;

import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.io.File;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 22:45
 */
public class FlexPowerplantFactoryTest {
    @Test
    public void testIt() throws Exception {
        FlexPowerplantFactory.build(new File("run-config/scenario1"), new DefaultContext<>(), null, null);
    }
}