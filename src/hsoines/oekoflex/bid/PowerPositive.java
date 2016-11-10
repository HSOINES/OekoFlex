package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * bid for positive power specifies the kind of Power bid
 */
public final class PowerPositive extends PowerBid {
	
	public BidType b;;

	/**
	 * Constructor, with constructor channeling,calls the superclass PowerBid
	 * 
	 * @param price		price of this power in [Euro/MW]
	 * @param quantity	amount of power in [MW]
	 * @param marketOperatorListener listener of a market which listens to this specific bid -> balancing power listener
	 */
    public PowerPositive(float price, float quantity, MarketOperatorListener marketOperatorListener,BidType bt) {
        super(price, quantity, marketOperatorListener);
        this.b = bt;
    }
    
    /**
     * @return returns the specific bid type, here: POWER_POSITIVE
     */
    @Override
    public BidType getBidType() {
        return b;
    }

}
