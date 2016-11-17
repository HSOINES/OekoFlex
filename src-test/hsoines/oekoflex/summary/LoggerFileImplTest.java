package hsoines.oekoflex.summary;

import hsoines.oekoflex.summary.impl.LoggerFileImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class LoggerFileImplTest {
    private static final Log log = LogFactory.getLog(LoggerFileImplTest.class);

    private LoggerFile testLogger;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        testLogger = new LoggerFileImpl("testlogger", "testscenario", 3);

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