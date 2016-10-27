package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * bid for negative power specifies the kind of Power bid
 */
public final class PowerNegative extends PowerBid {
	
	/**
	 * Constructor, with constructor channeling,calls the superclass PowerBid
	 * 
	 * @param price		price of this power in [Euro/MW]
	 * @param quantity	amount of power in [MW]
	 * @param marketOperatorListener listener of a market which listens to this specific bid
	 */
    public PowerNegative(float price, float quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
    
    /**
     * @return returns the specific bid type, here: POWER_POSITIVE
     */
    @Override
    public BidType getBidType() {
        return BidType.POWER_NEGATIVE;
    }
}
