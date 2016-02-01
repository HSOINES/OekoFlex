package hsoines.oekoflex.tools;

import hsoines.oekoflex.energytrader.impl.test.CombinedEnergyProducer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 16/01/16
 * Time: 16:01
 */
public final class RepastTestSample {

    @Before
    public void setUp() throws Exception {
        Schedule schedule = new Schedule();
        RunEnvironment.init(schedule, null, null, true);
        Context context = new DefaultContext();
        RunState.init().setMasterContext(context);

        // Any additional setup
    }

    @Test
    @Ignore("execute() doesn't schedule. ")
    public void testUninfectedToInfected() {
        ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
        CombinedEnergyProducer combinedTest = new CombinedEnergyProducer("combinedTest");
        assertEquals(-1, RunEnvironment.getInstance().getCurrentSchedule().getTickCount(), 0.001);
        for (int i = 0; i < 5; ++i) {
            schedule.schedule(combinedTest);
            schedule.execute();
        }
        assertEquals(4, schedule.getTickCount(), 0.001);
    }
}
