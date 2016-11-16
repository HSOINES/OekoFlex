package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import repast.simphony.context.DefaultContext;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 22:45
 */
public class FlexPowerplant2FactoryTest {
    static {
        Locale.setDefault(OekoFlexContextBuilder.defaultlocale);
        OekoFlexContextBuilder.defaultNumberFormat = DecimalFormat.getNumberInstance();
    }

    @Test
    public void testIt() throws Exception {
        BasicConfigurator.configure();
        final File configDir = new File("run-config/test");
        Properties globalProperties = OekoFlexContextBuilder.loadProperties(configDir);
        FlexPowerplant3Factory.build(configDir, new DefaultContext<>(), null, null, null, globalProperties);
    }
}