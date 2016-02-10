package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

public class EnergyDemand extends EnergyBid {

    public EnergyDemand(float price, int quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }

    @Override
    public BidType getBidType() {
        return BidType.ENERGY_DEMAND;
    }
}
