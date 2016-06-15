package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 15/06/16
 * Time: 21:26
 */
class TestSpotMarketOperator implements SpotMarketOperator {
    private EnergyDemand energyDemand;
    private EnergySupply supply;
    private List<EnergyDemand> energyDemands;
    private List<EnergySupply> energySupplies;

    public TestSpotMarketOperator() {
        energyDemands = new ArrayList<>();
        energySupplies = new ArrayList<>();
    }

    @Override
    public void addDemand(final EnergyDemand energyDemand) {
        this.energyDemand = energyDemand;
        energyDemands.add(energyDemand);
        supply = null;
    }

    @Override
    public void addSupply(final EnergySupply energySupply) {
        this.supply = energySupply;
        energySupplies.add(energySupply);
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

    public EnergyDemand getEnergyDemands(int i) {
        return energyDemands.get(i);
    }

    public EnergySupply getEnergySupply() {
        return supply;
    }

    public EnergySupply getEnergySupply(int i) {
        return energySupplies.get(i);
    }

}
