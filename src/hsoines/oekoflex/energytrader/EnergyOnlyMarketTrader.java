package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EnergyOnlyMarketTrader extends MarketTrader {
    void setEnergieOnlyMarketOperator(EnergyOnlyMarketOperator marketOperator);

    @ScheduledMethod(start = 1, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.RegelenergieMarketBidPriority)
    void makeBidRegelenergie();

}
