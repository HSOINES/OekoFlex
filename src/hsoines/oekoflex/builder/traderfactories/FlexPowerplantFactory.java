package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
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
 * User: jh
 * Date: 07/01/16
 * Time: 21:32
 */
public final class FlexPowerplantFactory {
    private static final Log log = LogFactory.getLog(FlexPowerplantFactory.class);

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final SpotMarketOperatorImpl energyOnlyMarketOperator,
                             final BalancingMarketOperator balancingMarketOperator) throws IOException {
        Set<FlexPowerplant> flexPowerplants = build(configDir);
        for (FlexPowerplant flexPowerplant : flexPowerplants) {
            flexPowerplant.setBalancingMarketOperator(balancingMarketOperator);
            flexPowerplant.setSpotMarketOperator(energyOnlyMarketOperator);
            context.add(flexPowerplant);
        }
    }

    public static Set<FlexPowerplant> build(File configDir) throws IOException {
        File configFile = new File(configDir + "/" + "FlexiblePowerplant.cfg.csv");
        FileReader reader = new FileReader(configFile);
        CSVParser format = CSVParameter.getCSVFormat().parse(reader);

        Set<FlexPowerplant> flexPowerplants = new HashSet<>();
        for (CSVRecord parameters : format) {
            try {
                String name = parameters.get("name");
                String description = parameters.get("description");
                int powerMax = Integer.parseInt(parameters.get("powerMax"));
                int powerMin = Integer.parseInt(parameters.get("powerMin"));
                int rampUp = Integer.parseInt(parameters.get("rampUp"));
                int rampDown = Integer.parseInt(parameters.get("rampDown"));
                float marginalCosts = Float.parseFloat(parameters.get("marginalCosts"));
                float shutdownCosts = Float.parseFloat(parameters.get("shutdownCosts"));


                FlexPowerplant flexPowerplant = new FlexPowerplant(name, description, powerMax, powerMin, rampUp, rampDown, marginalCosts, shutdownCosts);
                flexPowerplants.add(flexPowerplant);
                log.info("FlexPowerplant Build done for <" + name + ">.");
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }
        return flexPowerplants;
    }
}
