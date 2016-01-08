package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.summary.BidSummary;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;

public class CombinedEnergyProducer implements MarketOperatorListener, RegelenergieMarketTrader, EOMTrader {

    private final String name;
    private EnergySlotList produceSlotList;
    private PriceStrategy regelMarktPriceStrategy;
    private ConstantPriceStrategy energyOnlyPriceStrategy;
    private EnergyOnlyMarketOperator energyOnlyMarketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private int capacity;
    private float quantityPercentageOnRegelMarkt;
    private BidSummary bidSummary;

    public CombinedEnergyProducer(String name) {
        this.name = name;
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        int offerCapacity = produceSlotList.addOfferedQuantity(date, capacity, EnergyTimeZone.QUARTER_HOUR);
        energyOnlyMarketOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date date = TimeUtilities.getCurrentDate();
        int offerCapacity = produceSlotList.addOfferedQuantity(date, (int) (capacity * quantityPercentageOnRegelMarkt), EnergyTimeZone.FOUR_HOURS);
        regelenergieMarketOperator.addSupply(new Supply(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
    }

    @Override
    public void setEnergieOnlyMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.energyOnlyMarketOperator = marketOperator;
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        produceSlotList.addAssignedQuantity(currentDate, (int) (bid.getQuantity() * rate));
        if (bidSummary != null) {
            bidSummary.add(clearedPrice, rate, bid, currentDate);
        }
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public void setRegelenergieMarketOperator(RegelEnergieMarketOperator marketOperator) {
        regelenergieMarketOperator = marketOperator;
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }

    @Override
    public float getLastBidPrice() {
        return lastBidPrice;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
        produceSlotList = new EnergySlotListImpl(EnergySlotList.SlotType.PRODUCE, capacity);
    }

    public void setPriceRegelMarkt(final float priceRegelMarkt) {
        regelMarktPriceStrategy = new ConstantPriceStrategy(priceRegelMarkt);
    }

    public void setPriceEnergyOnlyMarkt(final float priceEnergyOnlyMarkt) {
        energyOnlyPriceStrategy = new ConstantPriceStrategy(priceEnergyOnlyMarkt);

    }

    public void setQuantityPercentageOnRegelMarkt(final float quantityPercentageOnRegelMarkt) {
        this.quantityPercentageOnRegelMarkt = quantityPercentageOnRegelMarkt;
    }

    public void setBidSummary(final BidSummary bidSummary) {
        this.bidSummary = bidSummary;
    }

}
