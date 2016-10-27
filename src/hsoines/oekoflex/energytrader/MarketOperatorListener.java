package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.util.Market;

import java.util.Date;

/**
 * Callback für die bearbeiteten Angebote der EnergyTrader
 */
public interface MarketOperatorListener extends OekoflexAgent {
    void notifyClearingDone(final Date currentDate, final Market market, Bid bid, float clearedPrice, float rate);

    String getName();
}
