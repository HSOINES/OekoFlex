package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 15:39
 */
public interface MarketTrader extends OekoflexAgent {
    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
