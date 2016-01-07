package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:08
 */
public final class EnergySlotListImpl implements EnergySlotList {

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
    public int addOfferedQuantity(final Date date, final int quantity, final EnergyTimeZone energyTimeZone) {
        int minOffersCap = quantity;
        long tick = TimeUtilities.getTick(date);
        for (int i = 0; i < energyTimeZone.getTicks(); i++) {
            int currentCap = getSlotOfferCapacity(tick + i);
            if (currentCap < minOffersCap) {
                minOffersCap = currentCap;
            }
        }
        for (int i = 0; i < energyTimeZone.getTicks(); i++) {
            addQuantity(tick + i, minOffersCap, offeredSlotList);
        }
        return minOffersCap;
    }

    @Override
    public void addAssignedQuantity(final Date date, final int quantity) {
        Long slotIndex = TimeUtilities.getTick(date);
        int i = addQuantity(slotIndex, quantity, assignedSlotList);
        if (i != quantity) {
            throw new IllegalStateException("Assigned quantity should not exceed the maximum quantity.");
        }
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

    int getSlotOfferCapacity(final long slotIndex) {
        return getRemainingCapacity(slotIndex, offeredSlotList);
    }

    private int getRemainingCapacity(final long slotIndex, final Map<Long, Integer> slotList) {
        if (slotList.get(slotIndex) == null) {
            slotList.put(slotIndex, 0);
            return capacity;
        } else {
            int i = capacity - slotList.get(slotIndex);
            return i >= 0 ? i : 0;
        }
    }

    private int addQuantity(final long slotIndex, final int quantity, final Map<Long, Integer> slotList) {
        int remainingCapacity = getRemainingCapacity(slotIndex, slotList);
        int currentQuantity = slotList.get(slotIndex);
        if (remainingCapacity == 0) {
            return 0;
        } else if (remainingCapacity > quantity) {
            slotList.put(slotIndex, currentQuantity + quantity);
            return quantity;
        } else {
            slotList.put(slotIndex, capacity);
            return remainingCapacity;
        }
    }
}
