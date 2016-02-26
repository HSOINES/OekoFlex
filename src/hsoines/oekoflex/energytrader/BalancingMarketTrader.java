package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface BalancingMarketTrader extends MarketTrader, MarketOperatorListener {
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BalancingMarketBidPriority)
    void makeBidBalancingMarket();

    void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator);


}
