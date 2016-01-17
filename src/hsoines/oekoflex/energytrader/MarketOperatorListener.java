package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.util.Duration;

import java.util.Date;

/**
 * User: jh
 * Date: 04/12/15
 * Time: 19:12
 */
public interface MarketOperatorListener {
    void notifyClearingDone(float clearedPrice, float rate, Bid bid, final Date currentDate, final Duration duration);
}
