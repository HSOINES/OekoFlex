package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * bid for an energy supply must run  the kind of energy bid
 */
public class EnergySupplyMustRun extends EnergySupply {
	
	/**
	 * Constructor, with constructor channeling,calls the superclass PowerBid
	 * 
	 * @param price		price of this energy in [Euro/MWh]
	 * @param quantity	amount of energy in [MWh]
	 * @param marketOperatorListener listener of a market which listens to this specific bid -> energy only listener
	 */
    public EnergySupplyMustRun(float price, float quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }

    /**
     * @return returns the specific bid type, here: ENERGY_SUPPLY_MUSTRUN
     */
    @Override
    public BidType getBidType() {
        return BidType.ENERGY_SUPPLY_MUSTRUN;
    }
}
