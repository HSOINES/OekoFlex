package hsoines.oekoflex;

import hsoines.oekoflex.ask.Ask;
import hsoines.oekoflex.bid.Bid;

public interface MarketOperator {
	public void registerBid(Bid bid);
	public void registerAsk(Ask ask);
}
