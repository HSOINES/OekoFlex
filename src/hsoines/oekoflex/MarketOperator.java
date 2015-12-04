package hsoines.oekoflex;

import hsoines.oekoflex.ask.Ask;
import hsoines.oekoflex.bid.Bid;

public interface MarketOperator {
	public void addBid(Bid bid);
	public void addAsk(Ask ask);
}
