package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

import java.util.Date;
import java.util.List;

public class ParametrizableEnergyProducer implements EOMTrader {

    public static final int INITIALCAPACITY = 200;
    private final String name;
    private SpotMarketOperator marketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float bidPrice;
    private final int supplyCapacity;
    private final int supplyDelay;
    private int bidQuantity;
    private TradeRegistry tradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, INITIALCAPACITY, 1000);

    public ParametrizableEnergyProducer(String name) {
        this.name = name;
        Parameters p = RunEnvironment.getInstance().getParameters();

        supplyCapacity = (int) p.getValue("supplyCapacityAgent1");
        supplyDelay = (int) p.getValue("supplyDelayAgent1");

        bidPrice = (float)Math.random() * 500;
        bidQuantity = 200;
    }

    public void makeBidEOM() {
        if (lastAssignmentRate > .5) {
            bidPrice += 10;
            bidQuantity += supplyDelay;
            if (bidQuantity > supplyCapacity) {
                bidQuantity = supplyCapacity;
            }
        } else {
            bidPrice -= 10;            
            bidQuantity -= supplyDelay;
            if (bidQuantity < 20) {
            	bidQuantity = 20;
            }
        }
        marketOperator.addSupply(new EnergySupply(bidPrice, Math.max(bidQuantity, INITIALCAPACITY), this));
    }

    @Override
    public void init() {

    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.marketOperator = spotMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return tradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }


    @Override
    public String getName() {
        return name;
    }
}
