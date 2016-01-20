package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeRegistry;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.energytrader.impl.EnergyTradeRegistryImpl;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Market;
import hsoines.oekoflex.util.TimeUtil;

import java.util.Date;
import java.util.List;

public class CombinedEnergyProducer implements RegelenergieMarketTrader, EOMTrader {

    private final String name;
    private EnergyTradeRegistry energyTradeRegistry;
    private PriceStrategy regelMarktPriceStrategy;
    private ConstantPriceStrategy energyOnlyPriceStrategy;
    private EOMOperator EOMOperator;
    private float lastClearedPrice;
    private float lastAssignmentRate;

    private float lastBidPrice;
    private RegelEnergieMarketOperator regelenergieMarketOperator;
    private int capacity;
    private float quantityPercentageOnRegelMarkt;

    public CombinedEnergyProducer(String name) {
        this.name = name;
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, 5000);
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtil.getCurrentDate();
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        int offerCapacity = energyTradeRegistry.getRemainingCapacity(date, Market.REGELENERGIE_MARKET);
        EOMOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date date = TimeUtil.getCurrentDate();
        int offerCapacity = (int) (energyTradeRegistry.getRemainingCapacity(date, Market.REGELENERGIE_MARKET) * quantityPercentageOnRegelMarkt);
        regelenergieMarketOperator.addSupply(new Supply(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
    }

    @Override
    public void setEOMOperator(final EOMOperator eomOperator) {
        this.EOMOperator = eomOperator;
    }

    @Override
    public void notifyClearingDone(final Date currentDate, final Market market, final Bid bid, final float clearedPrice, final float rate) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        energyTradeRegistry.addAssignedQuantity(currentDate, market, bid.getPrice(), clearedPrice, bid.getQuantity(), rate, bid.getBidType());
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return energyTradeRegistry.getEnergyTradeElements(TimeUtil.getCurrentDate());
    }

    @Override
    public void setRegelenergieMarketOperator(RegelEnergieMarketOperator regelenergieMarketOperator) {
        this.regelenergieMarketOperator = regelenergieMarketOperator;
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
        energyTradeRegistry = new EnergyTradeRegistryImpl(EnergyTradeRegistry.Type.PRODUCE, capacity);
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

}
