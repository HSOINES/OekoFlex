package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * Demand oder Nachfrage, die von den EnergyTradern an die MarketOperator gegeben werden
 */
public interface Bid {
    MarketOperatorListener getMarketOperatorListener();

    float getPrice();

    float getQuantity();

    BidType getBidType();

}
