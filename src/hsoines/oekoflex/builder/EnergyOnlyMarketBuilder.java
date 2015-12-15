package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.impl.EnergyOnlyMarketOperator;
import hsoines.oekoflex.impl.ParametrizableEnergyProducer;
import hsoines.oekoflex.impl.SimpleEnergyConsumer;
import hsoines.oekoflex.impl.SimpleEnergyProducer;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

import java.util.TimeZone;

public class EnergyOnlyMarketBuilder implements ContextBuilder<OekoflexAgent>{

	public static final int TICKS_TO_RUN = 24 * 4;

	@Override
	public Context build(Context<OekoflexAgent> context) {

		context.setId("EnergyOnlyMarket");

		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		RunEnvironment re = RunEnvironment.getInstance();
		re.endAt(TICKS_TO_RUN);
		
        EnergyOnlyMarketOperator mo = new EnergyOnlyMarketOperator("EOM_Operator");
		context.add(mo);
		for (int i = 0; i < 5; i++){
			SimpleEnergyProducer prod = new SimpleEnergyProducer("SimpleEnergyProducer_" + i);
			prod.setMarketOperator(mo);
			context.add(prod);
		}
		
		for (int i = 0; i < 45; i++){
			SimpleEnergyConsumer consumer = new SimpleEnergyConsumer("SimpleEnergyConsumer_" + i);
			consumer.setMarketOperator(mo);
			context.add(consumer);
		}

		for (int i = 0; i < 50; i++){
			ParametrizableEnergyProducer producer = new ParametrizableEnergyProducer("ParametrizableEnergyProducer_" + i);
			producer.setMarketOperator(mo);
			context.add(producer);
		}

		return context;
	}

}
