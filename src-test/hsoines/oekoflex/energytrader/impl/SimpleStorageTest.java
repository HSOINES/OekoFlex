package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 31/05/16
 * Time: 00:06
 */
public class SimpleStorageTest {

    private SimpleStorage simpleStorage;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
        final File priceForwardOutFile = new File("src-test/resources/price-forward-storage.csv");
        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
        priceForwardCurve.readData();

        simpleStorage = new SimpleStorage("test", "description", 0, 0, 200, 0.9f, .1f, 2, 2, priceForwardCurve, false);
    }

    @Test
    public void testBid() throws Exception {
        final MySpotMarketOperator operator = new MySpotMarketOperator();

        simpleStorage.setSpotMarketOperator(operator);
        simpleStorage.makeBidEOM(0);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(1);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(2);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(3);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(4);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(5);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(6);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(7);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(8);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(9);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(10);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(11);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(12);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(13);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(14);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(15);
        assertEquals(2 * .25f, operator.getEnergyDemand().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(16);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(17);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(18);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(19);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(20);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(21);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(22);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(23);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(24);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(25);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(26);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(27);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(28);
        assertEquals(2 * .25f, operator.getEnergySupply().getQuantity(), 0.00001f);
        simpleStorage.makeBidEOM(29);

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
            EnergyDemand tmp = energyDemand;
            energyDemand = null;
            return tmp;
        }

        public EnergySupply getEnergySupply() {
            EnergySupply tmp = supply;
            supply = null;
            return tmp;
        }

        public void reset() {
            energyDemand = null;
            supply = null;
        }
    }
}