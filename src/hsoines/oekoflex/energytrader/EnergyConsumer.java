package hsoines.oekoflex.energytrader;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 03/12/15
 * Time: 08:28
 */
public interface EnergyConsumer extends EnergyOnlyMarketTrader {
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    void makeDemand();
}
