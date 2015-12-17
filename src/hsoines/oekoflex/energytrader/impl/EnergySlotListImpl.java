package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:08
 */
public final class EnergySlotListImpl implements EnergySlotList{

    private final Map<Long, Integer> offeredSlotList;
    private final Map<Long, Integer> assignedSlotList;
    private final SlotType slotType;
    private final int capacity;

    public EnergySlotListImpl(SlotType slotType, int capacity) {
        this.slotType = slotType;
        this.capacity = capacity;
        offeredSlotList = new HashMap<>();
        assignedSlotList = new HashMap<>();
    }

    @Override
    public int addOfferedQuantity(final Date date, final int quantity) {
        return addQuantity(date, quantity, offeredSlotList);
    }

    @Override
    public int addAssignedQuantity(final Date date, final int quantity) {
        return addQuantity(date, quantity, assignedSlotList);
    }

    @Override
    public void resetSlot(final Date date) {
        long slotIndex = TimeUtilities.getTick(date);
        offeredSlotList.remove(slotIndex);
        assignedSlotList.remove(slotIndex);
    }

    @Override
    public int getSlotAssignCapacity(final Date date) {
        long slotIndex = TimeUtilities.getTick(date);
        return getRemainingCapacity(slotIndex, assignedSlotList);
    }

    @Override
    public int getSlotOfferCapacity(final Date date) {
        long slotIndex = TimeUtilities.getTick(date);
        return getRemainingCapacity(slotIndex, offeredSlotList);
    }

    private int getRemainingCapacity(final long slotIndex, final Map<Long, Integer> slotList) {
        if (slotList.get(slotIndex) == null){
        	slotList.put(slotIndex, 0);
            return capacity;
        } else {
            int i = capacity - slotList.get(slotIndex);
            return i > 0 ? i:0;
        }
    }

    private int addQuantity(final Date date, final int quantity, final Map<Long, Integer> slotList) {
        Long slotIndex = TimeUtilities.getTick(date);
        int remainingCapacity = getRemainingCapacity(slotIndex, slotList);
        int currentQuantity = slotList.get(slotIndex);
        if (remainingCapacity == 0){
            return 0;
        } else if (remainingCapacity > quantity){
            slotList.put(slotIndex, currentQuantity + quantity);
            return quantity;
        } else {
            slotList.put(slotIndex, capacity);
            return remainingCapacity;
        }
    }
}
