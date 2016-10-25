package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * User: jh
 * Date: 17/12/15
 * Time: 08:08
 * a) Speichert die abgerufene Energie/Leistung
 * b) Speichert die Menge pro Tick (für TotalLoad)
 *
 **/
public final class TradeRegistryImpl implements TradeRegistry {
    private static final Log log = LogFactory.getLog(TradeRegistryImpl.class);

    private final List<EnergyTradeElement> tradeElements;
    private final Type type;
    private final float initialcapacity;
    private int maxElements;
    private final Map<Long, Float> capacities;
    private long latestCapacity;

    public TradeRegistryImpl(Type type, float capacity, int maxElements) {
        this.type = type;
        this.initialcapacity = capacity;
        this.maxElements = maxElements;
        tradeElements = new ArrayList<>();
        capacities = new HashMap<>();
        latestCapacity = 0;
    }

    public TradeRegistryImpl(Type type, int capacity, int maxElements, float startQuantity) {
        this(type, capacity, maxElements);
        addQuantity(-1, Market.START_VALUE, 0, 0, startQuantity, 1, BidType.START_VALUE);
    }

    @Override
    public long getLatestCapacity() {
        return latestCapacity;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void addAssignedQuantity(final Date date, final Market market, final float offeredPrice, final float assignedPrice, final float offeredQuantity, float assignedRate, final BidType bidType) {
        Long slotIndex = TimeUtil.getTick(date);
        for (int i = 0; i < market.getTicks(); i++) {
            addQuantity(slotIndex + i, market, offeredPrice, assignedPrice, offeredQuantity, assignedRate, bidType);
        }
    }

    @Override
    public float getRemainingCapacity(final Date date, final Market market) {
        long slotIndex = TimeUtil.getTick(date);
        float minCapacity = Integer.MAX_VALUE;
        for (int i = 0; i < market.getTicks(); i++) {
            float capacity = getRemainingCapacity(slotIndex + i);
            if (capacity < minCapacity) {
                minCapacity = capacity;
            }
        }
        return minCapacity;
    }

    @Override
    public float getPositiveQuantityUsed(final Date date) {
        return getQuantityUsed(date, true);
    }

    @Override
    public float getNegativeQuantityUsed(final Date date) {
        return getQuantityUsed(date, false);
    }

    @Override
    public float getQuantityUsed(final Date date) {
        return getQuantityUsed(date, true) + getQuantityUsed(date, false);
    }

    public float getQuantityUsed(final Date date, boolean positive) {
        float quantityUsed = 0f;
        List<EnergyTradeElement> energyTradeElements = getEnergyTradeElements(date);
        for (EnergyTradeElement energyTradeElement : energyTradeElements) {
            float offeredQuantity = energyTradeElement.getOfferedQuantity();
            if (positive && energyTradeElement.bidType.isPositiveAmount()) {
                quantityUsed += offeredQuantity * energyTradeElement.getRate();
            } else if (!positive && !energyTradeElement.bidType.isPositiveAmount()) {
                quantityUsed += offeredQuantity * energyTradeElement.getRate();
            }
        }
        return quantityUsed;
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
    public void setCapacity(final long tick, final float demand) {
        if (capacities.get(tick) != null) {
            log.warn("tick <" + tick + "> already assigned. Will be overriden.");
        }
        capacities.put(tick, demand);
        if (tick > latestCapacity) {
            latestCapacity = tick;
        }
    }

    @Override
    public float getCapacity(final Date date) {
        long tick = TimeUtil.getTick(date);
        Float capacity = getSafeAndSetInitialCapacity(tick);
        return capacity;
    }

    @Override
    public void duplicateCapacity(final long prerunTicks) {
        final long latestTick = getLatestCapacity();
        final long startDuplicateTick = latestTick - prerunTicks + 1;
        for (long i = 0; i < prerunTicks; i++) {
            setCapacity(i - prerunTicks, getCapacity(TimeUtil.getDate(startDuplicateTick + i)));
        }
    }

    Float getSafeAndSetInitialCapacity(final long tick) {
        Float capacity = capacities.get(tick);
        if (capacity == null) {
            capacity = initialcapacity;
        }
        return capacity;
    }

    private float getRemainingCapacity(final long tick) {
        int assigned = 0;
        for (EnergyTradeElement energyTradeElement : tradeElements) {
            if (energyTradeElement.getTick() == tick) {
                assigned += energyTradeElement.getOfferedQuantity() * energyTradeElement.getRate();
            }
        }
        Float capacity = getSafeAndSetInitialCapacity(tick);
        return capacity - assigned;
    }

    private void addQuantity(final long tick, final Market market, float offeredPrice, final float clearedprice, float offeredQuantity, final float rate, final BidType bidType) {
        float remainingCapacity = getRemainingCapacity(tick);
        float assignedQuantity = offeredQuantity * rate;
        if (remainingCapacity < assignedQuantity) {
            throw new IllegalStateException("Assigned quantity should not exceed the maximum quantity.");
        } else {
            Float capacity = capacities.get(tick);
            if (capacity == null) {
                capacity = initialcapacity;
            }
            tradeElements.add(new EnergyTradeElement(tick, market, offeredPrice, clearedprice, offeredQuantity, rate, capacity, bidType));
            if (tradeElements.size() > maxElements) tradeElements.remove(0);
        }
    }

    int getNTicks() {
        return capacities.size();
    }

    public static class EnergyTradeElement {

        private final long tick;
        private final float offeredPrice;
        private final float assignedPrice;
        private final float offeredQuantity;
        private final float rate;
        private final float capacity;
        private Market market;
        private BidType bidType;

        public EnergyTradeElement(final long tick, final Market market, final float offeredPrice, final float assignedPrice, final float offeredQuantity, final float rate, final float capacity, final BidType bidType) {
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

        public float getOfferedQuantity() {
            return offeredQuantity;
        }

        public float getRate() {
            return rate;
        }

        public float getCapacity() {
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
