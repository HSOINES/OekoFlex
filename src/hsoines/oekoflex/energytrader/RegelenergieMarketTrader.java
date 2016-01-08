package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface RegelenergieMarketTrader extends OekoflexAgent {
    @ScheduledMethod(start = 1, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketBidPriority)
    void makeBidRegelenergie();

    void setRegelenergieMarketOperator(RegelEnergieMarketOperator marketOperator);

    float getLastClearedPrice();

    float getLastAssignmentRate();

    float getLastBidPrice();
}
