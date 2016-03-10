package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.Market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 21:04
 */
public final class ResidualSupplier implements EOMTrader {
    private final float quantity;
    private SpotMarketOperator spotMarketOperator;

    public ResidualSupplier(float quantity) {
        this.quantity = quantity;
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.spotMarketOperator = spotMarketOperator;
    }

    @Override
    public float getLastClearedPrice() {
        return 0;
    }

    @Override
    public void makeBidEOM() {
        spotMarketOperator.addSupply(new EnergySupply(-3000, quantity, null));
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

    }

    @Override
    public float getLastAssignmentRate() {
        return 0;
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getName() {
        return "residual operator";
    }
}
