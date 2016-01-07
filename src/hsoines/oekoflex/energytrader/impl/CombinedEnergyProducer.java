package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.*;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;

public class CombinedEnergyProducer implements EnergyProducer, MarketOperatorListener, RegelEnergieMarketTrader, EnergyOnlyMarketTrader {

    private final String name;
    private final EnergySlotList produceSlotList;
    private EnergyOnlyMarketOperator energyOnlyMarketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private RegelEnergieMarketOperator regelEnergieMarketOperator;

    public CombinedEnergyProducer(String name) {
        this.name = name;
        produceSlotList = new EnergySlotListImpl(EnergySlotList.SlotType.PRODUCE, 100);
    }

    @Override
    public void makeSupply(){
        Date currentDate = TimeUtilities.getCurrentDate();
        if (TimeUtilities.isEnergyTimeZone(EnergyTimeZone.FOUR_HOURS)) {
            int offerCapacity = produceSlotList.getSlotOfferCapacity(currentDate, EnergyTimeZone.FOUR_HOURS);
            produceSlotList.addOfferedQuantity(currentDate, offerCapacity);
            regelEnergieMarketOperator.addSupply(new Supply(100f, offerCapacity, this));
        }
        lastBidPrice = (float) (300f * Math.random()) + 500;
        int offerCapacity = produceSlotList.getSlotOfferCapacity(currentDate, EnergyTimeZone.QUARTER_HOUR);
        produceSlotList.addOfferedQuantity(currentDate, offerCapacity);
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
