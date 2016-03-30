package hsoines.oekoflex.builder;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.traderfactories.FlexPowerplantFactory;
import hsoines.oekoflex.builder.traderfactories.StorageFactory;
import hsoines.oekoflex.builder.traderfactories.TotalLoadFactory;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.impl.BalancingMarketOperatorImpl;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveGenerator;
import hsoines.oekoflex.priceforwardcurve.impl.PriceForwardCurveImpl;
import hsoines.oekoflex.summary.impl.EnergyTraderTypeLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Properties;

public class OekoFlexContextBuilder implements ContextBuilder<OekoflexAgent> {
    private static final Log log = LogFactory.getLog(OekoFlexContextBuilder.class);

    static final Locale defaultlocale = Locale.GERMAN;
    public static NumberFormat defaultNumberFormat;

    static {
        Locale.setDefault(OekoFlexContextBuilder.defaultlocale);
        OekoFlexContextBuilder.defaultNumberFormat = DecimalFormat.getNumberInstance();
    }

    @Override
    public Context build(Context<OekoflexAgent> context) {
    	log.info("locale: " + Locale.getDefault().getDisplayName());
        context.setId("OekoFlex");

        Parameters p = RunEnvironment.getInstance().getParameters();
        RunEnvironment re = RunEnvironment.getInstance();
        int daysToRun = (int) p.getValue("daysToRun");

        re.endAt(daysToRun * 96);

        boolean loggingActivated = (boolean) p.getValue("loggingActivated");

        String scenario = (String) p.getValue("scenario");
        String logDirName = "run/summary-logs/" + scenario;
        String configDirName = "run-config/" + scenario;
        String priceForwardOutDirName = configDirName + "/price-forward/";

        File configDir = new File(configDirName);
        if (!configDir.exists()) {
            log.error("Configuration directory is not existing: " + scenario);
            re.endRun();
        }

        try {
            File priceForwardOutDir = new File(priceForwardOutDirName);
            if (priceForwardOutDir.exists()) {
                log.info("removing old price forward");
                FileUtils.deleteDirectory(priceForwardOutDir);
                priceForwardOutDir.mkdir();
            }

            if (loggingActivated) {
                //remove log-dirs
                File logDir = new File(logDirName);
                FileUtils.deleteDirectory(logDir);
                EnergyTraderTypeLogger energyTraderTypeLogger = new EnergyTraderTypeLogger(context, logDirName);
                context.add(energyTraderTypeLogger);
            }

            Properties globalProperties = loadProperties(configDir);
            int positiveDemandREM = Integer.parseInt((String) globalProperties.get("positiveDemandREM"));
            int negativeDemandREM = Integer.parseInt((String) globalProperties.get("negativeDemandREM"));
            SpotMarketOperatorImpl spotMarketOperator = new SpotMarketOperatorImpl("EOM_Operator", logDirName, loggingActivated);
            BalancingMarketOperator balancingMarketOperator = new BalancingMarketOperatorImpl("BalancingMarketOperator", loggingActivated, logDirName, positiveDemandREM, negativeDemandREM);
            context.add(spotMarketOperator);
            context.add(balancingMarketOperator);

            // PriceForwardCurve
            File priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");
            PriceForwardCurveGenerator priceForwardCurveGenerator = new PriceForwardCurveGenerator(configDir, daysToRun * 96, priceForwardFile);
            PriceForwardCurve priceForwardCurve = new PriceForwardCurveImpl(priceForwardFile);

            //Consumer
            TotalLoadFactory.build(configDir, context, spotMarketOperator);

            //Producer
            FlexPowerplantFactory.build(configDir, context, spotMarketOperator, balancingMarketOperator, priceForwardCurve);
            StorageFactory.build(configDir, context, spotMarketOperator, balancingMarketOperator, priceForwardCurve);

            //build pfc
            priceForwardCurveGenerator.generate();
            priceForwardCurve.readData();
        } catch (IOException e) {
            log.error(e.toString(), e);
            re.endRun();
            System.exit(-1);
        } catch (ParseException e) {
            log.error(e.toString(), e);
        }

        return context;
    }

    Properties loadProperties(final File configDir) throws IOException {
        Properties globalProperties = new Properties();
        File globalPropertiesFile = new File(configDir, "Global.properties");
        FileInputStream is = new FileInputStream(globalPropertiesFile);
        globalProperties.load(is);
        return globalProperties;
    }

}
