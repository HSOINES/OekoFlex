package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.Bid;
import hsoines.oekoflex.EnergyOnlyMarketOperator;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.RegelEnergieMarketOperator;
import hsoines.oekoflex.energytrader.EnergyOnlyMarketTrader;
import hsoines.oekoflex.energytrader.EnergyProducer;
import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.energytrader.RegelEnergieMarketTrader;
import hsoines.oekoflex.supply.Supply;
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
        produceSlotList = new EnergySlotListImpl(EnergySlotList.SlotType.PRODUCE, 10000);
    }

    @Override
    public void makeSupply(){
        Date currentDate = TimeUtilities.getCurrentDate();
        if (TimeUtilities.isEnergyTimeZone(EnergyTimeZone.FOUR_HOURS)) {
            int offerCapacity = produceSlotList.getSlotOfferCapacity(currentDate, EnergyTimeZone.FOUR_HOURS);
            regelEnergieMarketOperator.addSupply(new Supply(100f, offerCapacity, this));
        }
        lastBidPrice = (float) (300f * Math.random()) + 500;
        int offerCapacity = produceSlotList.getSlotOfferCapacity(currentDate, EnergyTimeZone.QUARTER_HOUR);
        energyOnlyMarketOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));

    }

    @Override
    public void setMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.energyOnlyMarketOperator = marketOperator;
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public void setMarketOperator(final RegelEnergieMarketOperator marketOperator) {
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
