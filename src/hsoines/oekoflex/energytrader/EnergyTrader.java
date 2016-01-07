package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.EOMMarketOperator;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyTrader {
    void setMarketOperator(EOMMarketOperator marketOperator);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
