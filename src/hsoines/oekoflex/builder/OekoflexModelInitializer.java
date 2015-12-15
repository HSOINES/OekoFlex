package hsoines.oekoflex.builder;

import hsoines.oekoflex.display.MerritOrderGraph;
import repast.simphony.context.Context;
import repast.simphony.engine.controller.NullAbstractControllerAction;
import repast.simphony.engine.environment.GUIRegistryType;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.scenario.ModelInitializer;
import repast.simphony.scenario.Scenario;
import repast.simphony.visualization.IDisplay;

public class OekoflexModelInitializer  implements ModelInitializer  {
	  private IDisplay display;

	  static class DisplayUpdater implements IAction {

		    private IDisplay display;

		    public DisplayUpdater(IDisplay display) {
		      this.display = display;
		    }

		    public void execute() {
		      display.update();
		    }
		  }
	  
	@Override
	public void initialize(Scenario scen, RunEnvironmentBuilder builder) {
	    scen.addMasterControllerAction(new NullAbstractControllerAction() {
	        @Override
	        public void runInitialize(RunState runState, Context context, Parameters runParams) {
	          display = new MerritOrderGraph(context);
	          runState.getGUIRegistry().addDisplay("Merrit Order Graph", GUIRegistryType.OTHER, display);
	          runState.getScheduleRegistry().getModelSchedule().schedule(ScheduleParameters.createRepeating(1, 1,
	                  ScheduleParameters.END), new DisplayUpdater(display));
	        }

	        @Override
	        public void runCleanup(RunState runState, Context context) {
	          display.destroy();
	          display = null;
	        }

	        public String toString() {
	          return "Create a custom display";
	        }
	      });


	    }
}
