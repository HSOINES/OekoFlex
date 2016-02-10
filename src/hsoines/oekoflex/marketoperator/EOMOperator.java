package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;
import static hsoines.oekoflex.domain.SequenceDefinition.EOMInterval;

public interface EOMOperator extends OekoflexAgent {
	public void addDemand(EnergyDemand energyDemand);

	public void addSupply(EnergySupply supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	int getTotalClearedQuantity();

	float getLastClearedPrice();

	float getLastAssignmentRate();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
