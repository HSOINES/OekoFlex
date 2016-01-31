package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.Storage;
import hsoines.oekoflex.marketoperator.EOMOperator;
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
public final class StorageFactory {
    private static final Log log = LogFactory.getLog(StorageFactory.class);

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final EOMOperator eomOperator) throws IOException {
        File configFile = new File(configDir + "/" + "Storage.cfg.csv");
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

                int capacity = Integer.parseInt(parameters.get("capacity"));

                Storage storage = new Storage(name, powerMax, powerMin, rampUp, rampDown, marginalCosts, shutdownCosts, capacity);
                storage.setEOMOperator(eomOperator);
                context.add(storage);

                log.info("Storage Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
