package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.EOMOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EOMTrader extends MarketTrader, MarketOperatorListener {
    void setEOMOperator(EOMOperator eomOperator);

    float getLastClearedPrice();

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.EOMBidPriority)
    void makeBidEOM();


}
