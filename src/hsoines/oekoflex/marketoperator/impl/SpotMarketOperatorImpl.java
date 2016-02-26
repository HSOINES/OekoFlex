package hsoines.oekoflex.marketoperator.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.MarketOperatorListener;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
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

public class SpotMarketOperatorImpl implements SpotMarketOperator {
    private static final Log log = LogFactory.getLog(SpotMarketOperatorImpl.class);

    private final List<EnergyDemand> energyDemands;
    private final List<EnergySupply> supplies;
    private final LoggerFile logger;
    private List<EnergyDemand> lastEnergyDemands;
    private List<EnergySupply> lastSupplies;
    private final String name;
    private int clearedQuantity;
    private float clearedPrice;
    private float lastAssignmentRate;
    private AssignmentType lastAssignmentType;

    public SpotMarketOperatorImpl(String name, final String logDirName) throws IOException {
        this.name = name;

        energyDemands = new ArrayList<>();
        supplies = new ArrayList<>();

        logger = new LoggerFile(this.getClass().getSimpleName(), logDirName);
        logger.log("tick;traderType;traderName;bidType;offeredPrice;clearedPrice;offeredQuantity;assignedQuantity");
    }

    @Override
    public void addDemand(EnergyDemand supply) {
        if (supply.getQuantity() < 0.001) {
            return;
        }
        energyDemands.add(supply);
    }

    @Override
    public void addSupply(EnergySupply supply) {
        if (supply.getQuantity() < 0.001) {
            return;
        }
        this.supplies.add(supply);
    }

    @Override
    public void clearMarket() {
        energyDemands.sort(new EnergyDemand.DescendingComparator());
        this.supplies.sort(new PowerPositive.AscendingComparator());

        if (energyDemands.size() < 1 || this.supplies.size() < 1) {
            throw new IllegalStateException("Sizes unsufficient! SupportSize: "
                    + this.supplies.size() + ", DemandSize: " + energyDemands.size());
        }
        Iterator<EnergyDemand> demandIterator = energyDemands.iterator();
        Iterator<EnergySupply> supplyIterator = this.supplies.iterator();
        
        //increments until prices match
        int totalSupplyQuantity = 0;
        int totalDemandQuantity = 0;
        
        clearedQuantity = 0;
        //Indicates next element:
        // balance < 0 -> demands are fetched
        // balance > 0 -> supplies are fetched
        int balance = 0;
        //Stops clearing if false
        boolean moreSupplies = true;
        boolean moreDemands = true;

        //current bids to clear
        EnergyDemand energyDemand = null;
        EnergySupply energySupply = null;
        do {
            String logString = "";
            if (balance > 0) {
                if (supplyIterator.hasNext()) {
                    energySupply = supplyIterator.next();
                    if (energyDemand.getPrice() <= energySupply.getPrice()) {
                        break;
                    }
                    balance -= energySupply.getQuantity();
                    if (balance < 0) {
                        clearedPrice = energySupply.getPrice();
                    } else {
                        clearedPrice = energyDemand.getPrice();
                    }
                    totalSupplyQuantity += energySupply.getQuantity();
                    logString = "Supply assigned. Price: " + energySupply.getPrice() + ", Quantity: " + energySupply.getQuantity();
                } else {
                    moreSupplies = false;
                }
            } else if (balance < 0) {
                if (demandIterator.hasNext()) {
                    energyDemand = demandIterator.next();
                    if (energyDemand.getQuantity() < 0) {
                        break;
                    }
                    if (energyDemand.getPrice() <= energySupply.getPrice()) {
                        break;
                    }
                    balance += energyDemand.getQuantity();
                    if (balance > 0) {
                        clearedPrice = energyDemand.getPrice();
                    } else {
                        clearedPrice = energySupply.getPrice();
                    }
                    totalDemandQuantity += energyDemand.getQuantity();
                    logString = "Demand assigned. Price: " + energyDemand.getPrice() + ", Quantity: " + energyDemand.getQuantity();
                } else {
                    moreDemands = false;
                }
            } else {
                if (totalDemandQuantity != totalSupplyQuantity) {
                    //todo: right?!    throw new IllegalStateException("mustn't differ");
                }
                if (demandIterator.hasNext()) {
                    energyDemand = demandIterator.next();
                    if (!(energySupply == null) && energyDemand.getPrice() <= energySupply.getPrice()) {
                        break;
                    }
                    clearedPrice = (energySupply != null) ? (energyDemand.getPrice() + energySupply.getPrice()) / 2 : energyDemand.getPrice();
                    balance += energyDemand.getQuantity();
                    totalDemandQuantity += energyDemand.getQuantity();
                    logString = "Demand assigned. Price: " + energyDemand.getPrice() + ", Quantity: " + energyDemand.getQuantity();
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
        while (energySupply == null || (moreDemands && balance <= 0) || (moreSupplies && balance > 0)); //Demand + Supply immer quantity > 0!!!

        clearedQuantity = Math.min(totalDemandQuantity, totalSupplyQuantity);
        if (balance < 0) {
            lastAssignmentRate = ((float) energySupply.getQuantity() + balance) / energySupply.getQuantity();
            lastAssignmentType = AssignmentType.PartialSupply;
        } else if (balance > 0) {
            lastAssignmentRate = ((float) energyDemand.getQuantity() - balance) / energyDemand.getQuantity();
            lastAssignmentType = AssignmentType.PartialDemand;
        } else {
            lastAssignmentRate = 1;
            lastAssignmentType = AssignmentType.Full;
        }
        if (lastAssignmentRate > 1) {
            throw new IllegalStateException("lastAssignmentRate: " + lastAssignmentRate);
        }

        notifyAssignmentRate();

        lastEnergyDemands = new ArrayList<>(energyDemands);
        lastSupplies = new ArrayList<>(supplies);
        supplies.clear();
        energyDemands.clear();
    }

    private void notifyAssignmentRate() {
        StringBuilder logString = new StringBuilder();
        logString.append(getName()).append(",")
                .append(getLastClearedPrice()).append(",")
                .append(getLastAssignmentRate()).append(",")
                .append(getTotalClearedQuantity()).append(",");
        Date date = TimeUtil.getCurrentDate();
        for (EnergyDemand energyDemand : energyDemands) {
            MarketOperatorListener marketOperatorListener = energyDemand.getMarketOperatorListener();
            if (marketOperatorListener != null) {
                float assignmentRate;
                if (energyDemand.getPrice() > clearedPrice) {
                    assignmentRate = 1f;
                } else if (energyDemand.getPrice() == clearedPrice) {
                    assignmentRate = lastAssignmentRate;
                } else {
                    assignmentRate = 0;
                }
                marketOperatorListener.notifyClearingDone(date, Market.SPOT_MARKET, energyDemand, clearedPrice, assignmentRate);
                logSummary(energyDemand, assignmentRate);
                logString.append(assignmentRate).append(",")
                        .append(energyDemand.getPrice()).append(",")
                        .append(energyDemand.getQuantity()).append(",");
            }
        }
        for (EnergySupply supply : this.supplies) {
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
                marketOperatorListener.notifyClearingDone(date, Market.SPOT_MARKET, supply, clearedPrice, assignmentRate);
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
                        + NumberFormatUtil.format(bid.getQuantity()) + ";"
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

    public List<EnergySupply> getLastSupplies() {
        return lastSupplies;
    }

    public List<EnergyDemand> getLastEnergyDemands() {
        return lastEnergyDemands;
    }

    public AssignmentType getLastAssignmentType() {
        return lastAssignmentType;
    }
}
