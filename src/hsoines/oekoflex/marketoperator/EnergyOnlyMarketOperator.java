package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface EnergyOnlyMarketOperator {
	public void addDemand(Demand demand);
	public void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	void clearMarket();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
