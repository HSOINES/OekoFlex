package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.util.Market;

import java.util.Date;

/**
 * User: jh
 * Date: 04/12/15
 * Time: 19:12
 */
public interface MarketOperatorListener extends OekoflexAgent {
    void notifyClearingDone(final Date currentDate, final Market market, Bid bid, float clearedPrice, float rate);

    String getName();
}
