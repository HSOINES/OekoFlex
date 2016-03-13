package hsoines.oekoflex.priceforwardcurve;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.builder.CSVParameter;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.impl.FlexPowerplant;
import hsoines.oekoflex.energytrader.impl.TotalLoad;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.util.collections.IndexedIterable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jhense on 13.03.2016.
 */
public class PriceForwardCurveGenerator {
    private static final Log log = LogFactory.getLog(PriceForwardCurveGenerator.class);

    private Context<OekoflexAgent> context;
    private int ticksToRun;
    private TotalLoad renewables;
    private TotalLoad totalload;
    private final Set<FlexPowerplant> flexPowerplants;
    private final SpotMarketOperator spotMarketOperator;
    private CSVPrinter csvPrinter;

    public PriceForwardCurveGenerator(Context<OekoflexAgent> context, int ticksToRun, final File priceForwardOutDir) {
        this.context = context;
        this.ticksToRun = ticksToRun;
        flexPowerplants = new HashSet<>();
        IndexedIterable<OekoflexAgent> oekoflexAgents = context.getObjects(FlexPowerplant.class);
        for (OekoflexAgent oekoflexAgent : oekoflexAgents) {
            flexPowerplants.add((FlexPowerplant) oekoflexAgent);
        }
        oekoflexAgents = context.getObjects(TotalLoad.class);
        for (OekoflexAgent oekoflexAgent : oekoflexAgents) {
            if (oekoflexAgent.getName().equals("renewables")) {
                renewables = (TotalLoad) oekoflexAgent;
            }
            if (oekoflexAgent.getName().equals("totalload")) {
                totalload = (TotalLoad) oekoflexAgent;
            }
        }
        spotMarketOperator = (SpotMarketOperator) context.getObjects(SpotMarketOperator.class).iterator().next();

            if (!priceForwardOutDir.exists()) {
                if (!priceForwardOutDir.mkdirs()) {
                    throw new IllegalStateException("couldn't create directories.");
                }
            }
            File priceForwardFile = new File(priceForwardOutDir, "price-forward.csv");
            final Appendable out;
                try {
                    out = new FileWriter(priceForwardFile);
                csvPrinter = CSVParameter.getCSVFormat().withHeader("tick", "price").print(out);
                } catch (IOException e) {
                    log.error(e.getLocalizedMessage(), e);
                }
        }

    public void generate() {

        for (int tick = 0; tick < ticksToRun; tick++) {
            TimeUtil.nextTick();
            log.debug("Building pfc for tick: " + tick);
            for (FlexPowerplant flexPowerplant : flexPowerplants) {
                flexPowerplant.makeBidEOM(tick);
            }
            renewables.makeBidEOM();
            totalload.makeBidEOM();
            spotMarketOperator.clearMarket();
            spotMarketOperator.logPriceForward(tick, csvPrinter);
        }
        try {
            csvPrinter.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        TimeUtil.reset();
        for (FlexPowerplant flexPowerplant : flexPowerplants) {
            flexPowerplant.init();
        }
        renewables.init();
        totalload.init();
    }

   /* @Override
    public String getName() {
        return "PriceFowardGenerator";
    }*/
}
