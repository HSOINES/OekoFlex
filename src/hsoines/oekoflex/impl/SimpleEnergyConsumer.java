package hsoines.oekoflex.impl;

import hsoines.oekoflex.EnergyConsumer;
import hsoines.oekoflex.MarketOperator;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public final class SimpleEnergyConsumer implements EnergyConsumer {
    private MarketOperator marketOperator;

    @Override
    public void setMarketOperator(final MarketOperator marketOperator) {
        this.marketOperator = marketOperator;
    }
}
