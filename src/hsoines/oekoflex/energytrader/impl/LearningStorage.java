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
    private TradeRegistry energyTradeRegistry;
    private TradeRegistry powerTradeRegistry;
    private float lastAssignmentRate;
    private float lastClearedPrice;
    private float stateOfCharge;
    private float chargePower;
    private float dischargePower;
    private float energyCapacity;
    private PriceForwardCurve pfc;
	private EnergyTradeElement currentAssignment;

	public LearningStorage(final String name, final String description,final int powerMax, final int powerMin, final int chargePower, final int dischargePower, final float startStopCosts,final PriceForwardCurve priceForwardCurve,final float marginalCosts) {
		this.name = name;
		this.description = description;
		this.pfc = priceForwardCurve;
		this.chargePower = chargePower;
		this.dischargePower = dischargePower;
		this.dischargePower = this.chargePower; // Delete later, if rampUp and ramDown differ
		this.init();
    }

    public void init() {
    	// EOM/Spotmarket traderegistry
    	energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, energyCapacity, 1000);
    	
    	// Why not :
//    	energyTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE_AND_CONSUM, energyCapacity, 1000);
    	
    	// BPM traderegistry
    	powerTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE_AND_CONSUM, energyCapacity, 1000);
        setStateOfCharge(0.0f);
        
        energyCapacity = 120.0f;// Constructor setting energyCapacity???
    }

	@Override
	public float getLastAssignmentRate() {
		return this.lastAssignmentRate ;
	}

	@Override
	public List<EnergyTradeElement> getCurrentAssignments() {
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
			float maxPower = bid.getBidType()== BidType.ENERGY_DEMAND? chargePower:dischargePower;
			currentAssignment = new EnergyTradeElement(TimeUtil.getTick(currentDate),
					market, bid.getPrice(),clearedPrice, bid.getQuantity(),rate, maxPower, bid.getBidType());
			switch(bid.getBidType()){
				case ENERGY_SUPPLY:
					stateOfCharge -= (bid.getQuantity() * rate)/energyCapacity;
					break;
				case ENERGY_DEMAND:
					stateOfCharge += (bid.getQuantity() * rate)/energyCapacity;
					break;
				default:
					throw new IllegalStateException("neither ENERGY_SUPPLY nor ENERGY_DEMAND Bidtype");
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
	public void setBalancingMarketOperator(
		BalancingMarketOperator balancingMarketOperator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSpotMarketOperator(SpotMarketOperator spotMarketOperator) {
		this.eomMarketOperator = spotMarketOperator;
	}

	@Override
	public float getLastClearedPrice() {
		return lastClearedPrice;
	}

	@Override
	public void makeBidEOM() {
        makeBidEOM(TimeUtil.getCurrentTick());
	}

	@Override
	public void makeBidEOM(long currentTick) {
		int numberOfDischarge = (int)Math.floor((energyCapacity*stateOfCharge)/chargePower);
		int numberOfCharge = (int)Math.floor((energyCapacity*(1.0f-stateOfCharge))/chargePower);
		
		List<Long> lowestTicks = pfc.getTicksWithLowestPrices(numberOfDischarge, TimeUtil.getCurrentTick(), 96);
		List<Long> highestTicks =  pfc.getTicksWithHighestPrices(numberOfCharge, TimeUtil.getCurrentTick(), 96);
		
		List<Float> lowestPrices = new ArrayList<>();
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
		//Collections.reverse(highestPrices); // reverse to have list desc

		
		int minIndex = Math.min(lowestPrices.size(), highestPrices.size());
		int targetIndex = 0;
		float matchHigh = 3000.0f;
		float matchLow = -3000.0f;
		
		for(; targetIndex < minIndex ; targetIndex++){
			
			float high = highestPrices.get(targetIndex);
			float low = lowestPrices.get(targetIndex);
			
			boolean sp = checkPositiveSpread( high , low);
			
			if(sp){
				break;
			}	
		}
		
		
		if (highestPrices.size() > targetIndex ){
			matchHigh = highestPrices.get(targetIndex);	
		}
		
		if (lowestPrices.size() > targetIndex ){
			matchLow = lowestPrices.get(targetIndex);	
		}
		
		
		
		float curPrice = pfc.getPriceOnTick(currentTick);

/*		if(curPrice > matchLow && stateOfCharge*energyCapacity > dischargePower*TimeUtil.HOUR_PER_TICK){
	        eomMarketOperator.addSupply(new EnergySupply(-3000f, dischargePower*TimeUtil.HOUR_PER_TICK, this));

		}else if(curPrice < matchHigh &&  stateOfCharge*energyCapacity > chargePower*TimeUtil.HOUR_PER_TICK){
			eomMarketOperator.addDemand(new EnergyDemand(3000f, chargePower*TimeUtil.HOUR_PER_TICK, this));
		}
	*/
		if( numberOfDischarge > 0 && curPrice > matchHigh){
			eomMarketOperator.addSupply(new EnergySupply(-3000f, dischargePower*TimeUtil.HOUR_PER_TICK, this));
		}else if(numberOfCharge > 0 && curPrice < matchLow){
			eomMarketOperator.addDemand(new EnergyDemand(3000f, chargePower*TimeUtil.HOUR_PER_TICK, this));
		}
	}
	
	private boolean checkPositiveSpread(Float highMArketPrice , Float lowMarketPrice){
		
		float spread = highMArketPrice - lowMarketPrice; // TODO later impl complete formula
		
		return (spread < 0.0001f);
		
	}
	
	

	@Override
	public float getCurrentPower() {
		return currentAssignment.getOfferedQuantity()*currentAssignment.getRate()/TimeUtil.HOUR_PER_TICK;
	}

	public float getStateOfCharge() {
		return stateOfCharge;
	}

	public void setStateOfCharge(float stateOfCharge) {
		if(stateOfCharge > -0.0000001f){
			this.stateOfCharge = stateOfCharge;
		}else{
			throw new IllegalStateException("stateOfCharge would be set to negative level");
		}
	}
	
	
}