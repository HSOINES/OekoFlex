package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.DaytimeEnergyConsumer;
import hsoines.oekoflex.marketoperator.impl.EnergyOnlyMarketOperatorImpl;
import hsoines.oekoflex.summary.BidSummary;
import hsoines.oekoflex.summary.BidSummaryFactory;
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
public final class DaytimeEnergyConsumerFactory {
    private static final Log log = LogFactory.getLog(DaytimeEnergyConsumerFactory.class);

    public static void build(final File configDir, final Context<OekoflexAgent> context, final EnergyOnlyMarketOperatorImpl energyOnlyMarketOperator) throws IOException {
        File configFile = new File(configDir + "/" + "DaytimeEnergyConsumer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVFormat.DEFAULT.withHeader().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");// + "_" + Long.toHexString(System.currentTimeMillis());
                int quantity = Integer.parseInt(parameters.get("quantity"));
                float priceAtDay = Float.parseFloat(parameters.get("priceAtDay"));
                float decreaseAtNightInPercent = Float.parseFloat(parameters.get("decreaseAtNightInPercent"));

                DaytimeEnergyConsumer daytimeEnergyConsumer = new DaytimeEnergyConsumer(name, quantity, priceAtDay, decreaseAtNightInPercent);
                daytimeEnergyConsumer.setEnergieOnlyMarketOperator(energyOnlyMarketOperator);
                context.add(daytimeEnergyConsumer);

                BidSummary bidSummary = BidSummaryFactory.create(name);
                daytimeEnergyConsumer.setBidSummary(bidSummary);
                log.info("CombinedEnergyProducer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
