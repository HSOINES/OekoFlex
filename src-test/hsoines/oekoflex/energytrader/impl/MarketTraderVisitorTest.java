package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.MarketTrader;
import hsoines.oekoflex.energytrader.MarketTraderVisitor;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 20:50
 */
public final class MarketTraderVisitorTest {
    private static final Log log = LogFactory.getLog(MarketTraderVisitorTest.class);

    private Date date0;
    private Date date1;
    private Date date2;

    @Before
    public void setUp() throws Exception {
        date0 = TimeUtilities.getDate(0);
        date1 = TimeUtilities.getDate(1);
        date2 = TimeUtilities.getDate(2);

    }

    @Test
    public void testTradingActionVisitor() throws Exception {
        MarketTrader marketTrader = new RegelenergieMarketTrader() {
            @Override
            public void notifyRegelenergieClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate) {

            }

            @Override
            public void makeBidRegelenergie() {

            }

            @Override
            public void setRegelenergieMarketOperator(final RegelEnergieMarketOperator marketOperator) {

            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public void accept(final MarketTraderVisitor visitor) {
                visitor.visit(this);
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
            public float getLastBidPrice() {
                return 0;
            }

            @Override
            public hsoines.oekoflex.energytrader.EnergyTradeHistory getProducedEnergyTradeHistory() {
                EnergyTradeHistoryImpl tradeHistory = new EnergyTradeHistoryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.CONSUM, 3000);
                assertEquals(3000, tradeHistory.getRemainingCapacity(date0, Duration.QUARTER_HOUR));
                tradeHistory.addAssignedQuantity(date0, Duration.QUARTER_HOUR, 2000, 12.3f);
                return tradeHistory;
            }
        };

        marketTrader.accept(new MarketTraderVisitor() {
            @Override
            public void visit(final EOMTrader eomTrader) {

            }

            @Override
            public void visit(final RegelenergieMarketTrader regelenergieMarketTrader) {
                hsoines.oekoflex.energytrader.EnergyTradeHistory energyTradeHistory = regelenergieMarketTrader.getProducedEnergyTradeHistory();
                int available = energyTradeHistory.getRemainingCapacity(date0, Duration.QUARTER_HOUR);
                int used = energyTradeHistory.getEnergyUsed(date0);
                List<EnergyTradeHistoryImpl.EnergyTradeHistoryElement> historyElements = energyTradeHistory.getHistoryElements(date0);

                hsoines.oekoflex.energytrader.EnergyTradeHistory.Type type = energyTradeHistory.getType();
                log.info(available + " " + used + " First Price: " + historyElements.get(0).getPrice());
            }
        });

    }
}
