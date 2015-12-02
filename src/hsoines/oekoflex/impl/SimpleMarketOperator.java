package hsoines.oekoflex.impl;

import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.bid.Bid;

import java.util.ArrayList;
import java.util.List;

public class SimpleMarketOperator implements MarketOperator{

	private final List<Bid> bids;

	SimpleMarketOperator(){
		bids = new ArrayList<Bid>();
	}
	
	@Override
	public void registerBid(Bid bid) {
		bids.add(bid);
	}	
	
}
