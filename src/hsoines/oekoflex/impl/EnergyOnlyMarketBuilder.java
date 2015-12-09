package hsoines.oekoflex.impl;

import hsoines.oekoflex.OekoflexAgent;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

public class EnergyOnlyMarketBuilder implements ContextBuilder<OekoflexAgent>{

	public static final int TICKS_TO_RUN = 24 * 4;

	@Override
	public Context build(Context<OekoflexAgent> context) {
		context.setId("EnergyOnlyMarket");
		
		RunEnvironment re = RunEnvironment.getInstance();
		re.endAt(TICKS_TO_RUN);
		
        EnergyOnlyMarketOperator mo = new EnergyOnlyMarketOperator("EOM_Operator");
		context.add(mo);
		for (int i = 0; i < 50; i++){
			SimpleEnergyProducer prod = new SimpleEnergyProducer("SimpleEnergyProducer_" + i);
			prod.setMarketOperator(mo);
			context.add(prod);
		}
		
		for (int i = 0; i < 50; i++){
			SimpleEnergyConsumer consumer = new SimpleEnergyConsumer("SimpleEnergyConsumer_" + i);
			consumer.setMarketOperator(mo);
			context.add(consumer);
		}

		return context;
	}

}
