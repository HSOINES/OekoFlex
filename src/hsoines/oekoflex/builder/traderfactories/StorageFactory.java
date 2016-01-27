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
                int capacity = Integer.parseInt(parameters.get("capacity"));
                float costs = Float.parseFloat(parameters.get("costs"));
                float spread = Float.parseFloat(parameters.get("spread"));

                Storage storage = new Storage(name, capacity, costs, spread);
                storage.setEOMOperator(eomOperator);
                context.add(storage);

                log.info("Storage Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
