package hsoines.oekoflex.builder.traderfactories;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant3;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import repast.simphony.context.Context;

/**
 * Factory class for the flexible power plant 3 class
 * reads values of the csv files and creates fpp3 agents based on this information
 */
public class FlexPowerplant3Factory {
	 private static final Log log = LogFactory.getLog(FlexPowerplant3Factory.class);
	    private static PriceForwardCurve priceForwardCurve;


	 /**
	 * 
	 * @param configDir the config directory
	 * @param context the Repast context
	 * @param spotMarketOperator operator for the energy only market
	 * @param balancingMarketOperator operator for the balancing power market
	 * @param priceForwardCurve the specific price forward curve for this scenario
	 * @param globalProperties global properties for scenario
	 * @throws IOException
	 */
	    public static void build(final File configDir,final Context<OekoflexAgent> context, final SpotMarketOperatorImpl spotMarketMarketOperator, final BalancingMarketOperator balancingMarketOperator,final PriceForwardCurve priceForwardCurve, final Properties globalProperties) throws IOException {
	        
	    	FlexPowerplant3Factory.priceForwardCurve = priceForwardCurve;
	        Set<FlexPowerplant3> flexPowerplants = build(configDir, globalProperties);
	        for (FlexPowerplant3 flexPowerplant : flexPowerplants) {
	            flexPowerplant.setBalancingMarketOperator(balancingMarketOperator);
	            flexPowerplant.setSpotMarketOperator(spotMarketMarketOperator);
	            context.add(flexPowerplant);
	        }
	    }

	    public static Set<FlexPowerplant3> build(File configDir, final Properties globalProperties) throws IOException {
	        File configFile = new File(configDir + "/" + "FlexiblePowerplant.cfg.csv");
	        FileReader reader = new FileReader(configFile);
	        CSVParser format = CSVParameter.getCSVFormat().parse(reader);
	        Set<FlexPowerplant3> flexPowerplants = new HashSet<>();
	        for (CSVRecord parameters : format) {
	            try {
	                String name = parameters.get("name");
	                String description = parameters.get("description");
	                int powerMax = Integer.parseInt(parameters.get("powerMax"));
	                int powerMin = Integer.parseInt(parameters.get("powerMin"));
	                float efficiency = OekoFlexContextBuilder.defaultNumberFormat.parse(parameters.get("efficiency")).floatValue();
	                int rampUp = Integer.parseInt(parameters.get("rampUp"));
	                int rampDown = Integer.parseInt(parameters.get("rampDown"));

	                float variableCosts = getVariableCosts(globalProperties, description);
	                float fuelCosts = getFuelCosts(globalProperties, description);
	                final float emissionRate = getEmissionRate(globalProperties, description);
//	                final float startStopCosts = getStartStopCosts(globalProperties, description); // Notwendig???

	                float co2CertificateCosts = Float.parseFloat(globalProperties.getProperty("CO2CertificatesCosts"));
	                float cost_startUp = 1.0f; // TODO: read values from csv
					float cost_shutDown = 1.0f;// TODO: read values from csv
					FlexPowerplant3 flexPowerplant = new FlexPowerplant3(name, description, powerMax, powerMin, efficiency, rampUp, rampDown, FlexPowerplant3Factory.priceForwardCurve, variableCosts, fuelCosts, co2CertificateCosts, emissionRate,cost_startUp, cost_shutDown);
	                flexPowerplants.add(flexPowerplant);
	                log.info("FlexPowerplant2 Build done for <" + name + ">.");
	            } catch (NumberFormatException e) {
	                log.error(e.getMessage(), e);
	                throw e;
	            } catch (ParseException e) {
	                log.error(e.toString(), e);
	            }
	        }
	        return flexPowerplants;
	    }

	    @SuppressWarnings("unused")
		private static float getStartStopCosts(Properties globalProperties, String description) {
	        float startStopCosts;
	        switch (description) {
	            case "lignite":
	                startStopCosts = Float.parseFloat(globalProperties.getProperty("StartStopCosts_Lignite"));
	                break;
	            case "hard coal":
	                startStopCosts = Float.parseFloat(globalProperties.getProperty("StartStopCosts_HardCoal"));
	                break;
	            case "natural gas (open cycle)":
	                startStopCosts = Float.parseFloat(globalProperties.getProperty("StartStopCosts_NaturalGasOpenCycle"));
	                break;
	            case "natural gas (combined cycle)":
	                final String variableCosts_naturalGasCombinedCycle = globalProperties.getProperty("StartStopCosts_NaturalGasCombinedCycle");
	                startStopCosts = Float.parseFloat(variableCosts_naturalGasCombinedCycle);
	                break;
	            case "oil":
	                startStopCosts = Float.parseFloat(globalProperties.getProperty("StartStopCosts_Oil"));
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        return startStopCosts;
	    }

	    static float getVariableCosts(final Properties globalProperties, final String description) {
	        float variableCosts;
	        switch (description) {
	            case "lignite":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("VariableCosts_Lignite"));
	                break;
	            case "hard coal":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("VariableCosts_HardCoal"));
	                break;
	            case "natural gas (open cycle)":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("VariableCosts_NaturalGasOpenCycle"));
	                break;
	            case "natural gas (combined cycle)":
	                final String variableCosts_naturalGasCombinedCycle = globalProperties.getProperty("VariableCosts_NaturalGasCombinedCycle");
	                variableCosts = Float.parseFloat(variableCosts_naturalGasCombinedCycle);
	                break;
	            case "oil":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("VariableCosts_Oil"));
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        return variableCosts;
	    }

	    static float getFuelCosts(final Properties globalProperties, final String description) {
	        float variableCosts;
	        switch (description) {
	            case "lignite":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("FuelCosts_Lignite"));
	                break;
	            case "hard coal":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("FuelCosts_HardCoal"));
	                break;
	            case "natural gas (open cycle)":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("FuelCosts_NaturalGasOpenCycle"));
	                break;
	            case "natural gas (combined cycle)":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("FuelCosts_NaturalGasCombinedCycle"));
	                break;
	            case "oil":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("FuelCosts_Oil"));
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        return variableCosts;
	    }

	    static float getEmissionRate(final Properties globalProperties, final String description) {
	        float variableCosts;
	        switch (description) {
	            case "lignite":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("EmissionRate_Lignite"));
	                break;
	            case "hard coal":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("EmissionRate_HardCoal"));
	                break;
	            case "natural gas (open cycle)":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("EmissionRate_NaturalGasOpenCycle"));
	                break;
	            case "natural gas (combined cycle)":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("EmissionRate_NaturalGasCombinedCycle"));
	                break;
	            case "oil":
	                variableCosts = Float.parseFloat(globalProperties.getProperty("EmissionRate_Oil"));
	                break;
	            default:
	                throw new IllegalArgumentException("not supported type:" + description);

	        }
	        return variableCosts;
	    }
	}
