package hsoines.oekoflex.summary;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;

import java.io.IOException;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:56
 */
public final class LoggerFile {

    private final Logger logger;
    private final FileAppender appender;

    public LoggerFile(final String name) throws IOException {
        String loggerFilename = "run/summary-logs/" + name + ".log";
        appender = new FileAppender(new SimpleLayout(), loggerFilename);
        logger = RootLogger.getLogger(name);
        appender.setLayout(new PatternLayout("%m"));
    }

//    public String buildSummary(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
//        return TimeUtilities.dateFormat.format(currentDate) + "," + TimeUtilities.getTick(currentDate) + "," + clearedPrice + "," + rate + "," + bid.getQuantity() + "," + bid.getTypeString();
//    }

//    public void add(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
//        String logMessage = buildSummary(clearedPrice, rate, bid, currentDate);
//        appender.doAppend(buildEvent(logMessage));
//    }

    public void log(String text) {
        appender.doAppend(buildEvent(text));
    }

    LoggingEvent buildEvent(final String s) {
        return new LoggingEvent("", logger, Level.toLevel("INFO"), s + System.getProperty("line.separator"), null);
    }
}
