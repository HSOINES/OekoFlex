package hsoines.oekoflex.priceforwardcurve.impl;

import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.builder.OekoFlexContextBuilder;
import hsoines.oekoflex.builder.traderfactories.FlexPowerplantFactory;
import hsoines.oekoflex.builder.traderfactories.TotalLoadFactory;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant;
import hsoines.oekoflex.energytrader.impl.TotalLoad;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by jhense on 13.03.2016.
 */
public class PriceForwardCurveGenerator {
    private static final Log log = LogFactory.getLog(PriceForwardCurveGenerator.class);

    private int ticksToRun;
    private final long prerunTicks;
    private TotalLoad renewables;
    private TotalLoad totalload;
    private final Set<FlexPowerplant> flexPowerplants;
    private final SpotMarketOperator spotMarketOperator;
    private CSVPrinter csvPrinter;

    public PriceForwardCurveGenerator(File configDir, int ticksToRun, final File priceForwardFile, long prerunTicks) throws IOException {
        this.ticksToRun = ticksToRun;
        this.prerunTicks = prerunTicks;

        spotMarketOperator = new SpotMarketOperatorImpl("pfc-spotmarketoperator", "", false);
        flexPowerplants = FlexPowerplantFactory.build(configDir);
        for (FlexPowerplant flexPowerplant : flexPowerplants) {
            flexPowerplant.setSpotMarketOperator(spotMarketOperator);
        }

        Set<TotalLoad> totalLoads = TotalLoadFactory.build(configDir, prerunTicks);
        for (TotalLoad totalLoad : totalLoads) {
            totalLoad.setSpotMarketOperator(spotMarketOperator);
            if (totalLoad.getName().equals("renewables")) {
                renewables = totalLoad;
            }
            if (totalLoad.getName().equals("totalload")) {
                totalload = totalLoad;
            }
        }
        if (!priceForwardFile.getParentFile().exists()) {
            if (!priceForwardFile.getParentFile().mkdirs()) {
                throw new IllegalStateException("couldn't create directories.");
            }
        }
        final Appendable out;
        try {
            out = new FileWriter(priceForwardFile);
            csvPrinter = CSVParameter.getCSVFormat().withHeader("tick", "price").print(out);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    public void generate() {
        TimeUtil.startAt(-prerunTicks);
        for (long tick = -prerunTicks; tick < ticksToRun; tick++) {
            log.debug("Building pfc for tick: " + tick);
            for (FlexPowerplant flexPowerplant : flexPowerplants) {
                flexPowerplant.makeBidEOM(tick);
            }
            renewables.makeBidEOM(tick);
            totalload.makeBidEOM(tick);
            spotMarketOperator.clearMarket();
            logPriceForward(tick, csvPrinter);
            TimeUtil.nextTick();
        }
        TimeUtil.reset();

        try {
            csvPrinter.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void logPriceForward(long tick, CSVPrinter csvPrinter) {
        try {
            csvPrinter.printRecord(tick, OekoFlexContextBuilder.defaultNumberFormat.format(spotMarketOperator.getLastClearedPrice()));
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
    }

}
