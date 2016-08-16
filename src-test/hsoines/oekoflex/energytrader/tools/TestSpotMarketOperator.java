package hsoines.oekoflex.energytrader.tools;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.impl.LearningStorage;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: jh
 * Date: 15/06/16
 * Time: 21:26
 */
public class TestSpotMarketOperator implements SpotMarketOperator {
    private EnergyDemand lastEnergyDemand;
    private EnergySupply lastEnergySupply;
    private List<EnergyDemand> energyDemands;
    private List<EnergySupply> energySupplies;
    private EOMTrader eomTrader;

    public TestSpotMarketOperator() {
        energyDemands = new ArrayList<>();
        energySupplies = new ArrayList<>();
    }

    @Override
    public void addDemand(final EnergyDemand energyDemand) {
        this.lastEnergyDemand = energyDemand;
        energyDemands.add(energyDemand);
    }

    @Override
    public void addSupply(final EnergySupply energySupply) {
        this.lastEnergySupply = energySupply;
        energySupplies.add(energySupply);
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
    public List<EnergySupply> getLastEnergySupplies() {
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

    public EnergyDemand getLastEnergyDemand() {
        return lastEnergyDemand;
    }

    public EnergyDemand getEnergyDemands(int i) {
        return energyDemands.get(i);
    }

    public EnergySupply getEnergySupply() {
        return lastEnergySupply;
    }

    public EnergySupply getEnergySupply(int i) {
        return energySupplies.get(i);
    }

    public TestSpotMarketOperator makeBid(final EOMTrader eomTrader) {
        this.eomTrader = eomTrader;
        lastEnergyDemand = null;
        lastEnergySupply = null;
        energySupplies.clear();
        energyDemands.clear();

        eomTrader.makeBidEOM();
        return this;
    }

    public TestSpotMarketOperator checkSupplyQuantities(final float[] quantities) {
        assertTrue("Supply quantity not same: " + energySupplies.size(), quantities.length == energySupplies.size());
        for (int i = 0; i < quantities.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - quantities.length + i);
            assertEquals(quantities[i], energySupply.getQuantity(), 0.001f);
        }
        return this;
    }

    public TestSpotMarketOperator checkDemandQuantities(final float[] quantities) {
        assertTrue("Demand quantity not same: " + energyDemands.size(), quantities.length == energyDemands.size());
        for (int i = 0; i < quantities.length; i++) {
            final EnergyDemand energyDemand = energyDemands.get(energyDemands.size() - quantities.length + i);
            assertEquals(quantities[i], energyDemand.getQuantity(), 0.001f);
        }
        return this;
    }

    public TestSpotMarketOperator notifySupplyRates(final float[] rates) {
        for (int i = 0; i < rates.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - rates.length + i);
            eomTrader.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, energySupply, 0, rates[i]);
        }
        return this;
    }

    public TestSpotMarketOperator notifyDemandRates(final float[] rates) {
        for (int i = 0; i < rates.length; i++) {
            final EnergyDemand energyDemand = energyDemands.get(energyDemands.size() - rates.length + i);
            eomTrader.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, energyDemand, 0, rates[i]);
        }
        return this;
    }

    public TestSpotMarketOperator checkPower(final float power) {
        assertEquals(power, eomTrader.getCurrentPower(), 0.001f);
        return this;
    }

    public TestSpotMarketOperator checkSupplyPrices(final float[] prices) {
        for (int i = 0; i < prices.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - prices.length + i);
            assertEquals(prices[i], energySupply.getPrice(), 0.001f);
        }
        return this;
    }

   public TestSpotMarketOperator checkDemandPrices(final float[] prices) {
        for (int i = 0; i < prices.length; i++) {
            final EnergyDemand energyDemand = energyDemands.get(energyDemands.size() - prices.length + i);
            assertEquals(prices[i], energyDemand.getPrice(), 0.001f);
        }
        return this;
    }
   
  // Checks if eomTrader is a Storage and then
  // Compares expected StateofCharge with StateofCharge for current tick
  public TestSpotMarketOperator checkStateOfCharge(final float socExcpected){
	  if(eomTrader instanceof LearningStorage){
		  LearningStorage ls = (LearningStorage)eomTrader;
		  assertEquals(socExcpected , ls.getStateOfCharge(), 0.001f);
	  }
	  return this;
  }
}
