package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * User: jh
 * Date: 10/02/16
 * Time: 21:33
 */
public abstract class EnergyBid extends BidSupport {
    public EnergyBid(final float price, final int quantity, final MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
}
