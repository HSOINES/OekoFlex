package hsoines.oekoflex.energytrader.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl.EnergyTradeElement;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

public class LearningStorage implements EOMTrader, BalancingMarketTrader{
	
	private final String name;
    private final String description;
    private SpotMarketOperator eomMarketOperator;
    private float stateOfCharge;					// Percentage how full/empty the storage is
    private float chargePower;						// Power that can be charged in 15 Minutes
    private float dischargePower;					// Power that can be discharged in 15 Minutes
    private float energyCapacity;					// Whole amount of Power that can stored in stored
    private PriceForwardCurve pfc;					// PriceForwardCurve for current tick
	private EnergyTradeElement currentAssignment; 	// only last assignment is needed so this replaces the EOM TradeRegistry 
	
	// chargePower and disChargePower is measured as MW  so eg 10MWh/15min = 40 MW
	LearningStorage(final String name, final String description, final int chargePower, final int dischargePower, final float startStopCosts,final PriceForwardCurve priceForwardCurve,final float marginalCosts ,final float energyCapacity , final float stateOfCharge) {
		this.name = name;
		this.description = description;			
		this.pfc = priceForwardCurve;			
		this.chargePower = chargePower;			
		this.dischargePower = dischargePower;	
		this.energyCapacity = energyCapacity;	
		this.stateOfCharge = stateOfCharge;		
    }
	
	 public LearningStorage(final String name, final String description, final int chargePower, final int dischargePower, final float startStopCosts,final PriceForwardCurve priceForwardCurve,final float marginalCosts ,final float energyCapacity ) {
		 this(name,description,chargePower,dischargePower,startStopCosts,priceForwardCurve,marginalCosts,energyCapacity,0);
	 }
	
	@Override
	public float getLastAssignmentRate() {
		return this.currentAssignment.getRate() ;
	}

	@Override
	public List<EnergyTradeElement> getCurrentAssignments() {
		if(currentAssignment.getBidType() == BidType.NULL_BID){
			return new ArrayList<EnergyTradeElement>();
		}
		return Collections.singletonList(currentAssignment);
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void notifyClearingDone(Date currentDate, Market market, Bid bid, float clearedPrice, float rate) {
		
		if(market.equals(Market.SPOT_MARKET)){
			
			BidType currentBidType = bid.getBidType();
			float currentQuantity  = bid.getQuantity();		
			
			switch(currentBidType){
				case ENERGY_SUPPLY:
					this.setStateOfCharge(stateOfCharge - (currentQuantity * rate)/energyCapacity);
					currentAssignment = new EnergyTradeElement(TimeUtil.getTick(currentDate),market, bid.getPrice(),clearedPrice, currentQuantity ,rate, dischargePower, currentBidType);
					break;
				case ENERGY_DEMAND:
					this.setStateOfCharge(stateOfCharge + (currentQuantity * rate)/energyCapacity);
					currentAssignment = new EnergyTradeElement(TimeUtil.getTick(currentDate),market, bid.getPrice(),clearedPrice, currentQuantity ,rate, chargePower, currentBidType);
					break;
				default:
					throw new IllegalStateException("Neither ENERGY_SUPPLY nor ENERGY_DEMAND Bidtype, BidType is: " + currentBidType.toString());
			}
		}
	}

	@Override
	public void makeBidBalancingMarket() {
		makeBidBalancingMarket(TimeUtil.getCurrentTick());
	}

	@Override
	public void makeBidBalancingMarket(long currentTick) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void setSpotMarketOperator(SpotMarketOperator spotMarketOperator) {
		this.eomMarketOperator = spotMarketOperator;
	}
	
	@Override
	public float getLastClearedPrice() {
		return currentAssignment.getAssignedPrice();

	}

	@Override
	public void makeBidEOM() {
        makeBidEOM(TimeUtil.getCurrentTick());
	}

	@Override
	public void makeBidEOM(long currentTick) {
		this.currentAssignment = new EnergyTradeElement(currentTick, Market.SPOT_MARKET, 0,0, 0 ,0, 0, BidType.NULL_BID);
		
		int numberOfDischarge = (int)Math.floor((energyCapacity*stateOfCharge)/(dischargePower*TimeUtil.HOUR_PER_TICK));		// Number of full Units  (that can be discharged before the storage is empty)
		int numberOfCharge = (int)Math.floor((energyCapacity*(1.0f-stateOfCharge))/(chargePower*TimeUtil.HOUR_PER_TICK));		// Number of empty Units (that can be charged before the storage is empty)
		
		List<Long> lowestTicks  = pfc.getTicksWithLowestPrices(numberOfCharge, TimeUtil.getCurrentTick(), 96);
		List<Long> highestTicks =  pfc.getTicksWithHighestPrices(numberOfDischarge , TimeUtil.getCurrentTick(), 96);
		
		List<Float> lowestPrices  = new ArrayList<>();
		List<Float> highestPrices = new ArrayList<>();
		
		for (Long lowestTick : lowestTicks){
			lowestPrices.add(pfc.getPriceOnTick(lowestTick));
		}
		
		for (Long highestTick : highestTicks){
			highestPrices.add(pfc.getPriceOnTick(highestTick));
		}
		
		lowestPrices.sort(Float::compare); // sorts asc
		Collections.reverse(lowestPrices); // reverse to have list desc
		highestPrices.sort(Float::compare);// sorts asc
		
		int minIndex = Math.min(lowestPrices.size()-1, highestPrices.size()-1);
		int targetIndex = 0;
		float matchHigh = 3000.0f;
		float matchLow = -3000.0f;
		
		for(; targetIndex < minIndex ; targetIndex++){
			
			float high = highestPrices.get(targetIndex);
			float low = lowestPrices.get(targetIndex);
			
			boolean sp = checkPositiveSpread(high, low);
			
			if(sp){
				break;
			}	
		}
		
		
		if (highestPrices.size() > targetIndex ){
			matchHigh = highestPrices.get(targetIndex);	
		}
		
		if (lowestPrices.size() > targetIndex){
			matchLow = lowestPrices.get(targetIndex);	
		}
		
		float curPrice = pfc.getPriceOnTick(currentTick);
		
		
		if( numberOfDischarge > 0 && curPrice > matchHigh){
			eomMarketOperator.addSupply(new EnergySupply(-3000f, dischargePower*TimeUtil.HOUR_PER_TICK, this));
		}else if(numberOfCharge > 0 && curPrice < matchLow){
			eomMarketOperator.addDemand(new EnergyDemand(3000f, chargePower*TimeUtil.HOUR_PER_TICK, this));
		}
	}
	
	// Checks spread for current high/low price Combination and returns true if spread > 0
	private boolean checkPositiveSpread(Float highMarketPrice , Float lowMarketPrice){
		
		float spread = highMarketPrice - lowMarketPrice; // TODO later implement complete formula
		
		return (spread > 0.0001f);
		
	}
	
	@Override
	public float getCurrentPower() {
		return currentAssignment.getOfferedQuantity()*currentAssignment.getRate()/TimeUtil.HOUR_PER_TICK;
	}

	public float getStateOfCharge() {
		return stateOfCharge;
	}

	public void setStateOfCharge(float stateOfCharge) {
		if(stateOfCharge > -0.0000001f && stateOfCharge <= 1.0f){
			this.stateOfCharge = stateOfCharge;
		}else{
			throw new IllegalStateException("stateOfCharge would be set to an illegal level. Would be set to: " + stateOfCharge);
		}
	}
	
}