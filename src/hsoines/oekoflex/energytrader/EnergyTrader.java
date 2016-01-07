package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.EnergyOnlyMarketOperator;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyTrader {
    void setMarketOperator(EnergyOnlyMarketOperator marketOperator);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
