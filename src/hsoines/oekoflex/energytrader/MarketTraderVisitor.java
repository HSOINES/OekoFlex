package hsoines.oekoflex.energytrader;

/**
 * User: jh
 * Date: 15/01/16
 * Time: 20:52
 */
public interface MarketTraderVisitor {
    void visit(EOMTrader eomTrader);

    void visit(RegelenergieMarketTrader regelenergieMarketTrader);
}
