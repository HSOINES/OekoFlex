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
    @ScheduledMethod(start = 0, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketBidPriority)
    void makeBidRegelenergie();

    void setRegelenergieMarketOperator(RegelEnergieMarketOperator marketOperator);

    @Override
    default void accept(final MarketTraderVisitor visitor) {
        visitor.visit(this);
    }

}
