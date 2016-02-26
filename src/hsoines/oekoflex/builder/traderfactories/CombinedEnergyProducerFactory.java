package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.test.CombinedEnergyProducer;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
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
public final class CombinedEnergyProducerFactory {
    private static final Log log = LogFactory.getLog(CombinedEnergyProducerFactory.class);

    public static void build(final File configDir, final Context<OekoflexAgent> context, final SpotMarketOperatorImpl energyOnlyMarketOperator, final BalancingMarketOperator balancingMarketOperator) throws IOException {
        File configFile = new File(configDir + "/CombinedEnergyProducer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");// + "_" + Long.toHexString(System.currentTimeMillis());
                int capacity = Integer.parseInt(parameters.get("capacity"));
                float priceRegelMarkt = Float.parseFloat(parameters.get("priceRegelMarkt"));
                float priceEnergyOnlyMarkt = Float.parseFloat(parameters.get("priceEnergyOnlyMarkt"));
                float quantityPercentageOnRegelMarkt = Float.parseFloat(parameters.get("quantityPercentageOnRegelMarkt"));

                CombinedEnergyProducer combinedEnergyProducer = new CombinedEnergyProducer(name);
                combinedEnergyProducer.setCapacity(capacity);
                combinedEnergyProducer.setPriceRegelMarkt(priceRegelMarkt);
                combinedEnergyProducer.setPriceEnergyOnlyMarkt(priceEnergyOnlyMarkt);
                combinedEnergyProducer.setQuantityPercentageOnRegelMarkt(quantityPercentageOnRegelMarkt);
                combinedEnergyProducer.setSpotMarketOperator(energyOnlyMarketOperator);
                combinedEnergyProducer.setBalancingMarketOperator(balancingMarketOperator);

                context.add(combinedEnergyProducer);

                CombinedEnergyProducerFactory.log.info("CombinedEnergyProducer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
