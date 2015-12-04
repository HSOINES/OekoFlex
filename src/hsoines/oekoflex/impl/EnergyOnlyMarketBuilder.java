package hsoines.oekoflex.impl;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.graph.Network;

public class EnergyOnlyMarketBuilder implements ContextBuilder<Object>{

	public static final int TICKS_TO_RUN = 365 * 24 * 4;

	@Override
	public Context build(Context<Object> context) {
		context.setId("EnergyOnlyMarket");
		
		RunEnvironment re = RunEnvironment.getInstance();
		re.endAt(TICKS_TO_RUN);
		
        SimpleMarketOperator mo = new SimpleMarketOperator();
		context.add(mo);
		for (int i = 0; i < 5; i++){
			SimpleEnergyProducer prod = new SimpleEnergyProducer();
			prod.setMarketOperator(mo);
			context.add(prod);
		}
		
		for (int i = 0; i < 5; i++){
			SimpleEnergyConsumer consumer = new SimpleEnergyConsumer();
			consumer.setMarketOperator(mo);
			context.add(consumer);
		}

		return context;
	}

}
