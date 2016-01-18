package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.energytrader.impl.FixedDemandConsumer;
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
public final class FixedDemandConsumerFactory {
    private static final Log log = LogFactory.getLog(FixedDemandConsumerFactory.class);

    public static void build(final File configDir, final Context<OekoflexAgent> context, final EOMOperatorImpl energyOnlyMarketOperator) throws IOException {
        File configFile = new File(configDir + "/" + "FixedDemandConsumer.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                String demandFileName = parameters.get("demandFile");
                File demandFile = new File(configDir, demandFileName);

                FixedDemandConsumer fixedDemandConsumer = new FixedDemandConsumer(name, demandFile);
                fixedDemandConsumer.setEOMOperator(energyOnlyMarketOperator);
                context.add(fixedDemandConsumer);

                log.info("FixedDemandConsumer Build done: " + name);
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
