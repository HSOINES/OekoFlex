package hsoines.oekoflex.bid;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 11:41
 */
public class BidSupportTest {

    @Test
    public void testDemandSorter() throws Exception {
        final BidSupport.DemandSorter demandSorter = new BidSupport.DemandSorter();
        List<Bid> list = new ArrayList<>();
        list.add(new EnergyDemand(10, 10, null));
        list.add(new EnergyDemand(9, 10, null));
        list.add(new EnergyDemand(10, 11, null));
        list.add(new EnergyDemand(9, 12, null));
        list.add(new EnergyDemand(7, 10, null));

        list.sort(demandSorter);

        final Bid bid0 = list.get(0);
        assertEquals(10f, bid0.getPrice(), 0.0001f);
        assertEquals(11f, bid0.getQuantity(), 0.0001f);
        final Bid bid1 = list.get(1);
        assertEquals(10f, bid1.getPrice(), 0.0001f);
        assertEquals(10f, bid1.getQuantity(), 0.0001f);
        final Bid bid2 = list.get(2);
        assertEquals(9f, bid2.getPrice(), 0.0001f);
        assertEquals(12f, bid2.getQuantity(), 0.0001f);
        final Bid bid3 = list.get(3);
        assertEquals(9f, bid3.getPrice(), 0.0001f);
        assertEquals(10f, bid3.getQuantity(), 0.0001f);
        final Bid bid4 = list.get(4);
        assertEquals(7f, bid4.getPrice(), 0.0001f);
        assertEquals(10f, bid4.getQuantity(), 0.0001f);
    }

    @Test
    public void testSupplySorter() throws Exception {
        final BidSupport.SupplySorter demandSorter = new BidSupport.SupplySorter();
        List<Bid> list = new ArrayList<>();
        list.add(new EnergySupply(10, 10, null));
        list.add(new EnergySupply(9, 10, null));
        list.add(new EnergySupply(10, 11, null));
        list.add(new EnergySupply(9, 12, null));
        list.add(new EnergySupply(7, 10, null));

        list.sort(demandSorter);

        final Bid bid0 = list.get(0);
        assertEquals(7f, bid0.getPrice(), 0.0001f);
        assertEquals(10f, bid0.getQuantity(), 0.0001f);
        final Bid bid1 = list.get(1);
        assertEquals(9f, bid1.getPrice(), 0.0001f);
        assertEquals(12f, bid1.getQuantity(), 0.0001f);
        final Bid bid2 = list.get(2);
        assertEquals(9f, bid2.getPrice(), 0.0001f);
        assertEquals(10f, bid2.getQuantity(), 0.0001f);
        final Bid bid3 = list.get(3);
        assertEquals(10f, bid3.getPrice(), 0.0001f);
        assertEquals(11f, bid3.getQuantity(), 0.0001f);
        final Bid bid4 = list.get(4);
        assertEquals(10f, bid4.getPrice(), 0.0001f);
        assertEquals(10f, bid4.getQuantity(), 0.0001f);
    }
}