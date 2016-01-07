package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.summary.BidSummary;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 15:39
 */
public interface MarketTrader extends OekoflexAgent {
    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();

    void setBidSummary(BidSummary bidSummary);
}
