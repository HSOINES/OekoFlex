package hsoines.oekoflex.summary;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.BidType;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.MarketTrader;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.util.NumberFormatUtil;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.util.collections.IndexedIterable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 23:18
 */
public final class EnergyTraderTypeLogger implements OekoflexAgent {
    private static final Log log = LogFactory.getLog(EnergyTraderTypeLogger.class);
    private final Context<OekoflexAgent> context;
    private final String scenarioLogDir;

    private final Map<Class, LoggerFile> loggerFiles = new HashMap<>();

    public EnergyTraderTypeLogger(final Context<OekoflexAgent> context, String scenarioLogDir) {
        this.context = context;
        this.scenarioLogDir = scenarioLogDir + "/Trader";

        File logDir = new File(scenarioLogDir);
        try {
            FileUtils.deleteDirectory(logDir);
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
    }

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = 1, priority = SequenceDefinition.ReportingPriority)
    public void execute() {
        IndexedIterable<OekoflexAgent> oekoflexAgents = context.getObjects(MarketTrader.class);
        for (OekoflexAgent oekoflexAgent : oekoflexAgents) {
            MarketTrader marketTrader = (MarketTrader) oekoflexAgent;
            addIfNecessary(marketTrader);
            LoggerFile loggerFile = loggerFiles.get(marketTrader.getClass());
            logCommonData(marketTrader, loggerFile);
        }
    }

    public void addIfNecessary(final MarketTrader marketTrader) {
        if (!loggerFiles.keySet().contains(marketTrader.getClass())) {
            LoggerFile loggerFile = null;
            try {
                String header = buildHeaderString(marketTrader);
                loggerFile = new LoggerFile(marketTrader.getClass().getSimpleName() + "s", scenarioLogDir);
                loggerFiles.put(marketTrader.getClass(), loggerFile);
                loggerFile.log(header);
            } catch (IOException e) {
                log.error(e.toString(), e);
            }
        }
    }

    String buildHeaderString(final MarketTrader marketTrader) {
        final StringBuilder header = new StringBuilder();
        header.append("tick;ClassName;InstanceName;Description;Market;BidType;PriceOffered;PriceCleared;QuantityOffered;QuantityAssigned;Capacity;MustRunViolation");

        return header.toString();
    }

    void logCommonData(final MarketTrader marketTrader, final LoggerFile loggerFile) {
        List<TradeRegistryImpl.EnergyTradeElement> currentAssignments = marketTrader.getCurrentAssignments();
        if (currentAssignments == null) {
            return;
        }
        for (TradeRegistryImpl.EnergyTradeElement currentAssignment : currentAssignments) {
            float capacity = currentAssignment.getCapacity();
            int assignedQuantity = (int) (currentAssignment.getRate() * currentAssignment.getOfferedQuantity());
            boolean mustRunViolation = (currentAssignment.getBidType().equals(BidType.ENERGY_SUPPLY_MUSTRUN) && currentAssignment.getRate() - 1 > .01);
            loggerFile.log(TimeUtil.getTick(TimeUtil.getCurrentDate()) + ";"
                    + marketTrader.getClass().getSimpleName() + ";"
                    + marketTrader.getName() + ";"
                    + marketTrader.getDescription() + ";"
                    + currentAssignment.getMarket() + ";"
                    + currentAssignment.getBidType() + ";"
                    + NumberFormatUtil.format(currentAssignment.getOfferedPrice()) + ";"
                    + NumberFormatUtil.format(currentAssignment.getAssignedPrice()) + ";"
                    + NumberFormatUtil.format(currentAssignment.getOfferedQuantity()) + ";"
                    + NumberFormatUtil.format(assignedQuantity) + ";"
                    + NumberFormatUtil.format(capacity) + ";"
                    + (mustRunViolation ? "X" : ""))
            ;
        }
    }

    @Override
    public String getName() {
        return "EnergyTraderTypeLogger";
    }
}
