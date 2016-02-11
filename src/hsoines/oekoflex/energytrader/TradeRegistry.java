package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.util.Market;

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
public interface TradeRegistry {

    enum Type {PRODUCE, CONSUM, PRODUCE_AND_CONSUM}

    Type getType();
    /*
        Adds new assigned quantity to the History
        @return the quantity added, if bigger than max quantity, an error is thrown.
     */
    void addAssignedQuantity(final Date date, final Market market, final float offeredPrice, final float assignedPrice, final float offeredQuantity, float assignedRate, final BidType bidType);

    /* Energie, die zum entsprechenden Zeitpunkt vorhanden war */
    int getRemainingCapacity(Date date, final Market market);

    /* Energie, die zum entsprechenden Zeitpunkt abgerufen/geliefert wurde */
    float getQuantityUsed(Date date);

    /* Preis zu dem die Energie zugewiesen wurde, falls mehrere Zuweisungen  */
    List<TradeRegistryImpl.EnergyTradeElement> getEnergyTradeElements(Date date);

    /* Setzen der Kapazität */
    void setCapacity(long tick, float demand);

    /* Kapazität zum Zeitpunkt date */
    int getCapacity(Date date);

}
