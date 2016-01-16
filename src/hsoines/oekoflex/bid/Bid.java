package hsoines.oekoflex.bid;

/**
 * Created by IntelliJ IDEA.
 * User: jh
 * Date: 04/12/15
 * Time: 19:13
 */
public interface Bid {
    interface BidVisitor {
        void visit(Demand demand);

        void visit(Supply supply);
    }

    void accept(BidVisitor bidVisitor);  //todo

    float getPrice();

    int getQuantity();

    String getTypeString();

}
