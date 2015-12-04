package hsoines.oekoflex.impl;

import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.MarketOperatorListener;
import hsoines.oekoflex.ask.Support;
import hsoines.oekoflex.bid.Demand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class SimpleMarketOperator implements MarketOperator {

    private final List<Demand> demands;
    private final List<Support> supports;
    private int clearedQuantity;
    private float clearedPrice;
    private float lastAssignmentRate;

    SimpleMarketOperator() {
        demands = new ArrayList<Demand>();
        supports = new ArrayList<Support>();
    }

    @Override
    public void addDemand(Demand demand) {
        demands.add(demand);
    }

    @Override
    public void addSupport(Support support) {
        supports.add(support);
    }

    @ScheduledMethod(start = 1, interval = 1, priority = 1)
    public void clearMarket() {
        demands.sort(new Demand.DescendingComparator());
        supports.sort(new Support.AscendingComparator());

        if (demands.size() < 1 || supports.size() < 1) {
            throw new IllegalStateException("Sizes unsufficient! SupportSize: " + supports.size() + ", DemandSize: " + demands.size());
        }
        Iterator<Demand> demandIterator = demands.iterator();
        Iterator<Support> supportIterator = supports.iterator();
        int totalSupportQuantity = 0;
        int totalDemandQuantity = 0;
        int balance = 0;
        Support support = supportIterator.next();
        Demand demand = demandIterator.next();
        while (true) {
            if (demand.getPrice() >= support.getPrice()) {
                if (balance > 0) {
                    float supportQuantity = support.getQuantity();
                    balance -= supportQuantity;
                    totalSupportQuantity += supportQuantity;
                    if (supportIterator.hasNext()) {
                        support = supportIterator.next();
                    } else {
                        throw new IllegalStateException("not enought supports!");
                    }
                } else {
                    float demandQuantity = demand.getQuantity();
                    balance += demandQuantity;
                    totalDemandQuantity += demandQuantity;
                    if (demandIterator.hasNext()) {
                        demand = demandIterator.next();
                    } else {
                        throw new IllegalStateException("not enought demands!");
                    }
                }
            } else {
                if (balance > 0) {
                    clearedPrice = support.getPrice();
                    clearedQuantity = totalDemandQuantity;
                    lastAssignmentRate = (float) balance / support.getQuantity();
                } else if (balance < 0){
                    clearedPrice = demand.getPrice();
                    clearedQuantity = totalSupportQuantity;
                    lastAssignmentRate = (float) -balance / demand.getQuantity();
                } else {
                   /// unklar: demand-price oder support price? oder mittelwert
                    clearedPrice = (support.getPrice() + demand.getPrice()) / 2;
                    lastAssignmentRate = 1;
                    if (totalDemandQuantity != totalSupportQuantity){
                        throw new IllegalStateException("must be equal: " + totalDemandQuantity);
                    }
                    clearedQuantity = totalDemandQuantity;
                }
                break;
            }
        }

        notifyExecutionRate();

        supports.clear();
        demands.clear();
    }

    private void notifyExecutionRate() {
        for (Demand demand : demands) {
            MarketOperatorListener marketOperatorListener = demand.getMarketOperatorListener();
            if (marketOperatorListener != null) {
                if (demand.getPrice() > clearedPrice) {
                    marketOperatorListener.notifyAssignmentRate(1f, demand);
                } else if (demand.getPrice() == clearedPrice) {
                    marketOperatorListener.notifyAssignmentRate(lastAssignmentRate, demand);
                } else {
                    marketOperatorListener.notifyAssignmentRate(0, demand);
                }
            }
        }
        for (Support support : supports) {
            MarketOperatorListener marketOperatorListener = support.getMarketOperatorListener();
            if (marketOperatorListener != null) {
                if (support.getPrice() < clearedPrice) {
                    marketOperatorListener.notifyAssignmentRate(1f, support);
                } else if (support.getPrice() == clearedPrice) {
                    marketOperatorListener.notifyAssignmentRate(lastAssignmentRate, support);
                } else {
                    marketOperatorListener.notifyAssignmentRate(0, support);
                }
            }
        }
    }

    public int getTotalSupportQuantity() {
        return clearedQuantity;
    }

    public float getClearedPrice() {
        return clearedPrice;
    }

    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

}
