package hsoines.oekoflex.summary;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.util.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.RootLogger;

import java.io.IOException;
import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:56
 */
public final class BidSummary {

    private final Log log;

    public BidSummary(final String name) throws IOException {
        log = LogFactory.getLog(name);
        FileAppender newAppender = new FileAppender(new SimpleLayout(), "run/summary-logs/" + name + ".log");
        newAppender.setImmediateFlush(true);
        RootLogger.getLogger(name).addAppender(newAppender);
    }


    public String buildSummary(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        return TimeUtilities.dateFormatter.format(currentDate) + "," + TimeUtilities.getTick(currentDate) + "," + clearedPrice + "," + rate + "," + bid.getQuantity() + "," + bid.getTypeString();
    }

    public void add(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        log.info(buildSummary(clearedPrice, rate, bid, currentDate));
    }
    //todo: summary ausrechnen.
}
