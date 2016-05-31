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
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 31/05/16
 * Time: 00:06
 */
public class SimpleStorage2Test {

    private SimpleStorage simpleStorage;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        final File priceForwardOutFile = new File("src-test/resources/price-forward-storage.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();

        simpleStorage = new SimpleStorage("test", "description", 0, 0, 200, 0.9f, .1f, 2, 2, priceForwardCurve);

    }

    @Test
    public void testBid() throws Exception {
        final SpotMarketOperator operator = new SpotMarketOperatorImpl("test", "src-test", false);

        simpleStorage.setSpotMarketOperator(operator);
        simpleStorage.makeBidEOM(0);
        operator.addDemand(new EnergyDemand(0, 1000, new MyMarketOperatorListener()));
        operator.addSupply(new EnergySupply(0, 1000, new MyMarketOperatorListener()));
        operator.clearMarket();
        assertEquals(.1 + .25f, simpleStorage.getSoc(), 0.0001f);
        simpleStorage.makeBidEOM(1);
        simpleStorage.makeBidEOM(2);
        simpleStorage.makeBidEOM(3);
        simpleStorage.makeBidEOM(4);
        simpleStorage.makeBidEOM(5);
        simpleStorage.makeBidEOM(6);
        simpleStorage.makeBidEOM(7);
        simpleStorage.makeBidEOM(8);
        simpleStorage.makeBidEOM(9);
        simpleStorage.makeBidEOM(10);
        simpleStorage.makeBidEOM(11);
        simpleStorage.makeBidEOM(12);
        simpleStorage.makeBidEOM(13);
        simpleStorage.makeBidEOM(14);
        simpleStorage.makeBidEOM(15);
        simpleStorage.makeBidEOM(16);
        simpleStorage.makeBidEOM(17);
        simpleStorage.makeBidEOM(18);
        simpleStorage.makeBidEOM(19);
        simpleStorage.makeBidEOM(20);
        simpleStorage.makeBidEOM(21);
        simpleStorage.makeBidEOM(22);
        simpleStorage.makeBidEOM(23);
        simpleStorage.makeBidEOM(24);
        simpleStorage.makeBidEOM(25);
        simpleStorage.makeBidEOM(26);
        simpleStorage.makeBidEOM(27);
        simpleStorage.makeBidEOM(28);
        simpleStorage.makeBidEOM(29);

    }

    @Test
    public void testOverload() throws Exception {


    }

    private static class MySpotMarketOperator implements SpotMarketOperator {
        private EnergyDemand energyDemand;
        private EnergySupply supply;

        @Override
        public void addDemand(final EnergyDemand energyDemand) {
            this.energyDemand = energyDemand;
            supply = null;
        }

        @Override
        public void addSupply(final EnergySupply supply) {
            this.supply = supply;
            energyDemand = null;
        }

        @Override
        public void clearMarket() {

        }

        @Override
        public float getTotalClearedQuantity() {
            return 0;
        }

        @Override
        public float getLastClearedPrice() {
            return 0;
        }

        @Override
        public float getLastAssignmentRate() {
            return 0;
        }

        @Override
        public void stop() {

        }

        @Override
        public List<EnergySupply> getLastSupplies() {
            return null;
        }

        @Override
        public List<EnergyDemand> getLastEnergyDemands() {
            return null;
        }

        @Override
        public AssignmentType getLastAssignmentType() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        public EnergyDemand getEnergyDemand() {
            return energyDemand;
        }

        public EnergySupply getEnergySupply() {
            return supply;
        }
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