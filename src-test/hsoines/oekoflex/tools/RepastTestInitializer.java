package hsoines.oekoflex.tools;

import hsoines.oekoflex.OekoflexAgent;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 20:07
 */
public final class RepastTestInitializer {

    private static Schedule schedule;

    public static Context<OekoflexAgent> init() {
        schedule = new Schedule();
        RunEnvironment.init(schedule, null, null, true);
        Context<OekoflexAgent> context = new DefaultContext<>();
        RunState.init().setMasterContext(context);
        return context;
    }

    public static void next() {
        ISchedule currentSchedule = RunEnvironment.getInstance().getCurrentSchedule();
        currentSchedule.execute();
    }
}
