package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import org.apache.commons.csv.CSVPrinter;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.List;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;
import static hsoines.oekoflex.domain.SequenceDefinition.EOMInterval;

public interface SpotMarketOperator extends OekoflexAgent {
	public void addDemand(EnergyDemand energyDemand);

	public void addSupply(EnergySupply supply);

	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	int getTotalClearedQuantity();

	float getLastClearedPrice();

	float getLastAssignmentRate();

	@ScheduledMethod(start = ScheduledMethod.END)
	void stop();

	List<EnergySupply> getLastSupplies();

	List<EnergyDemand> getLastEnergyDemands();

	AssignmentType getLastAssignmentType();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
