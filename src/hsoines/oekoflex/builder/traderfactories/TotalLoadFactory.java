package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.TotalLoad;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Consumer/Producer, which are defined in a CSV-file (tick, Price) and offers based on the file.
 */
public final class TotalLoadFactory {
    private static final Log log = LogFactory.getLog(TotalLoadFactory.class);
    
    /**
     * 
     * @param configDir	the config directory
     * @param context	the Repast context
     * @param spotMarketOperator	operator for the energy only market
     * @param prerunTicks	number of ticks for prerun 
     * @throws IOException
     */
    public static void build(final File configDir, final Context<OekoflexAgent> context, final SpotMarketOperatorImpl spotMarketMarketOperator, long prerunTicks) throws IOException {
        Set<TotalLoad> totalLoads = build(configDir, prerunTicks);
        for (TotalLoad totalLoad : totalLoads) {
            totalLoad.setSpotMarketOperator(spotMarketMarketOperator);
            context.add(totalLoad);
        }
    }
    
    public static Set<TotalLoad> build(File configDir, final long prerunTicks) throws IOException {
        Set<TotalLoad> totalLoads = new HashSet<>();
        File configFile = new File(configDir + "/" + "TotalLoad.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                String description = parameters.get("description");
                String typeString = parameters.get("type");
                String dataFileName = parameters.get("dataFile");
                File dataFile = new File(configDir, dataFileName);

                TotalLoad.Type type = TotalLoad.Type.valueOf(typeString);
                TotalLoad totalLoad = new TotalLoad(name, description, type, dataFile, prerunTicks);
                totalLoads.add(totalLoad);

                log.info("TotalLoad Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
        return totalLoads;
    }
}
