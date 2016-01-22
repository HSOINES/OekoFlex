package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.PositiveSupply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.summary.LoggerFile;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.NumberFormatUtil;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EOMOperatorImpl implements EOMOperator {
    private static final Log log = LogFactory.getLog(EOMOperatorImpl.class);

    private final List<Demand> demands;
    private final List<PositiveSupply> supplies;
    private final LoggerFile logger;
    private List<Demand> lastDemands;
    private List<PositiveSupply> lastSupplies;
    private final String name;
    private int clearedQuantity;
    private float clearedPrice;
    private float lastAssignmentRate;
    private AssignmentType lastAssignmentType;

    public EOMOperatorImpl(String name, final String logDirName) throws IOException {
        this.name = name;

        demands = new ArrayList<>();
        supplies = new ArrayList<>();

        logger = new LoggerFile(this.getClass().getSimpleName(), logDirName);
        logger.log("tick;traderType;traderName;bidType;offeredPrice;clearedPrice;offeredQuantity;assignedQuantity");
    }

    @Override
    public void addDemand(Demand demand) {
        demands.add(demand);
    }

    @Override
    public void addSupply(PositiveSupply supply) {
        this.supplies.add(supply);
    }

    @Override
    public void clearMarket() {
        demands.sort(new Demand.DescendingComparator());
        this.supplies.sort(new PositiveSupply.AscendingComparator());

        if (demands.size() < 1 || this.supplies.size() < 1) {
            throw new IllegalStateException("Sizes unsufficient! SupportSize: "
                    + this.supplies.size() + ", DemandSize: " + demands.size());
        }
        Iterator<Demand> demandIterator = demands.iterator();
        Iterator<PositiveSupply> supplyIterator = this.supplies.iterator();
        int totalSupplyQuantity = 0;
        int totalDemandQuantity = 0;
        clearedQuantity = 0;
        int balance = 0;
        boolean moreSupplies = true;
        boolean moreDemands = true;
        Demand demand = null;
        PositiveSupply supply = null;
        do {
            String logString = "";
            if (balance > 0) {
                if (supplyIterator.hasNext()) {
                    supply = supplyIterator.next();
                    if (demand.getPrice() <= supply.getPrice()) {
                        break;
                    }
                    balance -= supply.getQuantity();
                    if (balance < 0) {
                        clearedPrice = supply.getPrice();
                    } else {
                        clearedPrice = demand.getPrice();
                    }
                    totalSupplyQuantity += supply.getQuantity();
                    logString = "Supply assigned. Price: " + supply.getPrice() + ", Quantity: " + supply.getQuantity();
                } else {
                    moreSupplies = false;
                }
            } else if (balance < 0) {
                if (demandIterator.hasNext()) {
                    demand = demandIterator.next();
                    if (demand.getQuantity() < 0) {
                        break;
                    }
                    if (demand.getPrice() <= supply.getPrice()) {
                        break;
                    }
                    balance += demand.getQuantity();
                    if (balance > 0) {
                        clearedPrice = demand.getPrice();
                    } else {
                        clearedPrice = supply.getPrice();
                    }
                    totalDemandQuantity += demand.getQuantity();
                    logString = "Demand assigned. Price: " + demand.getPrice() + ", Quantity: " + demand.getQuantity();
                } else {
                    moreDemands = false;
                }
            } else {
                if (totalDemandQuantity != totalSupplyQuantity) {
                    throw new IllegalStateException("mustn't differ");
                }
                if (demandIterator.hasNext()) {
                    demand = demandIterator.next();
                    if (!(supply == null) && demand.getPrice() <= supply.getPrice()) {
                        break;
                    }
                    clearedPrice = (supply != null) ? (demand.getPrice() + supply.getPrice()) / 2 : demand.getPrice();
                    balance += demand.getQuantity();
                    totalDemandQuantity += demand.getQuantity();
                    logString = "Demand assigned. Price: " + demand.getPrice() + ", Quantity: " + demand.getQuantity();
                } else {
                    moreDemands = false;
                    if (balance == 0) {
                        log.warn("market stops. balance is 0, no more demands.");
                        break;
                    }
                }
            }
            log.debug("                                                      " + logString + ", " + "Balance: " + balance);
        }
        while (supply == null || (moreDemands && balance <= 0) || (moreSupplies && balance > 0)); //Demand + Supply immer quantity > 0!!!

        clearedQuantity = Math.min(totalDemandQuantity, totalSupplyQuantity);
        if (balance < 0) {
            lastAssignmentRate = ((float) supply.getQuantity() + balance) / supply.getQuantity();
            lastAssignmentType = AssignmentType.PartialSupply;
        } else if (balance > 0) {
            lastAssignmentRate = ((float) demand.getQuantity() - balance) / demand.getQuantity();
            lastAssignmentType = AssignmentType.PartialDemand;
        } else {
            lastAssignmentRate = 1;
            lastAssignmentType = AssignmentType.Full;
        }
        if (lastAssignmentRate > 1) {
            throw new IllegalStateException("lastAssignmentRate: " + lastAssignmentRate);
        }

        notifyAssignmentRate();

        lastDemands = new ArrayList<>(demands);
        lastSupplies = new ArrayList<>(supplies);
        supplies.clear();
        demands.clear();
    }

    private void notifyAssignmentRate() {
        StringBuilder logString = new StringBuilder();
        logString.append(getName()).append(",")
                .append(getLastClearedPrice()).append(",")
                .append(getLastAssignmentRate()).append(",")
                .append(getTotalClearedQuantity()).append(",");
        Date date = TimeUtil.getCurrentDate();
        for (Demand demand : demands) {
            MarketOperatorListener marketOperatorListener = demand.getMarketOperatorListener();
            if (marketOperatorListener != null) {
                float assignmentRate;
                if (demand.getPrice() > clearedPrice) {
                    assignmentRate = 1f;
                } else if (demand.getPrice() == clearedPrice) {
                    assignmentRate = lastAssignmentRate;
                } else {
                    assignmentRate = 0;
                }
                marketOperatorListener.notifyClearingDone(date, Market.EOM_MARKET, demand, clearedPrice, assignmentRate);
                logSummary(demand, assignmentRate);
                logString.append(assignmentRate).append(",")
                        .append(demand.getPrice()).append(",")
                        .append(demand.getQuantity()).append(",");
            }
        }
        for (PositiveSupply supply : this.supplies) {
            MarketOperatorListener marketOperatorListener = supply.getMarketOperatorListener();
            if (marketOperatorListener != null) {
                float assignmentRate = 0;
                if (supply.getPrice() < clearedPrice) {
                    assignmentRate = 1;
                } else if (supply.getPrice() == clearedPrice) {
                    assignmentRate = lastAssignmentRate;
                } else {
                    assignmentRate = 0;
                }
                marketOperatorListener.notifyClearingDone(date, Market.EOM_MARKET, supply, clearedPrice, assignmentRate);
                logSummary(supply, assignmentRate);
                logString.append(assignmentRate).append(",")
                        .append(supply.getPrice()).append(",")
                        .append(supply.getQuantity()).append(",");
            }
            Log allInOneLine = LogFactory.getLog("ALL_IN_ONE_LINE");
            allInOneLine.info(logString.toString());
        }
    }

    private void logSummary(final Bid bid, final float assignmentRate) {
        logger.log(TimeUtil.getCurrentTick() + ";"
                        + bid.getMarketOperatorListener().getClass().getSimpleName() + ";"
                        + bid.getMarketOperatorListener().getName() + ";"
                        + bid.getClass().getSimpleName() + ";"
                        + NumberFormatUtil.format(bid.getPrice()) + ";"
                        + NumberFormatUtil.format(clearedPrice) + ";"
                        + bid.getQuantity() + ";"
                        + NumberFormatUtil.format(assignmentRate * bid.getQuantity()) + ";"
        );
    }

    @Override
    public int getTotalClearedQuantity() {
        return clearedQuantity;
    }

    @Override
    public float getLastClearedPrice() {
        return clearedPrice;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    public float getPrice() {  //dummy, da chart nicht sauber laeuft
        return -1;
    }


    @Override
    public String getName() {
        return name;
    }

    public List<PositiveSupply> getLastSupplies() {
        return lastSupplies;
    }

    public List<Demand> getLastDemands() {
        return lastDemands;
    }

    public AssignmentType getLastAssignmentType() {
        return lastAssignmentType;
    }
}
