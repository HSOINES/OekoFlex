package hsoines.oekoflex.energytrader.impl;

import static org.junit.Assert.assertEquals;
import hsoines.oekoflex.energytrader.tools.TestBalancingMarketOperator;
import hsoines.oekoflex.energytrader.tools.TestSpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.tools.RepastTestInitializer;
import hsoines.oekoflex.util.TimeUtil;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

// Der Test ist angelegt am realen Szenario mit dem nachfolgenden Kraftwerk:
public class FlexPowerplant3Test {
	
	public static final int 	POWER_MAX 		 = 2400;		// [MW] 
	public static final int 	POWER_MIN 		 = 2000;		// [MW] 
	public static final float 	EFFICIENCY 		 = 0.434F ;		// [percentage]
	public static final int 	POWER_RAMP_DOWN  = 336;			// [MW] per 15min
    public static final int 	POWER_RAMP_UP 	 = 336;			// [MW] per 15min	
    public static final float 	MARGINAL_COSTS 	 = 20.75f;		// [Euro/MWh]
    public static final float	COSTS_START_UP	 = 3.0f;		// [Euro/MW]
    public static final float	COSTS_SHUT_DOWN	 = 3.0f;		// [Euro/MW]

    private TestBalancingMarketOperator balancingMarketOperator;
    private FlexPowerplant3 flexpowerplant;
    private TestSpotMarketOperator eomOperator;
    private PriceForwardCurve priceForwardCurve;
    
    private float emissionRate;
    private float efficiency;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        RepastTestInitializer.init();
        eomOperator = new TestSpotMarketOperator();
        balancingMarketOperator = new TestBalancingMarketOperator();

        final File priceForwardOutFile = new File("src-test/resources/price-forward-flex.csv");
        priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();
        flexpowerplant = new FlexPowerplant3("flexpowerplant", "description",POWER_MAX, POWER_MIN, POWER_RAMP_UP, POWER_RAMP_DOWN ,priceForwardCurve, MARGINAL_COSTS,COSTS_START_UP, COSTS_SHUT_DOWN );
        flexpowerplant.setBalancingMarketOperator(balancingMarketOperator);
        flexpowerplant.setSpotMarketOperator(eomOperator);
    }

    @Test
    public void testSpotMarketBid() throws Exception {
    	// Irgendwas stimmt hier doch nicht!!! -> Die Werte für das Kraftwerk können doch nicht immer gleich bleiben?!
    	
    	
        TimeUtil.startAt(0);
        assertEquals(256, priceForwardCurve.getPriceSummation(TimeUtil.getCurrentTick(), 16), 0.00001f);
        
        balancingMarketOperator.makeBid(flexpowerplant).checkPowerPos(112f, 339.2857f).checkPowerNeg(0, 0).notifyRatePos(1).notifyRateNeg(0);
        
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);

        TimeUtil.startAt(1);
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);
        
        TimeUtil.startAt(2);
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);
       
        TimeUtil.startAt(3);
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);
        
        TimeUtil.startAt(4);
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);
       
        TimeUtil.startAt(5);
        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500f}).checkSupplyPrices(new float[]{20.75f}).notifySupplyRates(new float[]{1f}).checkPower(2000f);
        
        TimeUtil.startAt(6);
        
        
        TimeUtil.startAt(7);
        
        
        TimeUtil.startAt(8);
        
        
        TimeUtil.startAt(9);
        
        
        TimeUtil.startAt(10);
        
        
        TimeUtil.startAt(11);
        
        
        TimeUtil.startAt(12);
        
        
        TimeUtil.startAt(13);
        
        
        TimeUtil.startAt(14);
        
        
        TimeUtil.startAt(15);
        
        
        TimeUtil.startAt(16);
        
        
        TimeUtil.startAt(17);


    }

    @Test
    public void testMarginalCosts() throws Exception {
        final int variableCosts = 3;
        final int fuelCosts = 6;
        final int co2CertificateCosts = 7;
        emissionRate = .6f;
        efficiency = .9f;
        assertEquals(14.333333f, FlexPowerplant3.calculateMarginalCosts(variableCosts, fuelCosts, co2CertificateCosts, emissionRate, efficiency), 0.00001f);
    }
}