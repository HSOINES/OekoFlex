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
	Bereinigt den SpotMarkt
	-> Empfängt Gebote als Supplies und Demands von den MarketTradern
	-> Ermittelt den Marktpreis
	-> Notifiziert die MarketTrader über das Ergebnis ihres Angebots

	Für die Diagrammanzeige werden Getter bereitgestellt
 */
public interface SpotMarketOperator extends OekoflexAgent {
	/**
		Übergabe der Angebote
	 */
	 void addDemand(EnergyDemand energyDemand);
	 void addSupply(EnergySupply supply);

	/**
		Markträumung, wird von Repast-Scheduler aufgerufen
	 */
	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	/**
		Getter für Diagramm
	 */
	float getTotalClearedQuantity();
	float getLastClearedPrice();
	float getLastAssignmentRate();

	/**
		Cleanup 
	 */
	@ScheduledMethod(start = ScheduledMethod.END)
	void stop();

	/**
		Zugriff für Merrit Order Graph
	 */
	List<EnergySupply> getLastEnergySupplies();
	List<EnergyDemand> getLastEnergyDemands();
	AssignmentType getLastAssignmentType();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
