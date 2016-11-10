package hsoines.oekoflex.energytrader.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.bid.EnergySupplyMustRun;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl.EnergyTradeElement;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
/**
 * texty text for class
 * 
 */
public class FlexPowerplant3 implements EOMTrader, BalancingMarketTrader, MarketOperatorListener {
	
	private static final Log log = LogFactory.getLog(FlexPowerplant3.class);
	
	/** for BPM, trade per 5 minutes */
	static final float LATENCY = 3f;				
	
	/** name of the flexible power plant */
	private final String name;
	
	/** description of the flexible power plant */
	private final String description;
	
	/** maximum  amount of power for this flexible power plant*/
	private final int powerMax;
	
	/** minimum amount of power for this flexible power plant  */
	private final int powerMin;
	
	/** amount of power the plant can go up over the time of one tick*/
	private final int powerRampUp;
	
	/** amount of power the plant can go down over the time of one tick*/
	private final int powerRampDown;
	
	/** specific operator which handles the balancing power market */
	private BalancingMarketOperator balancingMarketOperator;
	
	/** specific operator which handles the energy only market */
	private SpotMarketOperator eomMarketOperator;
	
	/** registry to keep all the information about energy bids of this plant  */
	private TradeRegistry energyTradeRegistry;
	
	/** registry to keep all the information about power bids of this plant  */
	private TradeRegistry powerTradeRegistry;
	
	/** the price forward curve for this scenario */
	private final PriceForwardCurve priceForwardCurve;
	
	/** rate of last assignment by market operator as percentage  of the last bid */
	private float lastAssignmentRate;
	
	/** price of last assignment by market operator as percentage  of the last bid */
	private float lastClearedPrice;
	
	/** marginal costs of this specific power plant, determined only once when constructed */
	private final float marginalCosts;
	
	/** specific costs to start the flexible power plan, measured in [Euro/MW] used as euro per Nennleistung */
	private final float cost_startUp;
	
	/** specific costs to shut down the flexible power plan, measured in [Euro/MW] used as euro per Nennleistung*/
	private final float cost_shutDown;
	
	private final float dt   = 0.25f;
	private final float dtau = 4.0f;
	
	private float powerPreceding;
	
	private float negativeQuantityAssignedBPM ;
	float positiveQuantityAssignedBPM;
	
	/**
	 * Constructor 1
	 */ 
	public FlexPowerplant3(final String name, final String description, final int powerMax, final int powerMin, final float efficiency,	final int powerRampUp, final int powerRampDown,	final PriceForwardCurve priceForwardCurve, final float variableCosts, final float fuelCosts, final float co2CertificateCosts, final float emissionRate, final float cost_startUp , final float cost_shutDown)  {
		this(name, description, powerMax, powerMin, powerRampUp, powerRampDown, priceForwardCurve, FlexPowerplant3.calculateMarginalCosts(variableCosts, fuelCosts, co2CertificateCosts, emissionRate, efficiency), cost_startUp, cost_shutDown);
	}

	/**
	 * to calculate the marginal costs of the plant
	 *
	 * @return      marginal costs as [â‚¬/MWh]
	 */ 
	static float calculateMarginalCosts(final float variableCosts, final float startStopCosts, final float co2CertificateCosts, final float emissionRate, final float efficiency) {
		return startStopCosts / efficiency + (co2CertificateCosts * emissionRate / efficiency) + variableCosts;
	}
	
	/**
	 * Constructor 2
	 */ 
	FlexPowerplant3(final String name, final String description, final int powerMax, final int powerMin, final int powerRampUp,	final int powerRampDown, final PriceForwardCurve priceForwardCurve, final float marginalCosts, final float cost_startUp , final float cost_shutDown) {
		this.name = name;
		this.description 			= description;
		this.powerMax 				= powerMax;
		this.powerMin 				= powerMin;
		this.powerRampUp 			= powerRampUp;
		this.powerRampDown 			= powerRampDown;
		this.priceForwardCurve 		= priceForwardCurve;
		this.marginalCosts 			= marginalCosts;
		this.cost_startUp			= cost_startUp;
		this.cost_shutDown			= cost_shutDown;
		this.energyTradeRegistry 	= new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax, 1000, powerMin * TimeUtil.HOUR_PER_TICK); // TODO: Look up!!!
        this.powerTradeRegistry  	= new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, powerMax, 1000);
	}
	
	
	/**
	 * @return      last assignment rate
	 */ 
	@Override
	public float getLastAssignmentRate() {
		return lastAssignmentRate;
	}
	
	/**
	 *
	 * @return      the last/current Assignments of power and of energy elements
	 */ 
	@Override
	public List<EnergyTradeElement> getCurrentAssignments() {
        List<TradeRegistryImpl.EnergyTradeElement> powerTradeElements = powerTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        List<TradeRegistryImpl.EnergyTradeElement> energyTradeElements = energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
        powerTradeElements.addAll(energyTradeElements);
        return powerTradeElements;
	}
	
	/**
	 * @return       string containing description of particular flexible power plant
	 */ 
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return      string containing name of particular flexible power plant
	 */ 
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * This function is called by the marketoperator to inform every agent that is market is cleared and what has become their individual bids.
	 *
	 * @param currentDate  	current Date
	 * @param market  	   	the market, either EOM or BPM 
	 * @param bid			the bid we made 
	 * @param clearedPrice	cleared price for this date and our bid 
	 * @param rate			cleared rate of our bid amount for this date 
	 */ 
	@Override
	public void notifyClearingDone(Date currentDate, Market market, Bid bid, float clearedPrice, float rate) {
		switch (bid.getBidType()) {
			case ENERGY_SUPPLY_MUSTRUN:
				if (rate < 0.0001f) { return; }  	// Schrott zur Absicherung
				if (1 - rate > 0.00001f) {			
					log.error("rate of MUSTRUN < 1: " + rate + ", Plant: " + getName());
					energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), 0, bid.getQuantity(), 1 - rate, BidType.ENERGY_SUPPLY_MUSTRUN_COMPLEMENT); // Warum 1-rate???
				}									// Ende vom Schrott zur Absicherung
			case ENERGY_SUPPLY:
				energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
				break;
			case POWER_NEGATIVE_ARBEITSPREIS:
			case POWER_POSITIVE_ARBEITSPREIS:
				powerTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
				break;
			case POWER_NEGATIVE:
				negativeQuantityAssignedBPM = bid.getQuantity()*rate;
				break;
			case POWER_POSITIVE:
				positiveQuantityAssignedBPM =  bid.getQuantity()*rate;
				break;
			default:
				throw new IllegalStateException("No matching Bidtype, BidType is: " + bid.getBidType());
		}
		
		calculateAndSetPowerPreceding();   // EOM Market responded, so we calculate Power Preceding
		
		if (market.equals(Market.SPOT_MARKET)) {
			this.lastClearedPrice = clearedPrice;
			this.lastAssignmentRate = rate;
		}
	}

	/**
	 * Will be called by the BPM market-operator to invite the agents to  make bids.
	 * Calls makeBidBalancingMarket(long currentTick)
	 */ 
	@Override
	public void makeBidBalancingMarket() {
		Date currentDate = TimeUtil.getCurrentDate();
        makeBidBalancingMarket(TimeUtil.getTick(currentDate));
	}

	/**
	 * Makes 0,1 or 2 bids in the Balancing Power market. 
	 * <p>
	 * <ul>
	 * 		<li> One bid for positive balancing power
	 * 		<li> and one for negative balancing power.
	 * </ul>
	 * 
	 * This function is the result of the market operator calling the agents within in the market.
	 * 
	 * @param currentTick  tick in which the bid takes place
	 */ 
	@Override
    public void makeBidBalancingMarket(long currentTick) {
				
		Date currentDate = TimeUtil.getDate(currentTick);
		Date precedingDate = TimeUtil.precedingDate(currentDate); // precedingDate = currentDate plus 15min 
	
		

		float pfcCostsAverage = priceForwardCurve.avgPriceOverTicks(currentTick, Market.BALANCING_MARKET.getTicks());//  Market.BALANCING_MARKET.getTicks() is equivalent to 16
		
		float pRampUp   = (powerRampUp   / LATENCY);
		float pRampDown = (powerRampDown / LATENCY);
		
		
		// 1.1 Bestimmung Gebotsmenge am Regelenergiemarkt - positive Regelleistung
		float mengeRegelleistungPpos = Math.min(powerMax - powerPreceding, pRampUp);;
		
		if (mengeRegelleistungPpos < 5) {
			mengeRegelleistungPpos = 0;
		}
		
		if (mengeRegelleistungPpos > 0) {
			// 1.2 Bestimmung Leistungspreis am Regelenergiemarkt - positive Regelleistung
			float leistungspreisPpos = Math.max((pfcCostsAverage - marginalCosts) * dtau, 0)+ Math.abs(Math.min(((pfcCostsAverage - marginalCosts) * dtau * powerMin) / mengeRegelleistungPpos, 0));
			float arbeitspreisPpos   =  marginalCosts; // FACTOR_BALANCING_CALL := Faktor Regelenergieabruf
			
			// 1.3 Angebotsabgabe der positiven Regelleistung
			PowerPositive pPosSupply= new PowerPositive(leistungspreisPpos, mengeRegelleistungPpos, this , BidType.POWER_POSITIVE_LEISTUNGSPREIS);
			balancingMarketOperator.addPositiveSupply(pPosSupply);
			
			PowerPositive pPosSupplyArbeitspreis= new PowerPositive(arbeitspreisPpos, mengeRegelleistungPpos, this, BidType.POWER_POSITIVE_ARBEITSPREIS);
			balancingMarketOperator.addPositiveSupplyArbeitspreis(pPosSupplyArbeitspreis);
		}
		
		
		
		// 2.1 Bestimmung Gebotsmenge am Regelenergiemarkt - negative Regelleistung
		float mengeRegelleistungPneg = Math.min(powerPreceding - powerMin, pRampDown);  //Checked 10.11.2016
		
		if (mengeRegelleistungPneg < 5) {
			mengeRegelleistungPneg = 0;
		}
		
		
		if (mengeRegelleistungPneg > 0) {
			// 2.2 Bestimmung Leistungspreis am Regelenergiemarkt - negative Regelleistung
			float leistungspreisPneg = Math.abs(Math.min(((pfcCostsAverage - marginalCosts) * dtau * (powerMin + mengeRegelleistungPneg)) / mengeRegelleistungPneg, 0));
			float arbeitspreisPneg   = -marginalCosts;
			
			// 2.3 Angebotsabgabe der negativen Regelleistung
			PowerNegative pNegSupply =new PowerNegative(leistungspreisPneg, mengeRegelleistungPneg, this,BidType.POWER_NEGATIVE_LEISTUNGSPREIS);
			balancingMarketOperator.addNegativeSupply(pNegSupply);
			
			PowerNegative pNegSupplyArbeitspreis =new PowerNegative(arbeitspreisPneg, mengeRegelleistungPneg, this,BidType.POWER_NEGATIVE_ARBEITSPREIS);
			balancingMarketOperator.addNegativeSupplyArbeitspreis(pNegSupplyArbeitspreis);
		}

	}
	
	/**
	 * Will be called by the EOM market-operator to invite the agents to  make bids .
	 * Calls makeBidEOM(long currentTick)
	 */ 
	@Override
	public void makeBidEOM() {
		  long currentTick = TimeUtil.getCurrentTick();
	      makeBidEOM(currentTick);
	}

	/**
	 * Makes bid in the Energy Only Market. 
	 * <p>
	 * This function is the result of the market operator calling the agents within in the market.
	 * 
	 * @param currentTick  tick in which the bid takes place
	 */ 
	@Override
	public void makeBidEOM(long currentTick) {
		
		Date currentDate   = TimeUtil.getDate(currentTick);
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        float pPositiveCommited = powerTradeRegistry.getPositiveQuantityUsed(currentDate); // 
        float pNegativeCommited = powerTradeRegistry.getNegativeQuantityUsed(currentDate); //
        
        float ePreceding        = energyTradeRegistry.getQuantityUsed(precedingDate); 
        
        float pPreceding = ePreceding * 4; // EOM Momentanleistung im tick t-1
        
		float pConstRampUp   = powerRampUp   / 2;
		float pConstRampDown = powerRampDown / 2;
		
		// 1.1 Ermittlung Gebotsmenge des Must-Run-Gebots [MWh]
        float pMustRun = Math.max(powerPreceding - pConstRampDown  + negativeQuantityAssignedBPM , (powerMin + pNegativeCommited));
        float eMustRun = 0;
        
		if (pMustRun * dt >= 1) {
			eMustRun = pMustRun * dt;

			// 1.2 Ermittlung Gebotspreis des Must-Run-Gebots [Euro/MWh]
			float priceMustRun = -1.0f * (((cost_shutDown - cost_startUp) * powerMax) / powerMin) * dt + marginalCosts;

			// 1.3 Abgabe Must-Run-Gebot
			EnergySupplyMustRun mustRunSupply = new EnergySupplyMustRun(priceMustRun, eMustRun, this);
			eomMarketOperator.addSupply(mustRunSupply);
		}
      
        
		// 2.1 Ermittlung Gebotsmenge des Flexibilitätsgebots [MW]
        float pFlex = Math.min((powerMax - positiveQuantityAssignedBPM - pMustRun),(pConstRampUp - positiveQuantityAssignedBPM)-pMustRun);
        float eFlex  = 0;
        
		if (pFlex * dt >= 1) {
			eMustRun = pMustRun * dt;
			// 2.2 Ermittlung Gebotspreis des Flexibilitaetsangebots [Euro/MWh]
			float priceFlex = marginalCosts;

			// 2.3 Abgabe Flexibilitätsgebot
			EnergySupply flexSupply = new EnergySupply(priceFlex, eFlex, this);
			eomMarketOperator.addSupply(flexSupply);
        	
        }
	}
	
	/**
	 * @param balancingMarketOperator  the operator to set for the balancing power market
	 */ 
	@Override
	public void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator) {
		this.balancingMarketOperator = balancingMarketOperator;
	}

	/**
	 * @param spotMarketOperator  the operator to set for the spotmarket / Energy only market
	 */ 
	@Override
	public void setSpotMarketOperator(SpotMarketOperator spotMarketOperator) {
		this.eomMarketOperator = spotMarketOperator;
	}

	/**
	 * @return      last cleared price
	 */ 
	@Override
	public float getLastClearedPrice() {
		return lastClearedPrice;
	}

	/**
	 *
	 * @return      current Power as MWh per 15min
	 */ 
	@Override
	public float getCurrentPower() {
		return energyTradeRegistry.getQuantityUsed(TimeUtil.getCurrentDate()) * dtau;
	}
	
	/**
	 * 
	 */
	private void calculateAndSetPowerPreceding(){
		
		long currentTick = TimeUtil.getCurrentTick();
		Date currentDate   = TimeUtil.getDate(currentTick);
        Date precedingDate = TimeUtil.precedingDate(currentDate);// precedingDate = currentDate plus 15min 

		float pPositiveCommited = powerTradeRegistry.getPositiveQuantityUsed(precedingDate); 	// 
        float pNegativeCommited = powerTradeRegistry.getNegativeQuantityUsed(precedingDate); 	//
        float powerEOMpreceding = energyTradeRegistry.getQuantityUsed(precedingDate) * 4;		// power = energy * 4 
        
        
		powerPreceding = powerEOMpreceding- pNegativeCommited +  pPositiveCommited; 
	}
	
	/**
	 * 
	 * @return
	 */
	private float getPowerPreceding(){
		return powerPreceding;
	}
}
