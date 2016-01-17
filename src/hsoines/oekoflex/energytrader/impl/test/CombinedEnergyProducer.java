package hsoines.oekoflex.energytrader.impl.test;

import hsoines.oekoflex.bid.Bid;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.energytrader.EOMTrader;
import hsoines.oekoflex.energytrader.EnergyTradeHistory;
import hsoines.oekoflex.energytrader.MarketTraderVisitor;
import hsoines.oekoflex.energytrader.RegelenergieMarketTrader;
import hsoines.oekoflex.marketoperator.EOMOperator;
import hsoines.oekoflex.marketoperator.RegelEnergieMarketOperator;
import hsoines.oekoflex.strategies.ConstantPriceStrategy;
import hsoines.oekoflex.strategies.PriceStrategy;
import hsoines.oekoflex.util.Duration;
import hsoines.oekoflex.util.TimeUtilities;

import java.util.Date;
import java.util.List;

public class CombinedEnergyProducer implements RegelenergieMarketTrader, EOMTrader {

    private final String name;
    private hsoines.oekoflex.energytrader.EnergyTradeHistory produceHistory;
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
        produceHistory = new EnergyTradeRegistryImpl(EnergyTradeHistory.Type.PRODUCE, 10000);
    }

    @Override
    public void accept(final MarketTraderVisitor visitor) {
        visitor.visit((EOMTrader) this);
        visitor.visit((RegelenergieMarketTrader) this);
    }

    @Override
    public void makeBidEOM() {
        Date date = TimeUtilities.getCurrentDate();
        lastBidPrice = energyOnlyPriceStrategy.getPrice(date);
        int offerCapacity = produceHistory.getRemainingCapacity(date, Duration.FOUR_HOURS);
        EOMOperator.addSupply(new Supply(lastBidPrice, offerCapacity, this));
    }

    @Override
    public void makeBidRegelenergie() {
        Date date = TimeUtilities.getCurrentDate();
        int offerCapacity = (int) (produceHistory.getRemainingCapacity(date, Duration.FOUR_HOURS) * quantityPercentageOnRegelMarkt);
        regelenergieMarketOperator.addSupply(new Supply(regelMarktPriceStrategy.getPrice(date), offerCapacity, this));
    }

    @Override
    public void setEOMOperator(final EOMOperator marketOperator) {
        this.EOMOperator = marketOperator;
    }

    @Override
    public void notifyClearingDone(final float clearedPrice, final float rate, final Bid bid, final Date currentDate, final Duration duration) {
        this.lastClearedPrice = clearedPrice;
        lastAssignmentRate = rate;
        produceHistory.addAssignedQuantity(currentDate, Duration.QUARTER_HOUR, bid.getPrice(), clearedPrice, bid.getQuantity(), rate);
    }

    @Override
    public float getLastAssignmentRate() {
        return lastAssignmentRate;
    }

    @Override
    public List<EnergyTradeRegistryImpl.EnergyTradeElement> getCurrentAssignments() {
        return produceHistory.getEnergyTradeElements(TimeUtilities.getCurrentDate());
    }

    @Override
    public void setRegelenergieMarketOperator(RegelEnergieMarketOperator marketOperator) {
        regelenergieMarketOperator = marketOperator;
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
        produceHistory = new EnergyTradeRegistryImpl(hsoines.oekoflex.energytrader.EnergyTradeHistory.Type.PRODUCE, capacity);
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
