package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

public class EnergySupply extends EnergyBid {

    public EnergySupply(float price, float quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }

    @Override
    public BidType getBidType() {
        return BidType.ENERGY_SUPPLY;
    }
}
