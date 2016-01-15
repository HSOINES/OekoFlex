package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.energytrader.MarketTraderVisitor;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.summary.BidSummary;
import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;

public class CombinedEnergyProducer implements RegelenergieMarketTrader, EOMTrader {

    private final String name;
    private hsoines.oekoflex.energytrader.EnergyTradeHistory produceSlotList;
    private PriceStrategy regelMarktPriceStrategy;
    private ConstantPriceStrategy energyOnlyPriceStrategy;
    private EOMOperator EOMOperator;
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
    public void accept(final MarketTraderVisitor visitor) {
        visitor.visit((EOMTrader) this);
        visitor.visit((RegelenergieMarketTrader) this);
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        int offerCapacity = produceSlotList.getRemainingCapacity(date, Duration.FOUR_HOURS);
        EOMOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date date = TimeUtilities.getCurrentDate();
        int offerCapacity = (int) (produceSlotList.getRemainingCapacity(date, Duration.FOUR_HOURS) * quantityPercentageOnRegelMarkt);
        regelenergieMarketOperator.addSupply(new Supply(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.EOMOperator = marketOperator;
    }

    @Override
    public void notifyEOMClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        produceSlotList.addAssignedQuantity(currentDate, Duration.QUARTER_HOUR, (int) (bid.getQuantity() * rate), clearedPrice);
        if (bidSummary != null) {
            bidSummary.add(clearedPrice, rate, bid, currentDate);
        }
    }

    @Override
    public void notifyRegelenergieClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        produceSlotList.addAssignedQuantity(currentDate, Duration.FOUR_HOURS, (int) (bid.getQuantity() * rate), clearedPrice);
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
    public EnergyTradeHistory getProducedEnergyTradeHistory() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
        produceSlotList = new EnergyTradeHistoryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.PRODUCE, capacity);
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

    public void setEOMBidSummary(final BidSummary bidSummary) {
        this.bidSummary = bidSummary;
    }

}
