package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.impl.BidType;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 04/12/15
 * Time: 19:13
 */
public interface Bid {
    MarketOperatorListener getMarketOperatorListener();

    float getPrice();

    int getQuantity();

    BidType getBidType();

}
