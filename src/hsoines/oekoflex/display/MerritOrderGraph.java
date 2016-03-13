package hsoines.oekoflex.display;

import hsoines.oekoflex.OekoflexAgent;
import hsoines.oekoflex.bid.EnergyDemand;
import hsoines.oekoflex.bid.EnergySupply;
import hsoines.oekoflex.marketoperator.impl.SpotMarketOperatorImpl;
import hsoines.oekoflex.util.TimeUtil;
import org.knowm.xchart.ChartBuilder_XY;
import org.knowm.xchart.Chart_XY;
import org.knowm.xchart.Series_XY;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.internal.style.Styler;
import repast.simphony.context.Context;
import repast.simphony.visualization.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
todo
Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException
	at org.knowm.xchart.Series_XY.getLegendRenderType(Series_XY.java:84)
	at org.knowm.xchart.internal.chartpart.Legend_AxesChart.paint(Legend_AxesChart.java:86)
	at org.knowm.xchart.Chart_XY.paint(Chart_XY.java:253)
	at org.knowm.xchart.Chart_XY.paint(Chart_XY.java:224)
	at org.knowm.xchart.XChartPanel.paintComponent(XChartPanel.java:95)
	at javax.swing.JComponent.paint(Unknown Source)
 */
public class MerritOrderGraph implements IDisplay {

    public static final String SUPPLIES = "Supplies";
    public static final String DEMANDS = "Demands";
    private final Context<OekoflexAgent> context;
    private Chart_XY chart;
    private JPanel panel;
    private SpotMarketOperatorImpl eomOperator;

    public MerritOrderGraph(Context<OekoflexAgent> context) {

        this.context = context;

        Iterable<OekoflexAgent> iter = context.getAgentLayer(OekoflexAgent.class);
        for (OekoflexAgent oekoflexAgent : iter) {
            if (oekoflexAgent.getName().equals("EOM_Operator")) {
                eomOperator = (SpotMarketOperatorImpl) oekoflexAgent;
            }
        }
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        chart = new ChartBuilder_XY().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("Quantity").yAxisTitle("Bid").build();
        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Line);

    }

    @Override
    public void render() {
        panel.removeAll();

        chart.removeSeries(SUPPLIES);
        chart.removeSeries(DEMANDS);

        panel.add(new XChartPanel<>(chart), BorderLayout.CENTER);

        List<EnergySupply> lastSupplies = eomOperator.getLastSupplies();
        List<Float> quantityValues = new ArrayList<>();
        List<Float> priceValues = new ArrayList<>();
        float lastQuantity = 0;
        quantityValues.add(lastQuantity);
        priceValues.add(0f);
        for (EnergySupply lastSupply : lastSupplies) {
            quantityValues.add(lastQuantity);
            quantityValues.add(lastQuantity + lastSupply.getQuantity());
            lastQuantity += lastSupply.getQuantity();
            priceValues.add(lastSupply.getPrice());
            priceValues.add(lastSupply.getPrice());
        }
        quantityValues.add(lastQuantity);
        priceValues.add(0f);
        chart.addSeries(SUPPLIES, quantityValues, priceValues);

        List<EnergyDemand> lastEnergyDemands = eomOperator.getLastEnergyDemands();
        quantityValues = new ArrayList<>();
        priceValues = new ArrayList<>();
        lastQuantity = 0;
        quantityValues.add(lastQuantity);
        priceValues.add(0f);
        for (EnergyDemand lastEnergyDemand : lastEnergyDemands) {
            quantityValues.add(lastQuantity);
            quantityValues.add(lastQuantity + lastEnergyDemand.getQuantity());
            lastQuantity += lastEnergyDemand.getQuantity();
            priceValues.add(lastEnergyDemand.getPrice());
            priceValues.add(lastEnergyDemand.getPrice());
        }
        quantityValues.add(lastQuantity);
        priceValues.add(0f);
        chart.addSeries(DEMANDS, quantityValues, priceValues);


        Date date = TimeUtil.getCurrentDate();
        String info = "Date: " + TimeUtil.dateFormat.format(date) +
                ", Last Cleared Price:" + eomOperator.getLastClearedPrice() +
                ", Assignment Type: " + eomOperator.getLastAssignmentType().toString() +
                " ";
        panel.add(new JLabel(info), BorderLayout.SOUTH);

    }

    @Override
    public void setPause(boolean pause) {
    }

    @Override
    public void update() {
    }

    @Override
    public void init() {
    }

    @Override
    public void setLayout(Layout<?, ?> layout) {
    }

    @Override
    public void addDisplayListener(DisplayListener listener) {
    }

    @Override
    public void setLayoutFrequency(LayoutFrequency frequency, int interval) {
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void registerToolBar(JToolBar bar) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void iconified() {
    }

    @Override
    public void deIconified() {
    }

    @Override
    public void closed() {
    }

    @Override
    public void addProbeListener(ProbeListener listener) {
    }

    @Override
    public Layout<?, ?> getLayout() {
        return null;
    }

    @Override
    public DisplayEditorLifecycle createEditor(JPanel panel) {
        return null;
    }

    @Override
    public void resetHomeView() {
    }
}
