package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator<T extends PositiveSupply> extends OekoflexAgent {
	void addSupply(T supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketClearingPriority)
	void clearMarket();


	long getTotalClearedQuantity();

	float getLastAssignmentRate();
}
