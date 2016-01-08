package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface RegelenergieMarketTrader extends OekoflexAgent {
    void setMarketOperator(RegelEnergieMarketOperator marketOperator);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
