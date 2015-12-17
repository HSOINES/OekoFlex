package hsoines.oekoflex.energytrader;

import java.util.Date;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:02
 */
public interface EnergySlotList {
    enum SlotState { FREE, OFFERED, ASSIGNED}
    enum SlotType {PRODUCE, CONSUM}
    int addOfferedQuantity(Date date, int quantity);
    int addAssignedQuantity(Date date, int quantity);
    void resetSlot(Date date);
    int getSlotOfferCapacity(final Date date);
    int getSlotAssignCapacity(final Date date);
}
