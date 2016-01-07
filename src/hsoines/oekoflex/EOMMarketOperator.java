package hsoines.oekoflex;

import hsoines.oekoflex.supply.Supply;
import hsoines.oekoflex.demand.Demand;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface EOMMarketOperator {
	public void addDemand(Demand demand);
	public void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	void clearMarket();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
