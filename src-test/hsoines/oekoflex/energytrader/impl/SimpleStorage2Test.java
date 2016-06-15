package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.util.Market;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 31/05/16
 * Time: 00:06
 */
public class SimpleStorage2Test {
    private static final Log log = LogFactory.getLog(SimpleStorage2Test.class);

    private SimpleStorage simpleStorage;
    private SpotMarketOperator operator;
    private long step = 0;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        final File priceForwardOutFile = new File("src-test/resources/price-forward-storage.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();

        simpleStorage = new SimpleStorage("test", "description", 0, 0, 5, 0.9f, .1f, 2, 2, priceForwardCurve, false);
        operator = new SpotMarketOperatorImpl("test", "src-test", false);

    }

    @Test
    public void testBid() throws Exception {
        simpleStorage.setSpotMarketOperator(operator);
        makeNextBid();
        assertEquals(.20f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.30f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.40f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.50f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.60f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.70f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.70f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.80f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.70f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.60f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.50f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.40f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.30f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.20f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.10f, simpleStorage.getSoc(), 0.0001f);
        makeNextBid();
        assertEquals(.10f, simpleStorage.getSoc(), 0.0001f);
    }

    void makeNextBid() {
        log.info("Tick: " + step);
        simpleStorage.makeBidEOM(step++);
        operator.addDemand(new EnergyDemand(0, 1000, new MyMarketOperatorListener()));
        operator.addSupply(new EnergySupply(0, 1000, new MyMarketOperatorListener()));
        operator.clearMarket();
    }

    @Test
    public void testOverload() throws Exception {


    }

    private static class MyMarketOperatorListener implements MarketOperatorListener {
        @Override
        public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

        }

        @Override
        public String getName() {
            return null;
        }
    }
}