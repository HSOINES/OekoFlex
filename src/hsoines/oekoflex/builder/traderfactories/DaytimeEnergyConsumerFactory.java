package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.test.DaytimeEnergyConsumer;
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
public final class DaytimeEnergyConsumerFactory {
    private static final Log log = LogFactory.getLog(DaytimeEnergyConsumerFactory.class);

    public static void build(final File configDir, final Context<OekoflexAgent> context, final EOMOperatorImpl energyOnlyMarketOperator) throws IOException {
        File configFile = new File(configDir + "/" + "DaytimeEnergyConsumer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");// + "_" + Long.toHexString(System.currentTimeMillis());
                float quantity = Float.parseFloat(parameters.get("quantity"));
                float priceAtDay = Float.parseFloat(parameters.get("priceAtDay"));
                float decreaseAtNightInPercent = Float.parseFloat(parameters.get("decreaseAtNightInPercent"));

                DaytimeEnergyConsumer daytimeEnergyConsumer = new DaytimeEnergyConsumer(name, quantity, priceAtDay, decreaseAtNightInPercent);
                daytimeEnergyConsumer.setEOMOperator(energyOnlyMarketOperator);
                context.add(daytimeEnergyConsumer);

                log.info("DaytimeEnergyConsumer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
