package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface RegelenergieMarketTrader extends MarketTrader, MarketOperatorListener {
    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketBidPriority)
    void makeBidRegelenergie();

    void setRegelenergieMarketOperator(RegelEnergieMarketOperator regelenergieMarketOperator);


}
