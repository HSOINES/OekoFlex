package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator extends OekoflexAgent {
	void addPositiveSupply(PowerPositive supply);

	void addNegativeSupply(PowerNegative supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.RegelenergieMarketInterval, priority = SequenceDefinition.RegelenergieMarketClearingPriority)
	void clearMarket();


	long getTotalClearedPositiveQuantity();

	long getTotalClearedNegativeQuantity();

	float getLastPositiveAssignmentRate();

	float getLastClearedNegativeMaxPrice();

	float getLastNegativeAssignmentRate();

	float getLastClearedPositiveMaxPrice();
}
