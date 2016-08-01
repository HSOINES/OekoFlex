package hsoines.oekoflex.energytrader.tools;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        eomTrader.makeBidEOM();
        return this;
    }

    public TestSpotMarketOperator checkQuantities(final float[] quantities) {
        for (int i = 0; i < quantities.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - quantities.length + i);
            assertEquals(quantities[i], energySupply.getQuantity(), 0.001f);
        }
        return this;
    }

    public TestSpotMarketOperator notifyRates(final float[] rates) {
        for (int i = 0; i < rates.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - rates.length + i);
            eomTrader.notifyClearingDone(TimeUtil.getCurrentDate(), Market.SPOT_MARKET, energySupply, 0, rates[i]);
        }
        return this;
    }

    public TestSpotMarketOperator checkPower(final float power) {
        assertEquals(power, eomTrader.getCurrentPower(), 0.001f);
        return this;
    }

    public TestSpotMarketOperator checkPrices(final float[] prices) {
        for (int i = 0; i < prices.length; i++) {
            final EnergySupply energySupply = energySupplies.get(energySupplies.size() - prices.length + i);
            assertEquals(prices[i], energySupply.getPrice(), 0.001f);
        }
        return this;
    }
}
