package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.EnergyTradeHistoryImpl;

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

    List<EnergyTradeHistoryImpl.EnergyTradeHistoryElement> getCurrentAssignments();
}
