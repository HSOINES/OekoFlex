package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.EnergyOnlyMarketOperator;
import hsoines.oekoflex.util.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EnergyOnlyMarketOperatorImpl implements EnergyOnlyMarketOperator, OekoflexAgent {
    private static final Log log = LogFactory.getLog(EnergyOnlyMarketOperatorImpl.class);

    private final List<Demand> demands;
    private final List<Supply> supplies;
    private List<Demand> lastDemands;
    private List<Supply> lastSupplies;
    private final String name;
    private int clearedQuantity;
    private float clearedPrice;
    private float lastAssignmentRate;
    private AssignmentType lastAssignmentType;

    public EnergyOnlyMarketOperatorImpl(String name) {
        this.name = name;

        demands = new ArrayList<Demand>();
        supplies = new ArrayList<Supply>();
    }

    @Override
    public void addDemand(Demand demand) {
        demands.add(demand);
    }

    @Override
    public void addSupply(Supply supply) {
        this.supplies.add(supply);
    }

    @Override
    public void clearMarket() {
        demands.sort(new Demand.DescendingComparator());
        this.supplies.sort(new Supply.AscendingComparator());

        if (demands.size() < 1 || this.supplies.size() < 1) {
            throw new IllegalStateException("Sizes unsufficient! SupportSize: " + this.supplies.size() + ", DemandSize: " + demands.size());
        }
        Iterator<Demand> demandIterator = demands.iterator();
        Iterator<Supply> supplyIterator = this.supplies.iterator();
        int totalSupplyQuantity = 0;
        int totalDemandQuantity = 0;
        clearedQuantity = 0;
        int balance = 0;
        boolean moreSupplies = true;
        boolean moreDemands = true;
        Demand demand = null;
        Supply supply = null;
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
                    logString = "Supply added. Price: " + supply.getPrice() + ", Quantity: " + supply.getQuantity();
                } else {
                    moreSupplies = false;
                }
            } else if (balance < 0) {
                if (demandIterator.hasNext()) {
                    demand = demandIterator.next();
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
                    logString = "Demand added. Price: " + demand.getPrice() + ", Quantity: " + demand.getQuantity();
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
                    logString = "Demand added. Price: " + demand.getPrice() + ", Quantity: " + demand.getQuantity();
                } else {
                    moreDemands = false;
                }
            }
            log.debug("                                                      " + logString + ", " + "Balance: " + balance);
        }
        while (supply == null || (moreDemands && balance <= 0) || (moreSupplies && balance > 0)); //Demand + Supply immer quantity > 0!!!

        clearedQuantity = Math.min(totalDemandQuantity, totalSupplyQuantity);
        if (balance < 0) {
            lastAssignmentRate = (supply.getQuantity() + balance) / supply.getQuantity();
            lastAssignmentType = AssignmentType.PartialSupply;
        } else if (balance > 0) {
            lastAssignmentRate = (demand.getQuantity() - balance) / demand.getQuantity();
            lastAssignmentType = AssignmentType.PartialDemand;
        } else {
            lastAssignmentRate = 1;
            lastAssignmentType = AssignmentType.Full;
        }
        if (lastAssignmentRate > 1) {
            throw new IllegalStateException("lastAssignmentRate: " + lastAssignmentRate);
        }

        notifyExecutionRate();

        lastDemands = new ArrayList<>(demands);
        lastSupplies = new ArrayList<>(supplies);
        supplies.clear();
        demands.clear();
    }

    private void notifyExecutionRate() {
        StringBuilder logString = new StringBuilder();
        logString.append(getName()).append(",")
                .append(getLastClearedPrice()).append(",")
                .append(getLastAssignmentRate()).append(",")
                .append(getTotalClearedQuantity()).append(",");
        Date date = TimeUtilities.getCurrentDate();
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
                marketOperatorListener.notifyClearingDone(clearedPrice, assignmentRate, demand, date);
                logString.append(marketOperatorListener.getName()).append(",")
                        .append(assignmentRate).append(",")
                        .append(demand.getPrice()).append(",")
                        .append(demand.getQuantity()).append(",");
            }
        }
        for (Supply supply : this.supplies) {
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
                marketOperatorListener.notifyClearingDone(clearedPrice, assignmentRate, supply, date);
                logString.append(marketOperatorListener.getName()).append(",")
                        .append(assignmentRate).append(",")
                        .append(supply.getPrice()).append(",")
                        .append(supply.getQuantity()).append(",");
            }
            Log allInOneLine = LogFactory.getLog("ALL_IN_ONE_LINE");
            allInOneLine.info(logString.toString());
        }
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

    public List<Supply> getLastSupplies() {
        return lastSupplies;
    }

    public List<Demand> getLastDemands() {
        return lastDemands;
    }

    public AssignmentType getLastAssignmentType() {
        return lastAssignmentType;
    }
}
