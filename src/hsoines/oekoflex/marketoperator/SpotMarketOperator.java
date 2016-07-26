package hsoines.oekoflex.marketoperator;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.List;

import static hsoines.oekoflex.domain.SequenceDefinition.EOMClearingPriority;
import static hsoines.oekoflex.domain.SequenceDefinition.EOMInterval;

/*
	Bereinigt den SpotMarkt
	-> Empf�ngt Gebote als Supplies und Demands von den MarketTradern
	-> Ermittelt den Marktpreis
	-> Notifiziert die MarketTrader �ber das Ergebnis ihres Angebots

	F�r die Diagrammanzeige werden Getter bereitgestellt
 */
public interface SpotMarketOperator extends OekoflexAgent {
	/*
		�bergabe der Angebote
	 */
	 void addDemand(EnergyDemand energyDemand);
	 void addSupply(EnergySupply supply);

	/*
		Marktr�umung, wird von Repast-Scheduler aufgerufen
	 */
	@ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = EOMInterval, priority = EOMClearingPriority)
	void clearMarket();

	/*
		Getter f�r Diagramm
	 */
	float getTotalClearedQuantity();
	float getLastClearedPrice();
	float getLastAssignmentRate();

	/* 
		Cleanup 
	 */
	@ScheduledMethod(start = ScheduledMethod.END)
	void stop();

	/*
		Zugriff f�r Merrit Order Graph
	 */
	List<EnergySupply> getLastEnergySupplies();
	List<EnergyDemand> getLastEnergyDemands();
	AssignmentType getLastAssignmentType();

	enum AssignmentType{
		PartialDemand, PartialSupply, Full
	}
}
