package hsoines.oekoflex;

import hsoines.oekoflex.bid.Bid;

/**
 * User: jh
 * Date: 04/12/15
 * Time: 19:12
 */
public interface MarketOperatorListener extends OekoflexAgent{
    void notifyClearingDone(float clearedPrice, float rate, Bid bid);
}
