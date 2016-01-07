package hsoines.oekoflex;

import hsoines.oekoflex.bid.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator {
	void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = 3, priority = 2)
	void clearMarket();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
