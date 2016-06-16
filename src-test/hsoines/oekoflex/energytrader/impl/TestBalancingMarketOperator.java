package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.PowerNegative;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 16/06/16
 * Time: 12:31
 */
class TestBalancingMarketOperator implements BalancingMarketOperator {

    private PowerNegative lastPowerNegative;
    private PowerPositive lastPowerPositive;
    private final List<PowerNegative> powerNegatives;
    private final List<PowerPositive> powerPositives;

    public TestBalancingMarketOperator() {
        powerNegatives = new ArrayList<>();
        powerPositives = new ArrayList<>();
    }

    @Override
    public void addPositiveSupply(PowerPositive powerPositive) {
        lastPowerPositive = powerPositive;
        powerPositives.add(powerPositive);
    }

    @Override
    public void addNegativeSupply(PowerNegative powerNegative) {
        lastPowerNegative = powerNegative;
        powerNegatives.add(powerNegative);
    }

    @Override
    public void clearMarket() {

    }

    @Override
    public float getTotalClearedPositiveQuantity() {
        return 0;
    }

    @Override
    public float getTotalClearedNegativeQuantity() {
        return 0;
    }

    @Override
    public float getLastPositiveAssignmentRate() {
        return 0;
    }

    @Override
    public float getLastClearedNegativeMaxPrice() {
        return 0;
    }

    @Override
    public float getLastNegativeAssignmentRate() {
        return 0;
    }

    @Override
    public float getLastClearedPositiveMaxPrice() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    public PowerNegative getLastPowerNegative() {
        return lastPowerNegative;
    }

    public PowerPositive getLastPowerPositive() {
        return lastPowerPositive;
    }

    public PowerNegative getPowerNegative(int i) {
        return powerNegatives.get(i);
    }

    public PowerPositive getPowerPositive(int i) {
        return powerPositives.get(i);
    }
}
