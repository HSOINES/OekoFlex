package hsoines.oekoflex;

import hsoines.oekoflex.demand.Demand;
import hsoines.oekoflex.supply.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator {
	public void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = 3, priority = 2)
	void clearMarket();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
