package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.ParametrizableEnergyProducer;
import hsoines.oekoflex.energytrader.impl.SimpleEnergyProducer;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.EnergyOnlyMarketOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.io.File;
import java.io.IOException;

public class OekoFlexContextBuilder implements ContextBuilder<OekoflexAgent> {
    private static final Log log = LogFactory.getLog(OekoFlexContextBuilder.class);

    @Override
    public Context build(Context<OekoflexAgent> context) {

        context.setId("OekoFlex");

        Parameters p = RunEnvironment.getInstance().getParameters();
        RunEnvironment re = RunEnvironment.getInstance();
        int daysToRun = (int) p.getValue("daysToRun");
        re.endAt(daysToRun * 96);

        String configDirString = (String) p.getValue("runConfigDir");
        File configDir = new File(configDirString);
        if (!configDir.exists()) {
            log.error("Configuration directory is not existing: " + configDirString);
            re.endRun();
        }


        EnergyOnlyMarketOperatorImpl energyOnlyMarketOperator = new EnergyOnlyMarketOperatorImpl("EOM_Operator");
        RegelEnergieMarketOperator regelenergieMarketOperator = new RegelEnergieMarketOperatorImpl("RegelEnergieMarketOperator");
        context.add(energyOnlyMarketOperator);
        context.add(regelenergieMarketOperator);

        try {
            CombinedEnergyProducerFactory.build(configDir, context, energyOnlyMarketOperator, regelenergieMarketOperator);
            DaytimeEnergyConsumerFactory.build(configDir, context, energyOnlyMarketOperator);
        } catch (IOException e) {
            log.error(e.toString(), e);
            re.endRun();
        }

        for (int i = 0; i < 5; i++) {
            SimpleEnergyProducer prod = new SimpleEnergyProducer("SimpleEnergyProducer_" + i);
            prod.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
            context.add(prod);
        }

//        for (int i = 0; i < 45; i++) {
//            DaytimeEnergyConsumer consumer = new DaytimeEnergyConsumer("DaytimeEnergyConsumer_" + i);
//            consumer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
//            context.add(consumer);
//        }

        for (int i = 0; i < 50; i++) {
            ParametrizableEnergyProducer producer = new ParametrizableEnergyProducer("ParametrizableEnergyProducer_" + i);
            producer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
            context.add(producer);
        }

//        for (int i = 0; i < 3; i++) {
//            CombinedEnergyProducer producer = new CombinedEnergyProducer("CombinedEnergyProducer_" + i);
//            producer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
//            producer.setRegelEnergieMarketOperator(regelEnergieMarketOperator);
//            context.add(producer);
//        }

        return context;
    }

}
