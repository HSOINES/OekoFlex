package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.energytrader.impl.EnergyTradeHistoryImpl;
import hsoines.oekoflex.util.Duration;

import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:02
 *
 * Encapsulates information about quantities offered and assigned to energyproducers.
 *
 * offered quantities: quantities are offered to a marketoperator
 * assigned quantities: quantities are assigned by a marketoperator
 */
public interface EnergyTradeHistory {

    enum Type {PRODUCE, CONSUM}

    Type getType();

    /*
        Adds new assigned quantity to the History
        @return the quantity added, if bigger than max quantity, an error is thrown.
     */
    void addAssignedQuantity(Date date, final Duration duration, int quantity, final float price);

    void resetSlot(Date date);

    /* Energy, die zum entsprechenden Zeitpunkt vorhanden war */
    int getRemainingCapacity(Date date, final Duration duration);

    /* Energy, die zum entsprechenden Zeitpunkt abgerufen wurde */
    int getEnergyUsed(Date date);

    /* Preis zu dem die Energie zugewiesen wurde, falls mehrere Zuweisungen  */
    List<EnergyTradeHistoryImpl.EnergyTradeHistoryElement> getHistoryElements(Date date);

}
