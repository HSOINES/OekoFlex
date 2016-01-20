package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.FlexPowerplantProducer;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 21:32
 */
public final class FlexPowerplantProducerFactory {
    private static final Log log = LogFactory.getLog(FlexPowerplantProducerFactory.class);

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final EOMOperatorImpl energyOnlyMarketOperator,
                             final RegelEnergieMarketOperator regelEnergieMarketOperator) throws IOException {
        File configFile = new File(configDir + "/" + "FlexPowerplantProducer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                int capacity = Integer.parseInt(parameters.get("capacity"));
                float costs = Float.parseFloat(parameters.get("costs"));
                float quantityDelay = Float.parseFloat(parameters.get("quantityDelay"));

                FlexPowerplantProducer flexPowerplantProducer = new FlexPowerplantProducer(name, capacity, costs, quantityDelay);
                flexPowerplantProducer.setEOMOperator(energyOnlyMarketOperator);
                flexPowerplantProducer.setRegelenergieMarketOperator(regelEnergieMarketOperator);
                context.add(flexPowerplantProducer);

                log.info("FlexPowerplantProducer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
