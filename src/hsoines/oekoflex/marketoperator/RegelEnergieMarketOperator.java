package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator extends OekoflexAgent {
	void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketClearingPriority)
	void clearMarket();


	long getTotalClearedQuantity();

//	float getLastClearedPrice(); // nicht am Regelenergiemarkt!

	float getLastAssignmentRate();
}
