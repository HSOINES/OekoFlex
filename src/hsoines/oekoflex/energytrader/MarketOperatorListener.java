package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Bid;

import java.util.Date;

/**
 * User: jh
 * Date: 04/12/15
 * Time: 19:12
 */
public interface MarketOperatorListener extends OekoflexAgent {
    void notifyClearingDone(float clearedPrice, float rate, Bid bid, final Date currentDate);
}
