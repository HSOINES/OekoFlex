package hsoines.oekoflex.summary;

import hsoines.oekoflex.bid.Demand;
import org.junit.Test;

import java.util.Date;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 23:41
 */
public class BidSummaryFactoryTest {
    @Test
    public void testLog() throws Exception {
        BidSummary bidSummary = BidSummaryFactory.create("energy1");
        bidSummary.add(12f, .3f, new Demand(12f, 11111, null), new Date());
    }
}