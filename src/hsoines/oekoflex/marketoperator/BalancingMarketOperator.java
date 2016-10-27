package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Clears the balancing power market
 * <ul>
 * 	<li> gets bids as supplies or demands from the market traders
 * 	<li> determines the bids that are accepted 
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
public interface BalancingMarketOperator extends OekoflexAgent {

	/**
     * @param supply the positive power to add
     */
	void addPositiveSupply(PowerPositive supply);
	
	/**
     * @param negative the positive power to add
     */
	void addNegativeSupply(PowerNegative supply);

	/**
	 * market clearing, is called by the Repast scheduler 
	 */
	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BPMClearingPriority)
	void clearMarket();

	/** 
	 * Getter for Tests
	 * @return amount of positive power cleared
	 */
	float getTotalClearedPositiveQuantity();
	
	/** 
	 * Getter for Tests
	 * @return amount of negative power cleared
	 */
	float getTotalClearedNegativeQuantity();

	/** 
	 * Getter for diagram
	 * @return last positive assignment rate
	 */
	float getLastPositiveAssignmentRate();
	
	/** Getter for diagram  
	 * @return last cleared negative max price
	 */
	float getLastClearedNegativeMaxPrice();
	
	/** 
	 * Getter for diagram 
	 * @return last negative assignment rate
	 */
	float getLastNegativeAssignmentRate();
	
	/** 
	 * Getter for diagram 
	 * @return last cleared positive max price
	 */
	float getLastClearedPositiveMaxPrice();
}
