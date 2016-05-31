package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.energytrader.impl.SimpleStorage;
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
 * User: jh
 * Date: 07/01/16
 * Time: 21:32
 */
public final class StorageFactory {
    private static final Log log = LogFactory.getLog(StorageFactory.class);
    private static PriceForwardCurve priceForwardCurve;

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final SpotMarketOperator spotMarketOperator, final BalancingMarketOperator balancingMarketOperator,
                             final PriceForwardCurve priceForwardCurve) throws IOException {
        StorageFactory.priceForwardCurve = priceForwardCurve;
        File configFile = new File(configDir + "/" + "Storage.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                String description = parameters.get("description");
                int powerMax = Integer.parseInt(parameters.get("powerMax"));
                int powerMin = Integer.parseInt(parameters.get("powerMin"));
                float marginalCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("marginalCosts")).floatValue();
                float shutdownCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("shutdownCosts")).floatValue();
                float socMax = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("socMax")).floatValue();
                float socMin = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("socMin")).floatValue();
                int capacity = Integer.parseInt(parameters.get("capacity"));
                int chargePower = Integer.parseInt(parameters.get("chargePower"));
                int dischargePower = Integer.parseInt(parameters.get("dischargePower"));

                SimpleStorage storage = new SimpleStorage(name, description,
                        marginalCosts, shutdownCosts, capacity,
                        socMax, socMin, chargePower, dischargePower,
                        priceForwardCurve,
                        true);
                storage.setSpotMarketOperator(spotMarketOperator);
                storage.setBalancingMarketOperator(balancingMarketOperator);
                context.add(storage);

                log.info("Storage Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            } catch (ParseException e) {
                log.error(e.toString(), e);
            }
        }
    }
}
