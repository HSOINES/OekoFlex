package hsoines.oekoflex.display;

import org.knowm.xchart.Chart;
import org.knowm.xchart.ChartBuilder;
import org.knowm.xchart.StyleManager;
import org.knowm.xchart.SwingWrapper;


/**
 * User: jh
 * Date: 15/12/15
 * Time: 14:47
 */
public final class TestChartMain {
    public static void main(String[] args) {
        TestChartMain testChartMain = new TestChartMain();
        Chart chart = testChartMain.getChart();
        new SwingWrapper(chart).displayChart();
    }


     public Chart getChart() {

       // Create Chartx
       Chart chart = new ChartBuilder().chartType(StyleManager.ChartType.Area).width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();
       chart.addSeries("a", new double[] { 0, 3, 3, 5, 7, 9 }, new double[] { 10, 10,5, 9, 6, 5 });
       chart.addSeries("b", new double[] { 0, 2, 2, 4, 6, 9 }, new double[] { 1, 1, 3, 4, 0, 4 });

       // Customize Chart
       chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.InsideNW);
       chart.getStyleManager().setAxisTitlesVisible(false);

       return chart;
     }
}
