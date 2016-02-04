package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.traderfactories.*;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.RegelEnergieMarketOperatorImpl;
import hsoines.oekoflex.summary.EnergyTraderTypeLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OekoFlexContextBuilder implements ContextBuilder<OekoflexAgent> {
    private static final Log log = LogFactory.getLog(OekoFlexContextBuilder.class);

    @Override
    public Context build(Context<OekoflexAgent> context) {

        context.setId("OekoFlex");

        Parameters p = RunEnvironment.getInstance().getParameters();
        RunEnvironment re = RunEnvironment.getInstance();
        int daysToRun = (int) p.getValue("daysToRun");
        re.endAt(daysToRun * 96);//todo

        String scenario = (String) p.getValue("scenario");
        String logDirName = "run/summary-logs/" + scenario;
        String configDirName = "run-config/" + scenario;
        File configDir = new File(configDirName);
        if (!configDir.exists()) {
            log.error("Configuration directory is not existing: " + scenario);
            re.endRun();
        }

        //remove log-dirs
        EnergyTraderTypeLogger energyTraderTypeLogger = new EnergyTraderTypeLogger(context, logDirName);
        context.add(energyTraderTypeLogger);

        try {
            Properties globalProperties = loadProperties(configDir);
            int positiveDemandREM = Integer.parseInt((String) globalProperties.get("positiveDemandREM"));
            int negativeDemandREM = Integer.parseInt((String) globalProperties.get("negativeDemandREM"));
            EOMOperatorImpl eomOperator = new EOMOperatorImpl("EOM_Operator", logDirName);
            RegelEnergieMarketOperator regelenergieMarketOperator = new RegelEnergieMarketOperatorImpl("RegelEnergieMarketOperator", logDirName, positiveDemandREM, negativeDemandREM);
            context.add(eomOperator);
            context.add(regelenergieMarketOperator);

            CombinedEnergyProducerFactory.build(configDir, context, eomOperator, regelenergieMarketOperator);
            DaytimeEnergyConsumerFactory.build(configDir, context, eomOperator);
            FlexibleDemandFactory.build(configDir, context, eomOperator);
            FlexPowerplantFactory.build(configDir, context, eomOperator, regelenergieMarketOperator);
            StorageFactory.build(configDir, context, eomOperator, regelenergieMarketOperator);

        } catch (IOException e) {
            log.error(e.toString(), e);
            re.endRun();
        }


        return context;
    }

    Properties loadProperties(final File configDir) throws IOException {
        Properties globalProperties = new Properties();
        File globalPropertiesFile = new File(configDir, "Global.properties");
        FileInputStream is = new FileInputStream(globalPropertiesFile);
        globalProperties.load(is);
        return globalProperties;
    }

}
