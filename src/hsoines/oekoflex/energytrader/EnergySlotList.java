package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.util.EnergyTimeZone;

import java.util.Date;

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
public interface EnergySlotList {


    enum SlotState {FREE, OFFERED, ASSIGNED;}

    enum SlotType {PRODUCE, CONSUM;}
    /*
        Adds new offered quantity to the list.
        @return the quantity added, could be null if no remaining quantity left.
     */
    int addOfferedQuantity(Date date, int quantity, final EnergyTimeZone energyTimeZone);

    /*
        Adds new assigned quantity to the list.
        @return the quantity added, if bigger than max quantity, an error is thrown.
     */
    void addAssignedQuantity(Date date, int quantity);


    void resetSlot(Date date);

//    int getSlotOfferCapacity(final Date date);
//    int getSlotOfferCapacity(Date date, EnergyTimeZone energyTimeZone);

    int getSlotAssignCapacity(final Date date);
}
