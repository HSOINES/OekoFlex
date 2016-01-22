package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;
import static hsoines.oekoflex.domain.SequenceDefinition.EOMInterval;

public interface EOMOperator extends OekoflexAgent {
	public void addDemand(Demand demand);

	public void addSupply(PositiveSupply supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	int getTotalClearedQuantity();

	float getLastClearedPrice();

	float getLastAssignmentRate();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
