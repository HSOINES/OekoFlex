package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * User: jh
 * Date: 10/02/16
 * Time: 21:34
 */
public abstract class PowerBid extends BidSupport {
    public PowerBid(final float price, final int quantity, final MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
}
