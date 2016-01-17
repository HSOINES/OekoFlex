package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:08
 */
public final class EnergyTradeRegistryImpl implements hsoines.oekoflex.energytrader.EnergyTradeHistory {
    private static final Log log = LogFactory.getLog(EnergyTradeRegistryImpl.class);

    private final List<EnergyTradeElement> tradeElements;
    private final Type type;
    private final int initialcapacity;
    private final HashMap<Long, Integer> capacities;

    public EnergyTradeRegistryImpl(Type type, int initialcapacity) {
        this.type = type;
        this.initialcapacity = initialcapacity;
        tradeElements = new ArrayList<>();
        capacities = new HashMap<Long, Integer>();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void addAssignedQuantity(final Date date, final Duration duration, final float offeredPrice, final float assignedPrice, final int offeredQuantity, float assignedRate) {
        Long slotIndex = TimeUtilities.getTick(date);
        for (int i = 0; i < duration.getTicks(); i++) {
            addQuantity(slotIndex + i, offeredPrice, assignedPrice, offeredQuantity, assignedRate);
        }
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
        }
        return capacity;
    }

    @Override
    public List<EnergyTradeElement> getEnergyTradeElements(final Date date) {
        List<EnergyTradeElement> energyTradeElements = new ArrayList<>();
        for (EnergyTradeElement energyTradeElement : tradeElements) {
            if (TimeUtilities.getTick(date) == energyTradeElement.getTick()) {
                energyTradeElements.add(energyTradeElement);
            }
        }
        return energyTradeElements;
    }

    @Override
    public void setCapacity(final long tick, final int demand) {
        if (capacities.get(tick) != null) {
            log.warn("tick <" + tick + "> already assigned. Will be overriden.");
        }
        capacities.put(tick, demand);
    }

    private int getRemainingCapacity(final long tick) {
        int assigned = 0;
        for (EnergyTradeElement energyTradeElement : tradeElements) {
            if (energyTradeElement.getTick() == tick) {
                assigned += energyTradeElement.getOfferedQuantity() * energyTradeElement.getRate();
            }
        }
        Integer capacity = getSafeAndSetInitialValue(tick);
        return capacity - assigned;
    }

    private void addQuantity(final long tick, float offeredPrice, final float clearedprice, int offeredQuantity, final float rate) {
        int remainingCapacity = getRemainingCapacity(tick);
        float assignedQuantity = offeredQuantity * rate;
        if (remainingCapacity < assignedQuantity) {
            throw new IllegalStateException("Assigned quantity should not exceed the maximum quantity.");
        } else {
            tradeElements.add(new EnergyTradeElement(tick, offeredPrice, clearedprice, offeredQuantity, rate, capacities.get(tick)));
        }
    }

    public static class EnergyTradeElement {

        private final long tick;
        private final float offeredPrice;
        private final float assignedPrice;
        private final int offeredQuantity;
        private final float rate;
        private final Integer capacity;

        public EnergyTradeElement(final long tick, final float offeredPrice, final float assignedPrice, final int offeredQuantity, final float rate, final Integer capacity) {

            this.tick = tick;
            this.offeredPrice = offeredPrice;
            this.assignedPrice = assignedPrice;
            this.offeredQuantity = offeredQuantity;
            this.rate = rate;
            this.capacity = capacity;
        }

        public long getTick() {
            return tick;
        }

        public float getOfferedPrice() {
            return offeredPrice;
        }

        public float getAssignedPrice() {
            return assignedPrice;
        }

        public int getOfferedQuantity() {
            return offeredQuantity;
        }

        public float getRate() {
            return rate;
        }

        public Integer getCapacity() {
            return capacity;
        }
    }
}
