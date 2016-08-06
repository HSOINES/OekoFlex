package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 * Gibt Angebote an den Regelenergiemarkt
 */
public interface BalancingMarketTrader extends MarketTrader, MarketOperatorListener {
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BalancingMarketBidPriority)
    void makeBidBalancingMarket();

    /*
      This method is invoked from makeBidEOM(). This is necessary, to make it possible to invoke this method from the prerunner.
      Negative Ticks are not supported by Repast.
   */
    void makeBidBalancingMarket(long currentTick);

    void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator);


}
