package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * 
 */
public abstract class EnergyBid extends BidSupport {
	
	/**
	 * Constructor, with constructor channeling,calls the superclass BidSupport
	 * 
	 * @param price		price of this power in [Euro/MWh]
	 * @param quantity	amount of power in [MWh]
	 * @param marketOperatorListener listener of a market which listens to this specific bid -> energy only listener
	 */
    public EnergyBid(final float price, final float quantity, final MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
}
