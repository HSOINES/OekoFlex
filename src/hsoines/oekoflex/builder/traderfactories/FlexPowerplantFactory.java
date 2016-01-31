package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant;
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
public final class FlexPowerplantFactory {
    private static final Log log = LogFactory.getLog(FlexPowerplantFactory.class);

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final EOMOperatorImpl energyOnlyMarketOperator,
                             final RegelEnergieMarketOperator regelEnergieMarketOperator) throws IOException {
        File configFile = new File(configDir + "/" + "FlexPowerplant.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                int powerMax = Integer.parseInt(parameters.get("powerMax"));
                int powerMin = Integer.parseInt(parameters.get("powerMin"));
                int rampUp = Integer.parseInt(parameters.get("rampUp"));
                int rampDown = Integer.parseInt(parameters.get("rampDown"));
                float marginalCosts = Float.parseFloat(parameters.get("marginalCosts"));
                float shutdownCosts = Float.parseFloat(parameters.get("shutdownCosts"));


                FlexPowerplant flexPowerplantProducer = new FlexPowerplant(name, powerMax, powerMin, rampUp, rampDown, marginalCosts, shutdownCosts);
                flexPowerplantProducer.setEOMOperator(energyOnlyMarketOperator);
                flexPowerplantProducer.setRegelenergieMarketOperator(regelEnergieMarketOperator);
                context.add(flexPowerplantProducer);

                log.info("FlexPowerplant Build done for <" + name + ">.");
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
