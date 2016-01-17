package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.test.EnergyTradeRegistryImpl;

import java.util.List;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 15:39
 */
public interface MarketTrader extends OekoflexAgent {
    void accept(MarketTraderVisitor visitor);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments();
}
