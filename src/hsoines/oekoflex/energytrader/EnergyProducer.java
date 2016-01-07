package hsoines.oekoflex.energytrader;

import hsoines.oekoflex.energytrader.EnergyTrader;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 02/12/15
 * Time: 23:34
 */
public interface EnergyProducer extends EnergyTrader {
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    void makeSupply();
}
