package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
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
import java.util.HashSet;
import java.util.Set;

/**
 * User: jh
 * Date: 07/01/16
 * Time: 21:32
 */
public final class FlexPowerplantFactory {
    private static final Log log = LogFactory.getLog(FlexPowerplantFactory.class);
    private static PriceForwardCurve priceForwardCurve;

    public static void build(final File configDir,
                             final Context<OekoflexAgent> context,
                             final SpotMarketOperatorImpl energyOnlyMarketOperator,
                             final BalancingMarketOperator balancingMarketOperator,
                             final PriceForwardCurve priceForwardCurve) throws IOException {
        FlexPowerplantFactory.priceForwardCurve = priceForwardCurve;
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
                float marginalCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("marginalCosts")).floatValue();
                float shutdownCosts = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("shutdownCosts")).floatValue();

                FlexPowerplant flexPowerplant = new FlexPowerplant(name, description, powerMax, powerMin, rampUp, rampDown, marginalCosts, shutdownCosts, priceForwardCurve);
                flexPowerplants.add(flexPowerplant);
                log.info("FlexPowerplant Build done for <" + name + ">.");
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            } catch (ParseException e) {
                log.error(e.toString(), e);
            }
        }
        return flexPowerplants;
    }
}
