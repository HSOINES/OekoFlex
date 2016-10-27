package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * Interface for the Demand of energy traders to the market operator
 */
public interface Bid {
    MarketOperatorListener getMarketOperatorListener();

    float getPrice();

    float getQuantity();

    BidType getBidType();

}
