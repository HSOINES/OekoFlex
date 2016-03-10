package hsoines.oekoflex.summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 14:27
 */
public class LoggerFileTest {
    private static final Log log = LogFactory.getLog(LoggerFileTest.class);

    private LoggerFile testLogger;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        testLogger = new LoggerFile("testlogger", "testscenario", 3);

    }

    @Test
    public void testLogging() throws Exception {
        testLogger.log("mein test");
        log.info("logged");
        testLogger.log("mein test");
        log.info("logged");
        testLogger.log("mein test");
        log.info("logged");
        testLogger.log("mein test");
        log.info("logged");
    }
}