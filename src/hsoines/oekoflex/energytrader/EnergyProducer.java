package hsoines.oekoflex.energytrader;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 02/12/15
 * Time: 23:34
 */
public interface EnergyProducer {
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    void makeSupply();
}
