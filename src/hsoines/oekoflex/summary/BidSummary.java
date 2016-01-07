package hsoines.oekoflex.summary;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.util.TimeUtilities;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.RootLogger;

import java.io.IOException;
import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:56
 */
public final class BidSummary {

    private final Logger logger;
    private final FileAppender newAppender;

    public BidSummary(final String name) throws IOException {
        String loggerFilename = "run/summary-logs/" + name + ".log";
        newAppender = new FileAppender(new SimpleLayout(), loggerFilename);
        logger = RootLogger.getLogger(name);
        newAppender.doAppend(buildEvent("starting."));
    }

    public String buildSummary(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        return TimeUtilities.dateFormatter.format(currentDate) + "," + TimeUtilities.getTick(currentDate) + "," + clearedPrice + "," + rate + "," + bid.getQuantity() + "," + bid.getTypeString();
    }

    public void add(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        String logMessage = buildSummary(clearedPrice, rate, bid, currentDate);
        newAppender.doAppend(buildEvent(logMessage));
    }

    //todo: summary ausrechnen.
    LoggingEvent buildEvent(final String s) {
        return new LoggingEvent("", logger, Level.toLevel("INFO"), s, null);
    }
}
