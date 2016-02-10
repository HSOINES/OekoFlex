package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:27
 */
public final class PowerNegative extends PowerBid {

    public PowerNegative(float price, int quantity, MarketOperatorListener marketOperatorListener) {
        super(price, quantity, marketOperatorListener);
    }

    @Override
    public BidType getBidType() {
        return BidType.POWER_NEGATIVE;
    }
}
