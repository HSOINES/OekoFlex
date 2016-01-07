package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.*;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.ConstantQuantityStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.strategies.QuantityStrategy;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;

public class CombinedEnergyProducer implements EnergyProducer, MarketOperatorListener, RegelEnergieMarketTrader, EnergyOnlyMarketTrader {

    private final String name;
    private final EnergySlotList produceSlotList;
    private final PriceStrategy regelMarktPriceStrategy;
    private final QuantityStrategy quantityStrategy;
    private final ConstantPriceStrategy energyOnlyPriceStrategy;
    private EnergyOnlyMarketOperator energyOnlyMarketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private RegelEnergieMarketOperator regelEnergieMarketOperator;

    public CombinedEnergyProducer(String name) {
        this.name = name;
        produceSlotList = new EnergySlotListImpl(EnergySlotList.SlotType.PRODUCE, 100);
        regelMarktPriceStrategy = new ConstantPriceStrategy(500f);
        energyOnlyPriceStrategy = new ConstantPriceStrategy(300f);
        quantityStrategy = new ConstantQuantityStrategy(20);
    }

    @Override
    public void makeSupply(){
        Date date = TimeUtilities.getCurrentDate();
        if (TimeUtilities.isEnergyTimeZone(EnergyTimeZone.FOUR_HOURS)) {
            int offerCapacity = produceSlotList.addOfferedQuantity(date, quantityStrategy.getQuantity(), EnergyTimeZone.FOUR_HOURS);
            regelEnergieMarketOperator.addSupply(new Supply(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
        }
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        int offerCapacity = produceSlotList.addOfferedQuantity(date, quantityStrategy.getQuantity(), EnergyTimeZone.QUARTER_HOUR);
        energyOnlyMarketOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));
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
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public void setRegelEnergieMarketOperator(final RegelEnergieMarketOperator marketOperator) {
        regelEnergieMarketOperator = marketOperator;
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
}
