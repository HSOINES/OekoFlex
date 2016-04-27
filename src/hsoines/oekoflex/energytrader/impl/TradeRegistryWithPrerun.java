package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 27/04/16
 * Time: 19:33
 */
public final class TradeRegistryWithPrerun implements TradeRegistry {

    private final TradeRegistryImpl tradeRegistry;
    private final int prerunDays;

    public TradeRegistryWithPrerun(Type type, float capacity, int maxElements, int prerunDays) {
        this.prerunDays = prerunDays;
        tradeRegistry = new TradeRegistryImpl(type, capacity, maxElements);
    }

    @Override
    public long getLatestCapacity() {
        return tradeRegistry.getLatestCapacity();
    }

    @Override
    public Type getType() {
        return tradeRegistry.getType();
    }

    @Override
    public void addAssignedQuantity(final Date date, final Market market, final float offeredPrice, final float assignedPrice, final float offeredQuantity, final float assignedRate, final BidType bidType) {
        tradeRegistry.addAssignedQuantity(getEffectiveDate(date), market, offeredPrice, assignedPrice, offeredQuantity, assignedRate, bidType);
    }

    @Override
    public float getRemainingCapacity(final Date date, final Market market) {
        return tradeRegistry.getRemainingCapacity(getEffectiveDate(date), market);
    }

    @Override
    public float getQuantityUsed(final Date date) {
        return tradeRegistry.getQuantityUsed(getEffectiveDate(date));
    }

    @Override
    public float getPositiveQuantityUsed(final Date date) {
        return tradeRegistry.getPositiveQuantityUsed(getEffectiveDate(date));
    }

    @Override
    public float getNegativeQuantityUsed(final Date date) {
        return tradeRegistry.getNegativeQuantityUsed(getEffectiveDate(date));
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getEnergyTradeElements(final Date date) {
        return tradeRegistry.getEnergyTradeElements(getEffectiveDate(date));
    }

    @Override
    public void setCapacity(final long tick, final float capacity) {
        tradeRegistry.setCapacity(getEffectiveTick(tick), capacity);
    }

    @Override
    public float getCapacity(final Date date) {
        return tradeRegistry.getCapacity(getEffectiveDate(date));
    }

    private Date getEffectiveDate(final Date date) {
        if (date.getTime() >= TimeUtil.startDate.getTime()) {
            return date;
        }
        final long tick = TimeUtil.getTick(date);
        if (tick >= 0) {
            throw new IllegalStateException("tick must be negative: " + tick);
        }
        long currentTick = getEffectiveTick(tick);

        return TimeUtil.getDate(currentTick);
    }

    private long getEffectiveTick(final long tick) {
        if (tick >= 0) {
            return tick;
        }

        if (tick < -(prerunDays * 96)) {
            throw new IllegalStateException("tick too small: " + tick);
        }
        final long latestTick = tradeRegistry.getLatestCapacity();

        long currentTick = latestTick + tick + 1;
        if (currentTick < 0) {
            throw new IllegalStateException("Not enough data for prerun.");
        }
        return currentTick;
    }


}
