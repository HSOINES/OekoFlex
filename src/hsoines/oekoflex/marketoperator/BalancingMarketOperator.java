package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
	Bereinigt den Regelenergiemarkt
	-> Empf�ngt Gebote als Supplies und Demands von den MarketTradern
	-> Ermittelt die Gebote, welche angenommen werden (Preis entspricht dem des Gebots)
	-> Notifiziert die MarketTrader �ber das Ergebnis ihres Angebots

	Für die Diagrammanzeige werden Getter bereitgestellt
 */
public interface BalancingMarketOperator extends OekoflexAgent {

	/*
		�bergabe der Angebote
	 */
	void addPositiveSupply(PowerPositive supply);
	void addNegativeSupply(PowerNegative supply);

	/*
		Marktr�umung, wird von Repast-Scheduler aufgerufen
	 */
	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.BalancingMarketInterval, priority = SequenceDefinition.BalancingMarketClearingPriority)
	void clearMarket();

	/*
		Getter f�r Tests
	 */
	float getTotalClearedPositiveQuantity();
	float getTotalClearedNegativeQuantity();

	/*
    	Getter f�r Diagramm
 	*/
	float getLastPositiveAssignmentRate();
	float getLastClearedNegativeMaxPrice();
	float getLastNegativeAssignmentRate();
	float getLastClearedPositiveMaxPrice();
}
