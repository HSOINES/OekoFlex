package hsoines.oekoflex.impl;

import hsoines.oekoflex.MarketOperator;
import hsoines.oekoflex.ask.Ask;
import hsoines.oekoflex.bid.Bid;

import java.util.ArrayList;
import java.util.List;

public class SimpleMarketOperator implements MarketOperator{

	private final List<Bid> bids;
	private final List<Ask> asks;

	SimpleMarketOperator(){
		bids = new ArrayList<Bid>();
		asks = new ArrayList<Ask>();
	}
	
	@Override
	public void registerBid(Bid bid) {
		bids.add(bid);
	}

	@Override
	public void registerAsk(Ask ask) {
		asks.add(ask);
	}		
}
