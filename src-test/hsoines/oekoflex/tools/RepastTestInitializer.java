package hsoines.oekoflex.tools;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.Schedule;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 20:07
 */
public final class RepastTestInitializer {
    public static Context init() {
        Schedule schedule = new Schedule();
        RunEnvironment.init(schedule, null, null, true);
        Context context = new DefaultContext();
        RunState.init().setMasterContext(context);
        return context;
    }
}
