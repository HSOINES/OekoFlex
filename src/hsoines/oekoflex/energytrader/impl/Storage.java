package hsoines.oekoflex.energytrader.impl;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.domain.SequenceDefinition;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.priceforwardcurve.PriceForwardCurve;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import repast.simphony.engine.schedule.ScheduledMethod;

import java.util.Date;
import java.util.List;

/**
 * User: jh
 * Date: 18/01/16
 * Time: 16:14
 *
 *
 */
public final class Storage implements EOMTrader, BalancingMarketTrader {
    private static final Log log = LogFactory.getLog(Storage.class);

    private final String name;
    private final String description;
    private final float marginalCosts;
    private final float shutdownCosts;
    private final int capacity;
    private final float socMax;
    private final float socMin;
    private final int chargePower;
    private final int dischargePower;
    private final PriceForwardCurve priceForwardCurve;
    private int soc;
    private SpotMarketOperator eomMarketOperator;
    private TradeRegistry batteryTradeRegistry;
    private float lastAssignmentRate;
    private float lastClearedPrice;
    private BalancingMarketOperator balancingMarketOperator;


    public Storage(final String name, final String description,
                   final float marginalCosts, final float shutdownCosts,
                   final int capacity, final float socMax, final float socMin,
                   final int chargePower, final int dischargePower,
                   PriceForwardCurve priceForwardCurve) {
        this.name = name;
        this.description = description;
        this.marginalCosts = marginalCosts;
        this.shutdownCosts = shutdownCosts;
        this.capacity = capacity;
        this.socMax = socMax;
        this.socMin = socMin;
        this.chargePower = chargePower;
        this.dischargePower = dischargePower;
        this.priceForwardCurve = priceForwardCurve;
        init();
    }

    public void init() {
        batteryTradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, capacity, 1000);
        soc = 0;
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.eomMarketOperator = spotMarketOperator;
    }

    @Override
    public void makeBidBalancingMarket() {
        Date currentDate = TimeUtil.getCurrentDate();
        Date precedingDate = TimeUtil.precedingDate(currentDate);

        float priceForwardMin = priceForwardCurve.getMinimum(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());
        float priceForwardMax = priceForwardCurve.getMaximum(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());

        //float pPreceding = batteryTradeRegistry.getPositiveQuantityUsed(precedingDate);
        float mSpreadEOM = priceForwardCurve.getSpread(TimeUtil.getCurrentTick(), Market.BALANCING_MARKET.getTicks());

        if (mSpreadEOM >= marginalCosts) {
            //do bid  positive
            //do bid  negative
        }
//        float pNegative = Math.min(pPreceding - powerMin, dischargePower);
        //balancingMarketOperator.addNegativeSupply(new PowerNegative(bidNegative, pNegative, this));

//        float pPositive = Math.min(powerMax - pPreceding, chargePower);
        //balancingMarketOperator.addPositiveSupply(new PowerPositive(bidPositive, pPositive, this));

    }

    @ScheduledMethod(start = SequenceDefinition.SimulationStart, interval = SequenceDefinition.DayInterval, priority = SequenceDefinition.BalancingMarketBidPriority)
    public void calculateEOMActionsForDay() {
        float priceForwardDayMin = priceForwardCurve.getMinimum(TimeUtil.getCurrentTick(), SequenceDefinition.DayInterval);
        float priceForwardDayMax = priceForwardCurve.getMaximum(TimeUtil.getCurrentTick(), SequenceDefinition.DayInterval);

    }

    @Override
    public void makeBidEOM() {
        Date currentDate = TimeUtil.getCurrentDate();
        float capacity = batteryTradeRegistry.getCapacity(currentDate);

        float tFullLoad = capacity * (socMax - soc) / dischargePower;     // 5.43
        float fEmpty = capacity * (soc - socMin); // 5.44


        //eomMarketOperator.addSupply(new EnergySupply(marginalCosts * 1.1f, soc, this));
        //eomMarketOperator.addDemand(new EnergyDemand(marginalCosts * 0.9f, capacity - soc, this));

    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        batteryTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
        int assignedQuantity = (int) (bid.getQuantity() * rate);
        if (bid instanceof EnergyDemand) {
            soc += assignedQuantity;
        } else if (bid instanceof EnergySupply) {
            soc -= assignedQuantity;
        }
        lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        if (soc < socMin || soc > socMax) {
            throw new IllegalStateException("batterylevel exceeds capacity: " + soc + ", MaxLevel: " + capacity);
        }
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<TradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return batteryTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public String getName() {
        return name;
    }

    public int getSoc() {

        return soc;
    }

    @Override
    public void setBalancingMarketOperator(final BalancingMarketOperator balancingMarketOperator) {
        this.balancingMarketOperator = balancingMarketOperator;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
