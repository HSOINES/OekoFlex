package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;

public interface EnergyOnlyMarketOperator {
	public void addDemand(Demand demand);
	public void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = EOMClearingPriority, priority = 1)
	void clearMarket();

	int getTotalClearedQuantity();

	float getLastClearedPrice();

	float getLastAssignmentRate();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
