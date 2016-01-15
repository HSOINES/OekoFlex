package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.*;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:08
 */
public final class EnergyTradeHistoryImpl implements hsoines.oekoflex.energytrader.EnergyTradeHistory {

    private final List<EnergyTradeHistoryElement> assignedSlotList;
    private final Type type;
    private final int initialcapacity;
    private final HashMap<Long, Integer> capacities;

    public EnergyTradeHistoryImpl(Type type, int initialcapacity) {
        this.type = type;
        this.initialcapacity = initialcapacity;
        assignedSlotList = new ArrayList<>();
        capacities = new HashMap<Long, Integer>();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void addAssignedQuantity(final Date date, final Duration duration, final int quantity, final float price) {
        Long slotIndex = TimeUtilities.getTick(date);
        for (int i = 0; i < duration.getTicks(); i++) {
            addQuantity(slotIndex + i, quantity, price);
        }
    }

    @Override
    public void resetSlot(final Date date) {
        long slotIndex = TimeUtilities.getTick(date);
        assignedSlotList.remove(slotIndex);
    }

    @Override
    public int getRemainingCapacity(final Date date, final Duration duration) {
        long slotIndex = TimeUtilities.getTick(date);
        int minCapacity = Integer.MAX_VALUE;
        for (int i = 0; i < duration.getTicks(); i++) {
            int capacity = getRemainingCapacity(slotIndex + i);
            if (capacity < minCapacity) {
                minCapacity = capacity;
            }
        }
        return minCapacity;
    }

    @Override
    public int getEnergyUsed(final Date date) {
        long tick = TimeUtilities.getTick(date);
        Integer capacity = getSafeAndSetInitialValue(tick);
        return capacity - getRemainingCapacity(date, Duration.QUARTER_HOUR);
    }

    Integer getSafeAndSetInitialValue(final long tick) {
        Integer capacity = capacities.get(tick);
        if (capacity == null) {
            capacity = initialcapacity;
            capacities.put(tick, initialcapacity);
        }
        return capacity;
    }

    @Override
    public List<EnergyTradeHistoryElement> getHistoryElements(final Date date) {
        return Collections.unmodifiableList(assignedSlotList);
    }

    private int getRemainingCapacity(final long tick) {
        int assigned = 0;
        for (EnergyTradeHistoryElement energyTradeHistoryElement : assignedSlotList) {
            if (energyTradeHistoryElement.getTick() == tick) {
                assigned += energyTradeHistoryElement.getQuantity();
            }
        }
        Integer capacity = getSafeAndSetInitialValue(tick);
        return capacity - assigned;
    }

    private void addQuantity(final long tick, final int quantity, final float price) {
        int remainingCapacity = getRemainingCapacity(tick);
        if (remainingCapacity < quantity) {
            throw new IllegalStateException("Assigned quantity should not exceed the maximum quantity.");
        } else {
            assignedSlotList.add(new EnergyTradeHistoryElement(price, tick, quantity));
        }
    }

    public static class EnergyTradeHistoryElement {
        float price;
        long tick;
        int quantity;

        public EnergyTradeHistoryElement(final float price, final long tick, final int quantity) {
            this.price = price;
            this.tick = tick;
            this.quantity = quantity;
        }

        public float getPrice() {
            return price;
        }

        public long getTick() {
            return tick;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
