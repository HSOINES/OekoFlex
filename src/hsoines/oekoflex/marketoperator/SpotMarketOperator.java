package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.List;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;
import static hsoines.oekoflex.domain.SequenceDefinition.EOMInterval;

/**
 * Interface for the EOM Markettrader
 * Clears the energy only market:
 * <ul>
 * 	<li> gets bids as supplies or demands from the market traders
 * 	<li> determines the bids and marketprice that are accepted 
 * 	<li> notifies the market traders that their bids are accepted or denied
 * </ul>
 * <p>
 * <p>
 * Furthermore has getter functions for:
 * <ul>
 * 	<li> JUnit tests, and
 * 	<li> the diagram
 * </ul>
 */
public interface SpotMarketOperator extends OekoflexAgent {
	
	/**
     * @param energyDemand the energy demand to add
     */
	 void addDemand(EnergyDemand energyDemand);
	 
	 /**
	  * 
	  * @param supply the energy supply to add
	  */
	 void addSupply(EnergySupply supply);

	/**
	 *	market clearing, is called by the Repast scheduler 
	 */
	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	/** 
	 * Getter for diagram
	 * @return total cleared quantity
	 */
	float getTotalClearedQuantity();
	
	/** 
	 * Getter for diagram
	 * @return last cleared price
	 */
	float getLastClearedPrice();
	
	/** 
	 * Getter for diagram
	 * @return last  assignment rate
	 */
	float getLastAssignmentRate();

	/** 
	 * Getter for diagram
	 * @return last  energy supplies
	 */
	List<EnergySupply> getLastEnergySupplies();
	
	/** 
	 * Getter for diagram
	 * @return last  energy demands
	 */
	List<EnergyDemand> getLastEnergyDemands();
	
	/** 
	 * Getter for diagram
	 * @return last  assignment type
	 */
	AssignmentType getLastAssignmentType();
	
	/**
		Cleanup 
	 */
	@ScheduledMethod(start = ScheduledMethod.END)
	void stop();
	
	
	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
