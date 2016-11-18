package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.BidSupport;
import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.summary.LoggerFile;
import hsoines.oekoflex.summary.impl.LoggerFileImpl;
import hsoines.oekoflex.summary.impl.NullLoggerFile;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.NumberFormatUtil;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clears the balancing power market
 * <ul>
 * 	<li> gets bids as supplies or demands from the market traders
 * 	<li> determines the bids that are accepted 
 * 	<li> notifies the market traders that their bids are accepted or denied
 * </ul>
 * <p>
 * <p>
 * Furthermore has getter functions for:
 * <ul>
 * 	<li> JUnit tests, and
 * 	<li> the diagram
 * </ul>
 */
public final class BalancingMarketOperatorImpl implements BalancingMarketOperator {
    private static final Log log = LogFactory.getLog(BalancingMarketOperatorImpl.class);

    private final String name;
    private final int positiveQuantity;
    private final int negativeQuantity;
    private final List<BidSupport> positiveSupplies = new ArrayList<>(); // wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
    private final List<BidSupport> negativeSupplies = new ArrayList<>(); // wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
    
    // Liste der ArbeitspreisGebote
    private  List<BidSupport> positiveSuppliesArbeitsPreis = new ArrayList<>(); 
    private  List<BidSupport> negativeSuppliesArbeitsPreis = new ArrayList<>(); 

    private float totalClearedPositiveQuantity;
    private float totalClearedNegativeQuantity;

    private float lastClearedPositiveMaxPrice;
    private float lastClearedNegativeMaxPrice;

    private float lastPositiveAssignmentRate;
    private float lastNegativeAssignmentRate;

    private LoggerFile logger;
    
    /**
     * 
     * @param name				name of this balancing market operator
     * @param loggingActivated	is the logging activated true = yes , otherwise false
     * @param logDirName		name of directory for the logging
     * @param positiveDemandBPM	amount of positive demand of this balancing market
     * @param negativeDemandBPM amount of negative demand of this balancing market
     * @throws IOException
     */
    public BalancingMarketOperatorImpl(String name, final boolean loggingActivated, String logDirName, final int positiveDemandBPM, final int negativeDemandBPM) throws IOException {
        this.name = name;
        this.positiveQuantity = positiveDemandBPM; 
        this.negativeQuantity = negativeDemandBPM;
        if (loggingActivated) {
            initLogging(logDirName);
        } else {
            logger = new NullLoggerFile();
        }
    }
    /**
     * 
     * @param logDirName 	name of directory for the logging
     * @throws IOException
     */
    private void initLogging(final String logDirName) throws IOException {
        logger = new LoggerFileImpl(this.getClass().getSimpleName(), logDirName);
        logger.log("tick;traderType;traderName;bidType;offeredPrice;offeredQuantity;assignedQuantity");
    }
    
    /**
     * @param supply the positive power to add
     */
    @Override
    public void addPositiveSupply(final PowerPositive supply) {
        if (supply.getQuantity() < 0.00001) {
            return;
        }
        positiveSupplies.add(supply);
    }
    
    /**
     * @param supply the negative power to add
     */
    @Override
    public void addNegativeSupply(final PowerNegative supply) {
        if (supply.getQuantity() < 0.00001) {
            return;
        }
        negativeSupplies.add(supply);
    }
    
    /**
     * 
     * @param supplies
     * @param quantity
     * @return
     */
    ClearingData doClearMarketFor(final List<BidSupport> supplies, float quantity) {
        supplies.sort(new BidSupport.SupplyComparator()); // smaller prices before bigger ones, in case they are the same bigger quantities first 
        
        float totalClearedQuantity = 0;
        float lastAssignmentRate = 0;
        float lastClearedPrice = 0;
        
        for (BidSupport bidSupport : supplies) {
            MarketOperatorListener marketOperatorListener = bidSupport.getMarketOperatorListener(); // Wiso nicht ausserhalb der Schleife???!!!!
            
            if (totalClearedQuantity + bidSupport.getQuantity() < quantity) {	// Completely fulfilled bids
                totalClearedQuantity += bidSupport.getQuantity();
                lastAssignmentRate = 1;							
                doNotify(bidSupport, marketOperatorListener, 1);
                lastClearedPrice = bidSupport.getPrice();
            } else if (totalClearedQuantity >= quantity) {						// none fulfilled bids
                doNotify(bidSupport, marketOperatorListener, 0);
            } else {															// partially fulfilled bids
                lastAssignmentRate = (quantity - totalClearedQuantity) / bidSupport.getQuantity();
                doNotify(bidSupport, marketOperatorListener, lastAssignmentRate);
                totalClearedQuantity += bidSupport.getQuantity() * lastAssignmentRate;
                lastClearedPrice = bidSupport.getPrice();
                
            }
        }
        
        log.trace("Clearing done.");
        
        
        
        final float finalTotalClearedQuantity = totalClearedQuantity;
        final float finalLastAssignmentRate = lastAssignmentRate;
        final float finalLastClearedPrice = lastClearedPrice;
        
        log.trace("total cleared quantity: " + finalTotalClearedQuantity + ", lasst assignment rate: " + lastAssignmentRate + ", last cleared price: " + lastClearedPrice);
        
        return new ClearingData() {
            @Override
            public float getClearedQuantity() {
                return finalTotalClearedQuantity;
            }

            @Override
            public float getLastClearedMaxPrice() {
                return finalLastClearedPrice;
            }

            @Override
            public float getAssignmentRate() {
                return finalLastAssignmentRate;
            }
        };
    }

    /** 
	 * Getter for Tests
	 * @return amount of positive power cleared
	 */
    @Override
    public float getTotalClearedPositiveQuantity() {
        return totalClearedPositiveQuantity;
    }

    /** 
	 * Getter for Tests
	 * @return amount of negative power cleared
	 */
    @Override
    public float getTotalClearedNegativeQuantity() {
        return totalClearedNegativeQuantity;
    }

    /** 
	 * Getter for diagram
	 * @return last positive assignment rate
	 */
    @Override
    public float getLastPositiveAssignmentRate() {
        return lastPositiveAssignmentRate;
    }

    /** Getter for diagram  
	 * @return last cleared negative max price
	 */
    @Override
    public float getLastClearedNegativeMaxPrice() {
        return lastClearedNegativeMaxPrice;
    }

    /** 
	 * Getter for diagram 
	 * @return last negative assignment rate
	 */
    @Override
    public float getLastNegativeAssignmentRate() {
        return lastNegativeAssignmentRate;
    }

    /**
     * This function notifies every agent, that has a bid for the current tick how their bid went
     * @param bidSupport
     * @param marketOperatorListener
     * @param assignRate
     */
    void doNotify(final BidSupport bidSupport, final MarketOperatorListener marketOperatorListener, float assignRate) { // STRING type kann gelöscht werden sobald bidtype korrekt implementiert ist
        long tick = TimeUtil.getCurrentTick();
        marketOperatorListener.notifyClearingDone(TimeUtil.getDate(tick), Market.BALANCING_MARKET, bidSupport, bidSupport.getPrice(), assignRate);

        logger.log(String.valueOf(tick) + ";"
                + bidSupport.getMarketOperatorListener().getClass().getSimpleName() + ";"
                + bidSupport.getMarketOperatorListener().getName() + ";"
                + bidSupport.getBidType() + ";"
                + NumberFormatUtil.format(bidSupport.getPrice()) + ";"
                + NumberFormatUtil.format(bidSupport.getQuantity()) + ";"
                + NumberFormatUtil.format(bidSupport.getQuantity() * assignRate) + ";" );
    }
    
    /**
     * @return name of the balancing power market operator
     */
    @Override
    public String getName() {
        return name;
    }
    
    /** 
	 * Getter for diagram 
	 * @return last cleared positive max price
	 */
    @Override
    public float getLastClearedPositiveMaxPrice() {
        return lastClearedPositiveMaxPrice;
    }

    /**
     * Stop of the market operator, is called by the Repast scheduler
     */
    @ScheduledMethod(start = ScheduledMethod.END)
    public void stop() {
        logger.close();
    }

    /**
     * 
     */
    private interface ClearingData {
        float getClearedQuantity();

        float getLastClearedMaxPrice();

        float getAssignmentRate();
    }
    
    // Leistungspreis
	@Override
	public void clearMarketCapacityPrice() {

		
		
        log.trace("positive clearing Capacity Price.");
        ClearingData positiveClearingData = doClearMarketFor(positiveSupplies, positiveQuantity); // positiveSupplies-> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedPositiveQuantity = positiveClearingData.getClearedQuantity();
        lastPositiveAssignmentRate = positiveClearingData.getAssignmentRate();
        lastClearedPositiveMaxPrice = positiveClearingData.getLastClearedMaxPrice();
        
        log.trace("negative clearing Capacity Price.");
        ClearingData negativeClearingData = doClearMarketFor(negativeSupplies, negativeQuantity); // negativeSupplies-> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedNegativeQuantity = negativeClearingData.getClearedQuantity();
        lastNegativeAssignmentRate = negativeClearingData.getAssignmentRate();
        lastClearedNegativeMaxPrice = negativeClearingData.getLastClearedMaxPrice();
        
		//Clear all old supplies: 
        // Has to be at the end otherwise, all added supplies would be deleted before the clearing
	    positiveSupplies.clear();
	    negativeSupplies.clear();
	    positiveSuppliesArbeitsPreis.clear();
	    negativeSuppliesArbeitsPreis.clear();
		
	}
	
	//Arbeitspreis
	public void clearMarketEnergyPrice() {
		log.trace("positive clearing Energy Price.");
        ClearingData positiveClearingData = doClearMarketFor(positiveSupplies, positiveQuantity); // positiveSupplies-> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedPositiveQuantity = positiveClearingData.getClearedQuantity();
        lastPositiveAssignmentRate = positiveClearingData.getAssignmentRate();
        lastClearedPositiveMaxPrice = positiveClearingData.getLastClearedMaxPrice();
        
        log.trace("negative clearing Energy Price.");
        ClearingData negativeClearingData = doClearMarketFor(negativeSupplies, negativeQuantity); // negativeSupplies-> wiso final, sollte die Liste nicht mit jedem Tick neu generiert werden???
        totalClearedNegativeQuantity = negativeClearingData.getClearedQuantity();
        lastNegativeAssignmentRate = negativeClearingData.getAssignmentRate();
        lastClearedNegativeMaxPrice = negativeClearingData.getLastClearedMaxPrice();
	}
	

	
	@Override
	public void addNegativeSupplyArbeitspreis(PowerNegative pNegSupplyArbeitspreis) {
		   if (pNegSupplyArbeitspreis.getQuantity() < 0.00001) {
	            return;
	        }
	        negativeSupplies.add(pNegSupplyArbeitspreis);
		
	}
	@Override
	public void addPositiveSupplyArbeitspreis(PowerPositive pPosSupplyArbeitspreis) {
		  if (pPosSupplyArbeitspreis.getQuantity() < 0.00001) {
	            return;
	        }
	        positiveSuppliesArbeitsPreis.add(pPosSupplyArbeitspreis);
		
	}

}
