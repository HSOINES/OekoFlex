package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 04/12/15
 * Time: 19:13
 * Demand oder Nachfrage, die von den EnergyTradern an die MarketOperator gegeben werden
 */
public interface Bid {
    MarketOperatorListener getMarketOperatorListener();

    float getPrice();

    float getQuantity();

    BidType getBidType();

}
