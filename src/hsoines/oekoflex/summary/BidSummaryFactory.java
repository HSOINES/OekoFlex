package hsoines.oekoflex.summary;

import java.io.IOException;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:36
 */
public final class BidSummaryFactory {
    public static BidSummary create(final String name) throws IOException {
        BidSummary bidSummary = new BidSummary(name);
        return bidSummary;
    }

}
