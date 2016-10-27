package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Gibt Angebote an den Regelenergiemarkt
 */
public interface BalancingMarketTrader extends MarketTrader, MarketOperatorListener {
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BPMBidPriority)
    void makeBidBalancingMarket();

    /*
      This method is invoked from makeBidEOM(). This is necessary, to make it possible to invoke this method from the prerunner.
      Negative Ticks are not supported by Repast.
   */
    void makeBidBalancingMarket(long currentTick);

    void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator);


}
