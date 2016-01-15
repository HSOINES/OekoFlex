package hsoines.oekoflex.bid;

import hsoines.oekoflex.energytrader.EOMOperatorListener;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 04/12/15
 * Time: 19:24
 */
public interface MarketOperatorListenerProvider  {
    EOMOperatorListener getMarketOperatorListener();
}
