package hsoines.oekoflex;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyTrader {
    void setMarketOperator(MarketOperator marketOperator);

    float getLastAssignmentRate();

    float getLastClearedPrice();

    float getLastBidPrice();
}
