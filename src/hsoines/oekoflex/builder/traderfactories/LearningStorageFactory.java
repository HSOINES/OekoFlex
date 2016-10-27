package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.energytrader.impl.LearningStorage;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import repast.simphony.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Factory class for the learning storage plant 3 class
 * reads values of the csv-files and creates learning storage agents based on this information
 */
public final class LearningStorageFactory {
	/** */
    private static final Log log = LogFactory.getLog(LearningStorageFactory.class);
    
    /**
     * 
     * @param configDir	the config directory
     * @param context	the Repast context
     * @param spotMarketOperator	operator for the energy only market
     * @param balancingMarketOperator operator for the balancing power market
     * @param priceForwardCurve	the specific price forward curve for this scenario
     * @throws IOException
     */
    public static void build(final File configDir, final Context<OekoflexAgent> context, final SpotMarketOperator spotMarketOperator, final BalancingMarketOperator balancingMarketOperator, final PriceForwardCurve priceForwardCurve) throws IOException {
        
    	File configFile = new File(configDir + "/" + "LearningStorage.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                String description = parameters.get("description");
                float marginalCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("marginalCosts")).floatValue();
                int energyCapacity = Integer.parseInt(parameters.get("energyCapacity"));
                int chargePower = Integer.parseInt(parameters.get("chargePower"));
                int dischargePower = Integer.parseInt(parameters.get("dischargePower"));
                int startStopCosts = Integer.parseInt(parameters.get("startStopCosts"));
                LearningStorage storage = new LearningStorage(name, description, chargePower , dischargePower, startStopCosts, priceForwardCurve ,  marginalCosts,  energyCapacity);

                storage.setSpotMarketOperator(spotMarketOperator);
//                storage.setBalancingMarketOperator(balancingMarketOperator);
                context.add(storage); // so Repast knows the Object. context = RepastContext

                log.info("Storage Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            } catch (ParseException e) {
                log.error(e.toString(), e);
            }
        }
    }
}
