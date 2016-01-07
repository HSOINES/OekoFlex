package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.EnergyTimeZone;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 10:55
 */
public final class RegelEnergieMarketOperatorImpl implements RegelEnergieMarketOperator, OekoflexAgent {
    private final String name;
    private final int quantity;
    private final List<Supply> supplies;

    public RegelEnergieMarketOperatorImpl(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        supplies = new ArrayList<Supply>();
    }

    @Override
    public void addSupply(final Supply supply) {
        supplies.add(supply);
    }

    @Override
    public void clearMarket() {
        supplies.sort((o1, o2) -> Float.compare(o1.getPrice(), o2.getPrice()));
        int clearedQuantity = 0;
        for (Supply supply : supplies) {
            MarketOperatorListener marketOperatorListener = supply.getMarketOperatorListener();
            if (clearedQuantity + supply.getQuantity() < quantity) {
                clearedQuantity += supply.getQuantity();
                doNotify(supply, marketOperatorListener, 1);
            } else if (clearedQuantity >= quantity) {
                break;
            } else {
                float assignRate = (quantity - clearedQuantity) / supply.getQuantity();
                doNotify(supply, marketOperatorListener, assignRate);
                clearedQuantity += supply.getQuantity() * assignRate;
            }
        }
    }

    void doNotify(final Supply supply, final MarketOperatorListener marketOperatorListener, float assignRate) {
        long tick = TimeUtilities.getTick(TimeUtilities.getCurrentDate());
        for (int i = 0; i < EnergyTimeZone.FOUR_HOURS.getTicks(); i++) {
            marketOperatorListener.notifyClearingDone(supply.getPrice(), assignRate, supply, TimeUtilities.getDate(tick + i));
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
