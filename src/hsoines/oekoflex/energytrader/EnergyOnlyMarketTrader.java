package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyOnlyMarketTrader extends MarketTrader {
    void setEnergieOnlyMarketOperator(EnergyOnlyMarketOperator marketOperator);

}
