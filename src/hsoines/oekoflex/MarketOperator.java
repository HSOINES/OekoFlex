package hsoines.oekoflex;

import hsoines.oekoflex.supply.Supply;
import hsoines.oekoflex.demand.Demand;

public interface MarketOperator {
	public void addDemand(Demand demand);
	public void addSupply(Supply supply);
}
