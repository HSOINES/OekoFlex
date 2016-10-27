package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * bid for an energy supply the kind of energy bid
 */
public class EnergyDemand extends EnergyBid {

	/**
	 * Constructor, with constructor channeling,calls the superclass PowerBid
	 * 
	 * @param price		price of this energy in [Euro/MWh]
	 * @param quantity	amount of energy in [MWh]
	 * @param marketOperatorListener listener of a market which listens to this specific bid -> energy only listener
	 */
    public EnergyDemand(float price, float quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }
    
    /**
     * @return returns the specific bid type, here: ENERGY_DEMAND
     */
    @Override
    public BidType getBidType() {
        return BidType.ENERGY_DEMAND;
    }
}
