package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.TimeUtil;
import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public final class PreRunner {
    private final Context<OekoflexAgent> context;

    public PreRunner(final Context<OekoflexAgent> context) {
        this.context = context;
    }

    public void run(final int prerunTicks) {
        final IndexedIterable<OekoflexAgent> balancingMarketOperatorIterator = context.getObjects(BalancingMarketOperator.class);
        final IndexedIterable<OekoflexAgent> spotMarketOperatorIterator = context.getObjects(SpotMarketOperator.class);
        final IndexedIterable<OekoflexAgent> balancingMarketTraderIterator = context.getObjects(BalancingMarketTrader.class);
        final IndexedIterable<OekoflexAgent> eomTraderIterator = context.getObjects(EOMTrader.class);

        final BalancingMarketOperator balancingMarketOperator = (BalancingMarketOperator) balancingMarketOperatorIterator.get(0);
        final SpotMarketOperator spotMarketOperator = (SpotMarketOperator) spotMarketOperatorIterator.get(0);
        final List<BalancingMarketTrader> balancingMarketTraders = extract(balancingMarketTraderIterator);
        final List<EOMTrader> eomTraders = extract(eomTraderIterator);

        run_i(balancingMarketOperator, spotMarketOperator, balancingMarketTraders, eomTraders, prerunTicks);
    }

    private void run_i(final BalancingMarketOperator balancingMarketOperator, final SpotMarketOperator spotMarketOperator, final List<BalancingMarketTrader> balancingMarketTraders, final List<EOMTrader> eomTraders, final int prerunTicks) {
        TimeUtil.startAt(-prerunTicks);
        long currentTick = TimeUtil.getCurrentTick();
        while (currentTick < 0) {
            if (currentTick % SequenceDefinition.BalancingMarketInterval == 0) {
                for (BalancingMarketTrader balancingMarketTrader : balancingMarketTraders) {
                    balancingMarketTrader.makeBidBalancingMarket(currentTick);
                }
                if(TimeUtil.getCurrentTick() % 15 == 0){  				// TODO: Check and debug
                	balancingMarketOperator.clearMarketCapacityPrice();	// TODO: Check and debug
                }														// TODO: Check and debug
                balancingMarketOperator.clearMarketEnergyPrice();		// TODO: Check and debug
            }
            for (EOMTrader eomTrader : eomTraders) {
                eomTrader.makeBidEOM(currentTick);
            }
            spotMarketOperator.clearMarket();
            TimeUtil.nextTick();
            currentTick = TimeUtil.getCurrentTick();
        }
        TimeUtil.reset();
    }

    private <T extends OekoflexAgent> List<T> extract(final IndexedIterable<OekoflexAgent> iterator) {
        List<T> list = new ArrayList<>();
        for (OekoflexAgent oekoflexAgent : iterator) {
            list.add((T) oekoflexAgent);
        }
        return list;
    }


}
