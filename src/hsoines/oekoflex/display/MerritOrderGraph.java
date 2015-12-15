package hsoines.oekoflex.display;

import javax.swing.*;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.impl.EnergyOnlyMarketOperator;
import hsoines.oekoflex.demand.Demand;
import hsoines.oekoflex.supply.Supply;

import org.knowm.xchart.*;

import repast.simphony.context.Context;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.visualization.DisplayEditorLifecycle;
import repast.simphony.visualization.DisplayListener;
import repast.simphony.visualization.IDisplay;
import repast.simphony.visualization.Layout;
import repast.simphony.visualization.ProbeListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MerritOrderGraph implements IDisplay {

    private final Context<OekoflexAgent> context;
    private Chart chart;
    private JPanel panel;
    private EnergyOnlyMarketOperator eomOperator;

    public MerritOrderGraph(Context<OekoflexAgent> context) {

        this.context = context;

        Iterable<OekoflexAgent> iter = context.getAgentLayer(OekoflexAgent.class);
        for (OekoflexAgent oekoflexAgent : iter) {
            if (oekoflexAgent.getName().equals("EOM_Operator")) {
                eomOperator = (EnergyOnlyMarketOperator) oekoflexAgent;
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

        double tickCount = RepastEssentials.GetTickCount();
        Date date = new Date(((long)tickCount-1) * 15 * 60 * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String info = "Date: " + format.format(date) +
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
