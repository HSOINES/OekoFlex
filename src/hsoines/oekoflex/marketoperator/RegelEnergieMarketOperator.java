package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Supply;
import repast.simphony.engine.schedule.ScheduledMethod;

public interface RegelEnergieMarketOperator extends OekoflexAgent {
	void addSupply(Supply supply);

	@ScheduledMethod(start = 1, interval = 16, priority = 2)
	void clearMarket();


	long getTotalClearedQuantity();

//	float getLastClearedPrice(); // nicht am Regelenergiemarkt!

	float getLastAssignmentRate();
}
