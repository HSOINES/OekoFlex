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
 *
 * Quantities may be energy or power or another product, depending on the purpose.
 */
public interface TradeRegistry {

    enum Type {PRODUCE, CONSUM, PRODUCE_AND_CONSUM}

    Type getType();
    /*
        Adds new assigned quantity to the History
        @return the quantity added, if bigger than max quantity, an error is thrown.
     */
    void addAssignedQuantity(final Date date, final Market market, final float offeredPrice, final float assignedPrice, final float offeredQuantity, float assignedRate, final BidType bidType);

    /* Energy/Power available at a certain point of time */
    float getRemainingCapacity(Date date, final Market market);

    /* All Energy/Power assigned at a certain point of time */
    float getQuantityUsed(Date date);

    /* Positive Energy/Power assigned at a certain point of time */
    float getPositiveQuantityUsed(Date date);

    /* Negative Energy/Power assigned at a certain point of time */
    float getNegativeQuantityUsed(Date date);

    /* List of assigned energy/power elements*/
    List<TradeRegistryImpl.EnergyTradeElement> getEnergyTradeElements(Date date);

    void setCapacity(long tick, float demand);

    float getCapacity(Date date);

}
