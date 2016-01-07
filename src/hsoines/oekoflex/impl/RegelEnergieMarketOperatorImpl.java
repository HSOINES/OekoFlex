package hsoines.oekoflex.impl;

import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.RegelenergieMarketOperator;
import hsoines.oekoflex.supply.Supply;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 10:55
 */
public final class RegelenergieMarketOperatorImpl implements RegelenergieMarketOperator, OekoflexAgent {
    private final String name;
    private final int quantity;
    private final List<Supply> supplies;

    public RegelenergieMarketOperatorImpl(String name, int quantity) {
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
            if (clearedQuantity + supply.getQuantity() < quantity){
                clearedQuantity += supply.getQuantity();
                marketOperatorListener.notifyClearingDone(supply.getPrice(), 1, supply);
            } else if (clearedQuantity >= quantity){
                break;
            } else {
                float assignRate = (quantity - clearedQuantity)/supply.getQuantity();
                marketOperatorListener.notifyClearingDone(supply.getPrice(), assignRate, supply);
                clearedQuantity += supply.getQuantity()*assignRate;
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
