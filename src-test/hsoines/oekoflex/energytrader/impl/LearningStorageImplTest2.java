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
	        lst.setBalancingMarketOperator(testBalancingMarketOperator);
	        lst.setSpotMarketOperator(testEomOperator);

	    }
	    
	    @Test
	    public void testSpotMarketBid() throws Exception {
	        TimeUtil.startAt(0);
//	        testEomOperator.makeBid(lst).checkQuantities(new float[]{500, 16.666667f}).checkSupplyPrices(new float[]{-120 + 2, 2}).notifySupplyRates(new float[]{1f, 1f}).checkPower(2066.66667f);
	        testEomOperator.makeBid(lst).checkDemandQuantities(new float[]{2.5f}).checkDemandPrices(new float[]{3000}).notifyDemandRates(new float[]{1f}).checkPower(10);
	        TimeUtil.startAt(1);
	        testEomOperator.makeBid(lst).checkDemandQuantities(new float[]{2.5f}).checkDemandPrices(new float[]{3000}).notifyDemandRates(new float[]{.5f}).checkPower(5);

	}

}
