package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
import hsoines.oekoflex.util.Market;
import org.junit.Before;

import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 27/01/16
 * Time: 20:38
 */
public final class PositiveNegativeEnergyTraderTest {

    private PositiveNegativeEnergyTrader positiveNegativeEnergyTrader;
    private RegelEnergieMarketOperator positiveRegelEnergieMarketOperator;

    @Before
    public void setUp() throws Exception {
        positiveNegativeEnergyTrader = new PositiveNegativeEnergyTrader();
        positiveRegelEnergieMarketOperator = new RegelEnergieMarketOperatorImpl("positive_test", "", 1000, 1000);

        positiveNegativeEnergyTrader.setRegelenergieMarketOperator(positiveRegelEnergieMarketOperator);
    }

    class PositiveNegativeEnergyTrader implements RegelenergieMarketTrader {
        private RegelEnergieMarketOperator regelenergieMarketOperator;

        @Override
        public void makeBidRegelenergie() {
            regelenergieMarketOperator.addPositiveSupply(new PositiveSupply(10.0f, 100, this));
        }

        @Override
        public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator regelenergieMarketOperator) {
            this.regelenergieMarketOperator = regelenergieMarketOperator;
        }

        @Override
        public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {

        }

        @Override
        public float getLastAssignmentRate() {
            return 0;
        }

        @Override
        public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
