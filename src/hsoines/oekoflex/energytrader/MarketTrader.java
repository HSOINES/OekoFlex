package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;

import java.util.List;

/**
 * Allgemeine Methoden für alle MarketTrader
 */
public interface MarketTrader extends OekoflexAgent {

    float getLastAssignmentRate();

    List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments();

    String getDescription();
}
