package hsoines.oekoflex.impl;

import hsoines.oekoflex.ask.Ask;
import hsoines.oekoflex.bid.Bid;

import static org.junit.Assert.*;

/**
 * Created by jhense on 03.12.2015.
 */
public class SimpleMarketOperatorTest {

    @org.junit.Test
    public void testAssignBids() throws Exception {
        SimpleMarketOperator simpleMarketOperator = new SimpleMarketOperator();
        simpleMarketOperator.addBid(new Bid(4.0f, 120));
        simpleMarketOperator.addBid(new Bid(3.6f, 50));
        simpleMarketOperator.addBid(new Bid(3.2f, 50));
        simpleMarketOperator.addBid(new Bid(2.7f, 80));
        simpleMarketOperator.addBid(new Bid(2.1f, 80));
        simpleMarketOperator.addBid(new Bid(2.1f, 80));

        simpleMarketOperator.addAsk(new Ask(2f, 180));
        simpleMarketOperator.addAsk(new Ask(2.1f, 140));
        simpleMarketOperator.addAsk(new Ask(2.8f, 80));
        simpleMarketOperator.addAsk(new Ask(3f, 90));//2/3 davon
        simpleMarketOperator.addAsk(new Ask(3.4f, 70));

        simpleMarketOperator.assignBids();

        assertEquals(460, simpleMarketOperator.getTotalAskAmount());
        assertEquals(460, simpleMarketOperator.getTotalBidAmount());
        assertEquals(1372, simpleMarketOperator.getBidCosts(), 0.0001);
        assertEquals(1058, simpleMarketOperator.getAskCosts(), 0.0001);
    }
}