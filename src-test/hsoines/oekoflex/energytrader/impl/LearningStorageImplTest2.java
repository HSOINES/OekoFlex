package hsoines.oekoflex.energytrader.impl;

import static org.junit.Assert.*;
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

public class LearningStorageImplTest2 {
	 private TestBalancingMarketOperator testBalancingMarketOperator;
	    private TestSpotMarketOperator testEomOperator;
	    private PriceForwardCurve priceForwardCurve;
	    LearningStorage lst = null;

	    @Before
	    public void setUp() throws Exception {
	        BasicConfigurator.configure();

	        RepastTestInitializer.init();
	        testEomOperator = new TestSpotMarketOperator();
	        testBalancingMarketOperator = new TestBalancingMarketOperator();

	        final File priceForwardOutFile = new File("src-test/resources/pfc-learning-storage2.csv");
	        priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
	        priceForwardCurve.readData();
	        lst = new LearningStorage("l1", "skf", 120, 120, 10, 10, 0, priceForwardCurve, 0);
	        lst.setStateOfCharge(0.25f);
	        lst.setBalancingMarketOperator(testBalancingMarketOperator);
	        lst.setSpotMarketOperator(testEomOperator);

	    }
	    
	    @Test
	    public void testSpotMarketBid() throws Exception {
	        TimeUtil.startAt(0);
	        testEomOperator.makeBid(lst).checkDemandQuantities(new float[]{}).checkSupplyPrices(new float[]{}).checkSupplyQuantities(new float[]{}).checkDemandPrices(new float[]{}).notifyDemandRates(new float[]{});
	        TimeUtil.startAt(1);
	        testEomOperator.makeBid(lst).checkDemandQuantities(new float[]{}).checkSupplyPrices(new float[]{}).checkSupplyQuantities(new float[]{}).checkDemandPrices(new float[]{}).notifyDemandRates(new float[]{});
	        TimeUtil.startAt(2);
	        testEomOperator.makeBid(lst).checkDemandQuantities(new float[]{2.5f}).checkDemandPrices(new float[]{3000f}).notifyDemandRates(new float[]{1.0f}).checkPower(10);
//	        TimeUtil.startAt(3);
//	        TimeUtil.startAt(4);
//	        TimeUtil.startAt(5);
//	        TimeUtil.startAt(6);
//	        TimeUtil.startAt(7);
//	        TimeUtil.startAt(8);
//	        TimeUtil.startAt(9);
//	        
//	        
//	        TimeUtil.startAt(10);
//	        TimeUtil.startAt(11);
//	        TimeUtil.startAt(12);
//	        TimeUtil.startAt(13);
//	        TimeUtil.startAt(14);
//	        TimeUtil.startAt(15);
//	        TimeUtil.startAt(16);
//	        TimeUtil.startAt(17);
//	        TimeUtil.startAt(18);
//	        TimeUtil.startAt(19);
//	        
//	        
//	        TimeUtil.startAt(20);
//	        TimeUtil.startAt(21);
//	        TimeUtil.startAt(22);
//	        TimeUtil.startAt(23);
//	        TimeUtil.startAt(24);
//	        TimeUtil.startAt(25);
//	        TimeUtil.startAt(26);
//	        TimeUtil.startAt(27);
//	        TimeUtil.startAt(28);
//	        TimeUtil.startAt(29);
//	        

	}

}
