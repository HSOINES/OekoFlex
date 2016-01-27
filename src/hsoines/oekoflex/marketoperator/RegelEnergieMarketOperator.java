package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.NegativeSupply;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator extends OekoflexAgent {
	void addPositiveSupply(PositiveSupply supply);

	void addNegativeSupply(NegativeSupply supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketClearingPriority)
	void clearMarket();


	long getTotalClearedPositiveQuantity();

	long getTotalClearedNegativeQuantity();

	float getLastPositiveAssignmentRate();
}
