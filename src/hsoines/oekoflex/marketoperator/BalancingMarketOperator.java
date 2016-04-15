package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface BalancingMarketOperator extends OekoflexAgent {
	void addPositiveSupply(PowerPositive supply);

	void addNegativeSupply(PowerNegative supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BalancingMarketClearingPriority)
	void clearMarket();


	float getTotalClearedPositiveQuantity();

	float getTotalClearedNegativeQuantity();

	float getLastPositiveAssignmentRate();

	float getLastClearedNegativeMaxPrice();

	float getLastNegativeAssignmentRate();

	float getLastClearedPositiveMaxPrice();
}
