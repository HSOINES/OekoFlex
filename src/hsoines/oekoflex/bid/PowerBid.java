package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * 
 */
public abstract class PowerBid extends BidSupport {
	
	/**
	 * Constructor, with constructor channeling,calls the superclass BidSupport
	 * 
	 * @param price		price of this power in [Euro/MW]
	 * @param quantity	amount of power in [MW]
	 * @param marketOperatorListener listener of a market which listens to this specific bid
	 */
    public PowerBid(final float price, final float quantity, final MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
}
