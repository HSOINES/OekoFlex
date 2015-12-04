package hsoines.oekoflex;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 04/12/15
 * Time: 19:12
 */
public interface MarketOperatorListener {
    void notifyExectionRate(float rate, Bid bid);
}
