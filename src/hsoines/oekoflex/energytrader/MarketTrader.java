package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;

import java.util.List;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 15:39
 * Allgemeine Methoden für alle MarketTrader
 */
public interface MarketTrader extends OekoflexAgent {

    float getLastAssignmentRate();

    List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments();

    String getDescription();
}
