package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
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
public final class EnergyTradeRegistryImpl implements EnergyTradeRegistry {
    private static final Log log = LogFactory.getLog(EnergyTradeRegistryImpl.class);

    private final List<EnergyTradeElement> tradeElements;
    private final Type type;
    private final int initialcapacity;
    private final HashMap<Long, Integer> capacities;

    public EnergyTradeRegistryImpl(Type type, int initialcapacity) {
        this.type = type;
        this.initialcapacity = initialcapacity;
        tradeElements = new ArrayList<>();
        capacities = new HashMap<>();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void addAssignedQuantity(final Date date, final Market market, final float offeredPrice, final float assignedPrice, final int offeredQuantity, float assignedRate, final BidType bidType) {
        Long slotIndex = TimeUtil.getTick(date);
        for (int i = 0; i < market.getTicks(); i++) {
            addQuantity(slotIndex + i, market, offeredPrice, assignedPrice, offeredQuantity, assignedRate, bidType);
        }
    }

    @Override
    public int getRemainingCapacity(final Date date, final Market market) {
        long slotIndex = TimeUtil.getTick(date);
        int minCapacity = Integer.MAX_VALUE;
        for (int i = 0; i < market.getTicks(); i++) {
            int capacity = getRemainingCapacity(slotIndex + i);
            if (capacity < minCapacity) {
                minCapacity = capacity;
            }
        }
        return minCapacity;
    }

    @Override
    public int getQuantityUsed(final Date date) {
        long tick = TimeUtil.getTick(date);
        Integer capacity = getSafeAndSetInitialCapacity(tick);
        return capacity - getRemainingCapacity(date, Market.EOM_MARKET);
    }

    @Override
    public List<EnergyTradeElement> getEnergyTradeElements(final Date date) {
        List<EnergyTradeElement> energyTradeElements = new ArrayList<>();
        for (EnergyTradeElement energyTradeElement : tradeElements) {
            if (TimeUtil.getTick(date) == energyTradeElement.getTick()) {
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

    @Override
    public int getCapacity(final Date date) {
        long tick = TimeUtil.getTick(date);
        Integer capacity = getSafeAndSetInitialCapacity(tick);
        return capacity;
    }

    Integer getSafeAndSetInitialCapacity(final long tick) {
        Integer capacity = capacities.get(tick);
        if (capacity == null) {
            capacity = initialcapacity;
        }
        return capacity;
    }

    private int getRemainingCapacity(final long tick) {
        int assigned = 0;
        for (EnergyTradeElement energyTradeElement : tradeElements) {
            if (energyTradeElement.getTick() == tick) {
                assigned += energyTradeElement.getOfferedQuantity() * energyTradeElement.getRate();
            }
        }
        Integer capacity = getSafeAndSetInitialCapacity(tick);
        return capacity - assigned;
    }

    private void addQuantity(final long tick, final Market market, float offeredPrice, final float clearedprice, int offeredQuantity, final float rate, final BidType bidType) {
        int remainingCapacity = getRemainingCapacity(tick);
        float assignedQuantity = (float) Math.floor(offeredQuantity * rate);
        if (remainingCapacity < assignedQuantity) {
            throw new IllegalStateException("Assigned quantity should not exceed the maximum quantity.");
        } else {
            Integer capacity = capacities.get(tick);
            if (capacity == null) {
                capacity = initialcapacity;
            }
            tradeElements.add(new EnergyTradeElement(tick, market, offeredPrice, clearedprice, offeredQuantity, rate, capacity, bidType));
        }
    }

    public static class EnergyTradeElement {

        private final long tick;
        private final float offeredPrice;
        private final float assignedPrice;
        private final int offeredQuantity;
        private final float rate;
        private final Integer capacity;
        private Market market;
        private BidType bidType;

        public EnergyTradeElement(final long tick, final Market market, final float offeredPrice, final float assignedPrice, final int offeredQuantity, final float rate, final int capacity, final BidType bidType) {
            this.tick = tick;
            this.market = market;
            this.offeredPrice = offeredPrice;
            this.assignedPrice = assignedPrice;
            this.offeredQuantity = offeredQuantity;
            this.rate = rate;
            this.capacity = capacity;
            this.bidType = bidType;
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

        public int getCapacity() {
            return capacity;
        }

        public Market getMarket() {
            return market;
        }

        public BidType getBidType() {
            return bidType;
        }
    }
}
