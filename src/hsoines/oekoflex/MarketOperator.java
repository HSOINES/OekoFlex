package hsoines.oekoflex;

import hsoines.oekoflex.ask.Support;
import hsoines.oekoflex.bid.Demand;

public interface MarketOperator {
	public void addDemand(Demand demand);
	public void addSupport(Support support);
}
