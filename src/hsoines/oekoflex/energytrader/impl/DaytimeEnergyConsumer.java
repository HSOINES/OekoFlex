package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.Bid;
import hsoines.oekoflex.EOMMarketOperator;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.demand.Demand;
import hsoines.oekoflex.energytrader.EnergyConsumer;
import hsoines.oekoflex.energytrader.EnergySlotList;
import hsoines.oekoflex.util.TimeUtilities;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class DaytimeEnergyConsumer implements EnergyConsumer, MarketOperatorListener, OekoflexAgent {
    private final String name;
    private EOMMarketOperator marketOperator;
    private float clearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private EnergySlotList energySlotList;

    public DaytimeEnergyConsumer(String name) {
        this.name = name;
    }

    @Override
    public void setMarketOperator(final EOMMarketOperator marketOperator) {
        this.marketOperator = marketOperator;
        energySlotList = new EnergySlotListImpl(EnergySlotList.SlotType.CONSUM, 500);
        for (int i = 0; i < 5000; i++){
            Date date = TimeUtilities.getDate(i);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.HOUR) > 8 && calendar.get(Calendar.HOUR) < 20){
                energySlotList.addAssignedQuantity(date, 400);
                energySlotList.addOfferedQuantity(date, 400);
            }
        }
    }
    
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void makeAsk(){
        Date date = TimeUtilities.getCurrentDate();
    	if (marketOperator != null){
            lastBidPrice = (float) (100f * Math.random()) + 400;
            int quantity = energySlotList.getSlotOfferCapacity(date);
            marketOperator.addDemand(new Demand(lastBidPrice, quantity, this));
    	}
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid) {
        Date date = TimeUtilities.getCurrentDate();

        this.clearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        energySlotList.addAssignedQuantity(date, (int) Math.floor(rate * bid.getQuantity()));
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
