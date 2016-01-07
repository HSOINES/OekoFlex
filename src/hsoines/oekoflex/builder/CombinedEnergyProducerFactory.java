package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.CombinedEnergyProducer;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.marketoperator.impl.EnergyOnlyMarketOperatorImpl;
import org.apache.commons.csv.CSVFormat;
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

    public static void build(final File configDir, final Context<OekoflexAgent> context, final EnergyOnlyMarketOperatorImpl energyOnlyMarketOperator, final RegelEnergieMarketOperator regelEnergieMarketOperator) throws IOException {
        File configFile = new File(configDir + "/CombinedEnergyProducer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVFormat.DEFAULT.withHeader().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                int capacity = Integer.parseInt(parameters.get("capacity"));
                float priceRegelMarkt = Float.parseFloat(parameters.get("priceRegelMarkt"));
                float priceEnergyOnlyMarkt = Float.parseFloat(parameters.get("priceEnergyOnlyMarkt"));
                float quantityPercentageOnRegelMarkt = Float.parseFloat(parameters.get("quantityPercentageOnRegelMarkt"));

                CombinedEnergyProducer combinedEnergyProducer = new CombinedEnergyProducer(name);
                combinedEnergyProducer.setCapacity(capacity);
                combinedEnergyProducer.setPriceRegelMarkt(priceRegelMarkt);
                combinedEnergyProducer.setPriceEnergyOnlyMarkt(priceEnergyOnlyMarkt);
                combinedEnergyProducer.setQuantityPercentageOnRegelMarkt(quantityPercentageOnRegelMarkt);
                combinedEnergyProducer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
                combinedEnergyProducer.setRegelEnergieMarketOperator(regelEnergieMarketOperator);

                context.add(combinedEnergyProducer);

                log.info("CombinedEnergyProducer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
