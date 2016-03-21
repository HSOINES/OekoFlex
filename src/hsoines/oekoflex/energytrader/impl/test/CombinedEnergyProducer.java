package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.bid.PowerPositive;
import hsoines.oekoflex.energytrader.BalancingMarketTrader;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.TradeRegistry;
import hsoines.oekoflex.energytrader.impl.TradeRegistryImpl;
import hsoines.oekoflex.marketoperator.BalancingMarketOperator;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

import java.util.Date;
import java.util.List;

public class CombinedEnergyProducer implements BalancingMarketTrader, EOMTrader {

    private final String name;
    private TradeRegistry tradeRegistry;
    private PriceStrategy regelMarktPriceStrategy;
    private ConstantPriceStrategy energyOnlyPriceStrategy;
    private SpotMarketOperator SpotMarketOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private BalancingMarketOperator balancingMarketOperator;
    private int capacity;
    private float quantityPercentageOnRegelMarkt;

    public CombinedEnergyProducer(String name) {
        this.name = name;
        init();
    }

    public void init() {
        tradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, 5000, 1000);
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtil.getCurrentDate();
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        float offerCapacity = tradeRegistry.getRemainingCapacity(date, Market.BALANCING_MARKET);
        SpotMarketOperator.addSupply(new EnergySupply(lastBidPrice, offerCapacity, this));
    }

    @Override
    public void makeBidBalancingMarket() {
        Date date = TimeUtil.getCurrentDate();
        int offerCapacity = (int) (tradeRegistry.getRemainingCapacity(date, Market.BALANCING_MARKET) * quantityPercentageOnRegelMarkt);
        balancingMarketOperator.addPositiveSupply(new PowerPositive(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
    }

    @Override
    public void setSpotMarketOperator(final SpotMarketOperator spotMarketOperator) {
        this.SpotMarketOperator = spotMarketOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        tradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
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
    public void setBalancingMarketOperator(BalancingMarketOperator balancingMarketOperator) {
        this.balancingMarketOperator = balancingMarketOperator;
    }

    @Override
    public float getLastClearedPrice() {
        return lastClearedPrice;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setCapacity(final int capacity) {
        this.capacity = capacity;
        tradeRegistry = new TradeRegistryImpl(TradeRegistry.Type.PRODUCE, capacity, 1000);
    }

    public void setPriceRegelMarkt(final float priceRegelMarkt) {
        regelMarktPriceStrategy = new ConstantPriceStrategy(priceRegelMarkt);
    }

    public void setPriceEnergyOnlyMarkt(final float priceEnergyOnlyMarkt) {
        energyOnlyPriceStrategy = new ConstantPriceStrategy(priceEnergyOnlyMarkt);

    }

    public void setQuantityPercentageOnRegelMarkt(final float quantityPercentageOnRegelMarkt) {
        this.quantityPercentageOnRegelMarkt = quantityPercentageOnRegelMarkt;
    }


    @Override
    public String getDescription() {
        return "";
    }
}
