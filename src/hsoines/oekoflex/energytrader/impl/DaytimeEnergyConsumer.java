package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.energytrader.EnergyConsumer;
import hsoines.oekoflex.energytrader.EnergyOnlyMarketTrader;
import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class DaytimeEnergyConsumer implements EnergyConsumer, MarketOperatorListener, EnergyOnlyMarketTrader {
    private final String name;
    private EnergyOnlyMarketOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private EnergySlotList consumeSlots;

    public DaytimeEnergyConsumer(String name) {
        this.name = name;
    }

    @Override
    public void setEnergieOnlyMarketOperator(final EnergyOnlyMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
        consumeSlots = new EnergySlotListImpl(EnergySlotList.SlotType.CONSUM, 500);
        for (int i = 0; i < 5000; i++){
            Date date = TimeUtilities.getDate(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.HOUR_OF_DAY) > 8 && calendar.get(Calendar.HOUR_OF_DAY) < 20) {
                consumeSlots.addOfferedQuantity(date, 0, EnergyTimeZone.QUARTER_HOUR);
            } else {
                consumeSlots.addOfferedQuantity(date, 250, EnergyTimeZone.QUARTER_HOUR);
            }
        }
    }
    
    @Override
    public void makeDemand(){
        Date date = TimeUtilities.getCurrentDate();
    	if (marketOperator != null){
            lastBidPrice = (float) (100f * Math.random()) + 400;
            int quantity = consumeSlots.addOfferedQuantity(date, 500, EnergyTimeZone.QUARTER_HOUR);
            marketOperator.addDemand(new Demand(lastBidPrice, quantity, this));
        }
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {
        Date date = TimeUtilities.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        consumeSlots.addAssignedQuantity(date, (int) Math.floor(rate * bid.getQuantity()));
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    public float getLastClearedPrice() {
        return clearedPrice;
    }

    public float getLastBidPrice() {
        return lastBidPrice;
    }


    @Override
    public String getName() {
        return name;
    }
}
