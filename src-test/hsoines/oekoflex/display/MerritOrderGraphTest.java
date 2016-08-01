package hsoines.oekoflex.display;

import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.SpotMarketOperator;
import hsoines.oekoflex.util.TimeUtil;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * Created by jhense on 15.03.2016.
 */
public class MerritOrderGraphTest {
    @Test
    public void testDisplay() throws Exception {
        TimeUtil.nextTick();

        JFrame jFrame = new JFrame();
        MySpotMarketOperator mySpotMarketOperator = new MySpotMarketOperator();
        MerritOrderGraph merritOrderGraph = new MerritOrderGraph(mySpotMarketOperator);
        merritOrderGraph.init();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                Dimension size = new Dimension(400, 400);
                jFrame.setMinimumSize(size);
                jFrame.setMaximumSize(size);
                jFrame.setSize(size);

                //  Thread.sleep(4000);

                merritOrderGraph.render();
                jFrame.setLayout(new BorderLayout());
                jFrame.add(merritOrderGraph.getPanel(), BorderLayout.CENTER);
                jFrame.setVisible(true);
                jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
    });
        Thread.sleep(4000);
    }

        private static class MySpotMarketOperator implements SpotMarketOperator {
        @Override
        public String getName() {
            return "test merrit order";
        }

        @Override
        public void addDemand(EnergyDemand energyDemand) {

        }

        @Override
        public void addSupply(EnergySupply supply) {

        }

        @Override
        public void clearMarket() {

        }

        @Override
        public float getTotalClearedQuantity() {
            return 4;
        }

        @Override
        public float getLastClearedPrice() {
            return 3;
        }

        @Override
        public float getLastAssignmentRate() {
            return .434f;
        }

        @Override
        public void stop() {

        }

        @Override
        public List<EnergySupply> getLastEnergySupplies() {
            return Collections.singletonList(new EnergySupply(234f, 100f, null));
        }

        @Override
        public List<EnergyDemand> getLastEnergyDemands() {
            return Collections.singletonList(new EnergyDemand(111f,343,null));
        }

        @Override
        public AssignmentType getLastAssignmentType() {
            return AssignmentType.Full;
        }
    }
}