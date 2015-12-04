package hsoines.oekoflex.impl;

import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.ask.Ask;
import hsoines.oekoflex.bid.Bid;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.engine.schedule.ScheduledMethod;

public class SimpleMarketOperator implements MarketOperator{

	private final List<Bid> bids;
	private final List<Ask> asks;

	private int totalAskAmount = 0;
	private int totalBidAmount = 0;
	private float bidCosts = 0;
	private float askCosts = 0;

	SimpleMarketOperator(){
		bids = new ArrayList<Bid>();
		asks = new ArrayList<Ask>();
	}
	
	@Override
	public void addBid(Bid bid) {
		bids.add(bid);
	}

	@Override
	public void addAsk(Ask ask) {
		asks.add(ask);
	}	
	
    @ScheduledMethod(start = 1, interval = 1, priority = 100)
    public void clearMarket(){
		bids.sort(new Bid.AscendingComparator());
		asks.sort(new Ask.AscendingComparator());

		for (Bid bid : bids) {
			totalBidAmount += bid.getAmount();
			bidCosts += bid.getPrice() * bid.getAmount();
		}

		for (Ask ask : asks) {
			totalAskAmount += ask.getAmount();
			float lastAskCosts = ask.getPrice() * ask.getAmount();
			askCosts += lastAskCosts;
			if (totalAskAmount > totalBidAmount){
				float diff = totalAskAmount - totalBidAmount;
				float percentageOveruse = diff / ask.getAmount();
				totalAskAmount -= percentageOveruse * ask.getAmount();
				askCosts -= percentageOveruse * ask.getAmount();
				break;
			}
		}

		asks.clear();
		bids.clear();
    }

	public int getTotalAskAmount() {
		return totalAskAmount;
	}

	public int getTotalBidAmount() {
		return totalBidAmount;
	}

	public float getBidCosts() {
		return bidCosts;
	}

	public float getAskCosts() {
		return askCosts;
	}


}
