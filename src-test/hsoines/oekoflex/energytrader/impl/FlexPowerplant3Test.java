//package hsoines.oekoflex.energytrader.impl;
//
//import static org.junit.Assert.assertEquals;
//import hsoines.oekoflex.energytrader.tools.TestBalancingMarketOperator;
//import hsoines.oekoflex.energytrader.tools.TestSpotMarketOperator;
//import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
//import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
//import hsoines.oekoflex.tools.RepastTestInitializer;
//import hsoines.oekoflex.util.TimeUtil;
//
//import java.io.File;
//
//import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
//
//// Der Test ist angelegt am realen Szenario mit dem nachfolgenden Kraftwerk:
//public class FlexPowerplant3Test {
//	public static final int 	POWER_MAX 		 = 2400;		// [MW] 
//	public static final int 	POWER_MIN 		 = 2000;		// [MW] 
//	public static final float 	EFFICIENCY 		 = 0.434F ;		// [percentage]
//	public static final int 	POWER_RAMP_DOWN  = 336;			// [MW] per 15min
//    public static final int 	POWER_RAMP_UP 	 = 336;			// [MW] per 15min	
//    public static final float 	MARGINAL_COSTS 	 = 20.75f;		// Euro/MWh
//    public static final float	COSTS_START_UP	 = 3.0f;			// Euro/MW
//    public static final float	COSTS_SHUT_DOWN	 = 3.0f;			// Euro/MW
//
//    private TestBalancingMarketOperator balancingMarketOperator;
//    private FlexPowerplant3 flexpowerplant;
//    private TestSpotMarketOperator eomOperator;
//    private PriceForwardCurve priceForwardCurve;
//    
//    private float emissionRate;
//    private float efficiency;
//
//    @Before
//    public void setUp() throws Exception {
//        BasicConfigurator.configure();
//
//        RepastTestInitializer.init();
//        eomOperator = new TestSpotMarketOperator();
//        balancingMarketOperator = new TestBalancingMarketOperator();
//
//        final File priceForwardOutFile = new File("src-test/resources/price-forward-flex.csv");
//        priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
//        priceForwardCurve.readData();
//        flexpowerplant = new FlexPowerplant3("flexpowerplant", "description",POWER_MAX, POWER_MIN, POWER_RAMP_UP, POWER_RAMP_DOWN ,priceForwardCurve, MARGINAL_COSTS,COSTS_START_UP, COSTS_SHUT_DOWN );
//        flexpowerplant.setBalancingMarketOperator(balancingMarketOperator);
//        flexpowerplant.setSpotMarketOperator(eomOperator);
//    }
//
//    @Test
//    public void testSpotMarketBid() throws Exception {
//    	
//        TimeUtil.startAt(0);
//        assertEquals(256, priceForwardCurve.getPriceSummation(TimeUtil.getCurrentTick(), 16), 0.00001f);
//        
//        balancingMarketOperator.makeBid(flexpowerplant).checkPowerPos(33.3333f, 57.6f).checkPowerNeg(0, 0).notifyRatePos(1).notifyRateNeg(0);
//        
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{8000.0f, -7100.0f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2016.6666f);
//
//        TimeUtil.startAt(1);
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 8.333313f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2033.3333f);
//        TimeUtil.startAt(2);
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 12.5f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2050);
//        TimeUtil.startAt(3);
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 16.666687f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2066.6666f);
//        TimeUtil.startAt(4);
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 20.833374f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2083.3335f);
//        TimeUtil.startAt(5);
//        eomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{500, 25.000061f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2100.0002f);
////        for (int i = 6; i < 16; i++) {
////            TimeUtil.startAt(i);
////            testEomOperator.makeBid(flexpowerplant).checkSupplyQuantities(new float[]{504.00006f, 29.166687f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2116.667f);
////        }
//        TimeUtil.startAt(16);
//        //PFC = -9, MC=2, Pmin=2000, Pangebot=66.66666, => 17.54
////        testBalancingMarketOperator.makeBid(flexpowerplant).checkPowerPos(33.3333f, 2641.6064f).checkPowerNeg(66.6666f, 1362.4f).notifyRatePos(1).notifyRateNeg(0);//todo: preis prüfen
//
//    }
//
//    @Test
//    public void testMarginalCosts() throws Exception {
//        final int variableCosts = 3;
//        final int fuelCosts = 6;
//        final int co2CertificateCosts = 7;
//        emissionRate = .6f;
//        efficiency = .9f;
//        assertEquals(14.333333f, FlexPowerplant2.calculateMarginalCosts(variableCosts, fuelCosts, co2CertificateCosts, emissionRate, efficiency), 0.00001f);
//    }
//
//    @Test
//    public void testName() throws Exception {
//    }
//
//}