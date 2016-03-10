package hsoines.oekoflex.summary;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:56
 */
public final class LoggerFile {
    private static final Log log = LogFactory.getLog(LoggerFile.class);

    private final Logger logger;
    private final FileAppender appender;
    private final ScheduledExecutorService executorService;
    private final LinkedBlockingQueue<String> loggingEvents;

    public LoggerFile(final String name, final String scenario) throws IOException {
        this(name, scenario, 100000);
    }

    LoggerFile(final String name, final String scenario, final int queueSize) throws IOException {
        String scenarioLogDir = scenario + "/";
        String loggerFilename = scenarioLogDir + name + ".log.csv";
        appender = new FileAppender(new SimpleLayout(), loggerFilename);
        logger = RootLogger.getLogger(name);
        appender.setLayout(new PatternLayout("%m"));
        loggingEvents = new LinkedBlockingQueue<>(queueSize);

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    String s;
                    while ((s = loggingEvents.take()) != null) {
                        final LoggingEvent event = buildEvent(s);
                        appender.doAppend(event);
                    }
                } catch (Throwable e) {
                    System.err.println(e.getMessage());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

//    public String buildSummary(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
//        return TimeUtilities.dateFormat.format(currentDate) + "," + TimeUtilities.getTick(currentDate) + "," + clearedPrice + "," + rate + "," + bid.getQuantity() + "," + bid.getTypeString();
//    }

//    public void add(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
//        String logMessage = buildSummary(clearedPrice, rate, bid, currentDate);
//        appender.doAppend(buildEvent(logMessage));
//    }

    public void log(String text) {
        try {
            loggingEvents.put(text);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    LoggingEvent buildEvent(final String s) {
        return new LoggingEvent("", logger, Level.toLevel("INFO"), s + System.getProperty("line.separator"), null);
    }
}
