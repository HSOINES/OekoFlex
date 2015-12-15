package hsoines.oekoflex;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyTrader {
    void setMarketOperator(MarketOperator marketOperator);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
