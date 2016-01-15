package hsoines.oekoflex.display;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.Demand;
import hsoines.oekoflex.bid.Supply;
import hsoines.oekoflex.marketoperator.impl.EOMOperatorImpl;
import hsoines.oekoflex.util.TimeUtilities;
import org.knowm.xchart.Chart;
import org.knowm.xchart.ChartBuilder;
import org.knowm.xchart.StyleManager;
import org.knowm.xchart.XChartPanel;
import repast.simphony.context.Context;
import repast.simphony.visualization.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MerritOrderGraph implements IDisplay {

    private final Context<OekoflexAgent> context;
    private Chart chart;
    private JPanel panel;
    private EOMOperatorImpl eomOperator;

    public MerritOrderGraph(Context<OekoflexAgent> context) {

        this.context = context;

        Iterable<OekoflexAgent> iter = context.getAgentLayer(OekoflexAgent.class);
        for (OekoflexAgent oekoflexAgent : iter) {
            if (oekoflexAgent.getName().equals("EOM_Operator")) {
                eomOperator = (EOMOperatorImpl) oekoflexAgent;
            }
        }
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        panel.removeAll();
        chart = new ChartBuilder().chartType(StyleManager.ChartType.Area)
                .width(800).height(600).title(getClass()
                        .getSimpleName()).xAxisTitle("Quantity").yAxisTitle("Bid").build();
        panel.add(new XChartPanel(chart), BorderLayout.CENTER);
        List<Supply> lastSupplies = eomOperator.getLastSupplies();
        List<Float> quantityValues = new ArrayList<>();
        List<Float> priceValues = new ArrayList<>();
        float lastQuantity = 0;
        for (Supply lastSupply : lastSupplies) {
            quantityValues.add(lastQuantity);
            quantityValues.add(lastQuantity + lastSupply.getQuantity());
            lastQuantity += lastSupply.getQuantity();
            priceValues.add(lastSupply.getPrice());
            priceValues.add(lastSupply.getPrice());
        }
        chart.addSeries("Supplies", quantityValues, priceValues);

        List<Demand> lastDemands = eomOperator.getLastDemands();
        quantityValues = new ArrayList<>();
        priceValues = new ArrayList<>();
        lastQuantity = 0;
        for (Demand lastDemand : lastDemands) {
            quantityValues.add(lastQuantity);
            quantityValues.add(lastQuantity + lastDemand.getQuantity());
            lastQuantity += lastDemand.getQuantity();
            priceValues.add(lastDemand.getPrice());
            priceValues.add(lastDemand.getPrice());
        }
        chart.addSeries("Demands", quantityValues, priceValues);

        // Customize Chart
        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
        chart.getStyleManager().setAxisTitlesVisible(false);

        Date date = TimeUtilities.getCurrentDate();
        String info = "Date: " + TimeUtilities.dateFormatter.format(date) +
                ", Last Cleared Price:" + eomOperator.getLastClearedPrice() +
                ", Assignment Type: " + eomOperator.getLastAssignmentType().toString() +
                " ";
        panel.add(new JLabel(info), BorderLayout.SOUTH);

    }

    @Override
    public void setPause(boolean pause) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
    }

    @Override
    public void setLayout(Layout<?, ?> layout) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addDisplayListener(DisplayListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLayoutFrequency(LayoutFrequency frequency, int interval) {
        // TODO Auto-generated method stub

    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void registerToolBar(JToolBar bar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void iconified() {
        // TODO Auto-generated method stub

    }

    @Override
    public void deIconified() {
        // TODO Auto-generated method stub

    }

    @Override
    public void closed() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addProbeListener(ProbeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Layout<?, ?> getLayout() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DisplayEditorLifecycle createEditor(JPanel panel) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void resetHomeView() {
        // TODO Auto-generated method stub

    }

}
