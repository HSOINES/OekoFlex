package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * 
 */
public abstract class EnergyBid extends BidSupport {
    public EnergyBid(final float price, final float quantity, final MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
}
