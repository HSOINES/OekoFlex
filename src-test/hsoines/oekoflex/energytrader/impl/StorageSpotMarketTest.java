//package hsoines.oekoflex.energytrader.impl;
//
//import hsoines.oekoflex.bid.EnergyDemand;
//import hsoines.oekoflex.bid.EnergySupply;
//import hsoines.oekoflex.marketoperator.SpotMarketOperator;
//import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
//import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
//import hsoines.oekoflex.tools.RepastTestInitializer;
//import hsoines.oekoflex.util.Market;
//import hsoines.oekoflex.util.TimeUtil;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
///**
// * User: jh
// * Date: 18/01/16
// * Time: 18:23
// */
//public class StorageSpotMarketTest {
//
//    public static final float SOC_MIN = 0.2f;
//    public static final float SOC_MAX = .9f;
//    public static final int CHARGE_POWER = 90;
//    public static final int DISCHARGE_POWER = 110;
//    public static final int ENERGY_CAPACITY = 1000;
//    private Storage storage;
//    private TestSpotMarketOperator operator;
//
//    @Before
//    public void setUp() throws Exception {
//        RepastTestInitializer.init();
//        final File priceForwardOutFile = new File("src-test/resources/price-forward.csv");
//        final PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardOutFile);
//        priceForwardCurve.readData();
//        storage = new Storage("test", "description", 1f, 0.1f, ENERGY_CAPACITY, SOC_MAX, SOC_MIN, CHARGE_POWER, DISCHARGE_POWER, priceForwardCurve);
//        operator = new TestSpotMarketOperator();
//        storage.setSpotMarketOperator(operator);
//
//        TimeUtil.nextTick();
//    }
//
//    @Test
//    public void testSOCMiddle() throws Exception {
//        storage.setSOC(0.5f);
//        storage.calculateEOMActionsForDay();
//        storage.makeBidEOM();
//
//        //not cheapest, not most expensive!
//        assertNull(operator.getLastDemand());
//        assertNull(operator.getLastDemand());
//
//        float load = storage.getSoc() * ENERGY_CAPACITY;
//        assertEquals(0.5f * ENERGY_CAPACITY, load, 0.0001f);
//
//        operator.clear();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick();
//        TimeUtil.nextTick(); //tick 11
//
//        storage.makeBidEOM();
//
//        EnergySupply lastSupply = operator.getLastSupply();
//        assertEquals(DISCHARGE_POWER * .25, lastSupply.getQuantity(), 0.0001f);
//        assertEquals(-3000, lastSupply.getPrice(), 0.0001f);
//        storage.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, lastSupply, 10, 1f);
//        load = storage.getSoc() * ENERGY_CAPACITY;
//        assertEquals(0.5f * ENERGY_CAPACITY - DISCHARGE_POWER * .25f, load, 0.0001f);
//
//        operator.clear();
//        TimeUtil.nextTick();
//        storage.makeBidEOM();
//
//        EnergyDemand lastDemand = operator.getLastDemand();
//        assertEquals(CHARGE_POWER * .25f, lastDemand.getQuantity(), 0.0001f);
//        assertEquals(3000, lastDemand.getPrice(), 0.0001f);
//
//        storage.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, lastDemand, 10, 1f);
//
//        load = storage.getSoc() * ENERGY_CAPACITY;
//        assertEquals(0.5f * ENERGY_CAPACITY + CHARGE_POWER * .25 - DISCHARGE_POWER * .25, load, 0.0001f);
//    }
//
//    @Test
//    public void testBatteryEmpty2() throws Exception {
//        storage.makeBidEOM();
//        operator.addSupply(new EnergySupply(0.8f, 500, null));
//        operator.addSupply(new EnergySupply(1f, 300, null));
//
//        operator.clearMarket();
//        float load = storage.getSoc();
//        assertEquals(500, load, 0.0001f);
//    }
//
//    @Test
//    public void test2Cycles() throws Exception {
//        storage.makeBidEOM();
//        operator.addSupply(new EnergySupply(0.8f, 400, null));
//        operator.clearMarket();
//        assertEquals(400, storage.getSoc(), .0001f);
//        TimeUtil.nextTick();
//        storage.makeBidEOM();
//        operator.addDemand(new EnergyDemand(1.2f, 150, null));
//        operator.clearMarket();
//
//        assertEquals(250, storage.getSoc(), 0.0001f);
//    }
//
//    private static class TestSpotMarketOperator implements SpotMarketOperator {
//        private EnergySupply lastSupply;
//        private EnergyDemand lastDemand;
//
//        public void clear(){
//            lastSupply = null;
//            lastDemand = null;
//        }
//
//        public EnergySupply getLastSupply() {
//            return lastSupply;
//        }
//
//        public EnergyDemand getLastDemand() {
//            return lastDemand;
//        }
//
//        @Override
//        public void addDemand(EnergyDemand energyDemand) {
//           this.lastDemand = energyDemand;
//        }
//
//        @Override
//        public void addSupply(EnergySupply supply) {
//            this.lastSupply = supply;
//        }
//
//        @Override
//        public void clearMarket() {
//
//        }
//
//        @Override
//        public float getTotalClearedQuantity() {
//            return 0;
//        }
//
//        @Override
//        public float getLastClearedPrice() {
//            return 0;
//        }
//
//        @Override
//        public float getLastAssignmentRate() {
//            return 0;
//        }
//
//        @Override
//        public void stop() {
//
//        }
//
//        @Override
//        public List<EnergySupply> getLastEnergySupplies() {
//            return null;
//        }
//
//        @Override
//        public List<EnergyDemand> getLastEnergyDemands() {
//            return null;
//        }
//
//        @Override
//        public AssignmentType getLastAssignmentType() {
//            return null;
//        }
//
//        @Override
//        public String getName() {
//            return null;
//        }
//    }
//}