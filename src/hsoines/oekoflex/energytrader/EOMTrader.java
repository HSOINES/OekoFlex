package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.summary.BidSummary;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * User: jh
 * Date: 03/12/15
 * Time: 08:29
 */
public interface EOMTrader extends MarketTrader, EOMOperatorListener {
    void setEOMOperator(EOMOperator marketOperator);

    @ScheduledMethod(start = 1, interval = SequenceDefinition.EOMInterval, priority = SequenceDefinition.EOMBidPriority)
    void makeBidEOM();

    void setEOMBidSummary(BidSummary bidSummary);


    @Override
    default void accept(final MarketTraderVisitor visitor) {
        visitor.visit(this);
    }
}
