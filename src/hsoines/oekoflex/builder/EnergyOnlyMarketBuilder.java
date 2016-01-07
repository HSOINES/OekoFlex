package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.CombinedEnergyProducer;
import hsoines.oekoflex.energytrader.impl.DaytimeEnergyConsumer;
import hsoines.oekoflex.energytrader.impl.ParametrizableEnergyProducer;
import hsoines.oekoflex.energytrader.impl.SimpleEnergyProducer;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.EnergyOnlyMarketOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;

public class EnergyOnlyMarketBuilder implements ContextBuilder<OekoflexAgent>{

	public static final int TICKS_TO_RUN = 24 * 4;

	@Override
	public Context build(Context<OekoflexAgent> context) {

		context.setId("OekoFlex");

		RunEnvironment re = RunEnvironment.getInstance();
		re.endAt(TICKS_TO_RUN);

		EnergyOnlyMarketOperatorImpl energyOnlyMarketOperator = new EnergyOnlyMarketOperatorImpl("EOM_Operator");
		RegelEnergieMarketOperator regelEnergieMarketOperator = new RegelEnergieMarketOperatorImpl("RegelEnergieMarketOperator", 10000);
		context.add(energyOnlyMarketOperator);
		for (int i = 0; i < 5; i++){
			SimpleEnergyProducer prod = new SimpleEnergyProducer("SimpleEnergyProducer_" + i);
			prod.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
			context.add(prod);
		}
		
		for (int i = 0; i < 45; i++){
			DaytimeEnergyConsumer consumer = new DaytimeEnergyConsumer("DaytimeEnergyConsumer_" + i);
			consumer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
			context.add(consumer);
		}

		for (int i = 0; i < 50; i++){
			ParametrizableEnergyProducer producer = new ParametrizableEnergyProducer("ParametrizableEnergyProducer_" + i);
			producer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
			context.add(producer);
		}

		for (int i = 0; i < 50; i++) {
			CombinedEnergyProducer producer = new CombinedEnergyProducer("CombinedEnergyProducer" + i);
			producer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
			producer.setRegelEnergieMarketOperator(regelEnergieMarketOperator);
			context.add(producer);
		}

		return context;
	}

}
