package hsoines.oekoflex.display;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.chartpart.Chart;


/**
 * 
 */
public final class TestChartMain {

    private static Chart chart;

    public static void main(String[] args) {
        TestChartMain testChartMain = new TestChartMain();
//        chart = testChartMain.getChart3();
//        new SwingWrapper(chart).displayChart();
    }


    public Chart getChart() {

        // Create Chartx
        XYChart chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("Quantity").yAxisTitle("Bid").build();
        chart.addSeries("a", new double[]{0, 0, 3, 3, 5, 7, 9, 9, 15}, new double[]{0, 10, 10, 5, 9, 6, 5, -5, -5});
        chart.addSeries("b", new double[]{0, 2, 2, 4, 6, 9}, new double[]{1, 1, 3, 4, 0, 4});

        // Customize Chart
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

        return chart;
    }

//    public Chart getChart2() {
//        Chart_Category chart = new ChartBuilder_Category().width(800).height(600).title("Score Histogram").xAxisTitle("Score").yAxisTitle("Number").build();
//
//        // Customize Chart
//        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
//        chart.getStyler().setPlotContentSize(1.0);
//
//        // Series
//        chart.addSeries("test 1", Arrays.asList(new Integer[]{0, 1, 2, 3, 4}), Arrays.asList(new Integer[]{4, -5, 9, 6, 5}));
//
//        return chart;
//
//    }
//
//    public Chart getChart3() {
//
//        // Create Chart
//        Chart_XY chart = new ChartBuilder_XY().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("Age").yAxisTitle("Amount").build();
//
//        // Customize Chart
//        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
//        chart.getStyler().setDefaultSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Line);
//        chart.getStyler().setYAxisLabelAlignment(Styler.TextAlignment.Right);
//        chart.getStyler().setYAxisDecimalPattern("$ #,###.##");
//        chart.getStyler().setPlotMargin(0);
//        chart.getStyler().setPlotContentSize(.95);
//
//        // Series
//        // @formatter:off
//        double[] xAges = new double[]{60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87,
//                88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
//
//        double[] yLiability = new double[]{-672234, -691729, -711789, -732431, -753671, -775528, -798018, -821160, -844974, -869478, -907735, -887139, -865486,
//                -843023, -819621, 795398, 770426, 744749, 719011, 693176, 667342, 641609, 616078, 590846, 565385, 540002, 514620, 489380, 465149, 441817,
//                419513, 398465, 377991, 358784, 340920, 323724, 308114, 293097, 279356, 267008, 254873};
//
//        double[] yPercentile75th = new double[]{800000, 878736, 945583, 1004209, 1083964, 1156332, 1248041, 1340801, 1440138, 1550005, 1647728,
//                1705046, 1705032, 1710672, 1700847, 1683418, 1686522, 1674901, 1680456, 1679164, 1668514, 1672860, 1673988, 1646597, 1641842, 1653758,
//                1636317, 1620725, 1589985, 1586451, 1559507, 1544234, 1529700, 1507496, 1474907, 1422169, 1415079, 1346929, 1311689, 1256114, 1221034};
//
//        double[] yPercentile50th = new double[]{800000, 835286, 873456, 927048, 969305, 1030749, 1101102, 1171396, 1246486, 1329076, 1424666, 1424173,
//                1421853, 1397093, 1381882, 1364562, 1360050, 1336885, 1340431, 1312217, 1288274, 1271615, 1262682, 1237287, 1211335, 1191953, 1159689,
//                1117412, 1078875, 1021020, 974933, 910189, 869154, 798476, 744934, 674501, 609237, 524516, 442234, 343960, 257025};
//
//        double[] yPercentile25th = new double[]{800000, 791439, 809744, 837020, 871166, 914836, 958257, 1002955, 1054094, 1118934, 1194071, 1185041,
//                1175401, 1156578, 1132121, 1094879, 1066202, 1054411, 1028619, 987730, 944977, 914929, 880687, 809330, 783318, 739751, 696201, 638242,
//                565197, 496959, 421280, 358113, 276518, 195571, 109514, 13876, 29, 0, 0, 0, 0};
//        // @formatter:on
//
//        Series_XY seriesLiability = chart.addSeries("Liability", xAges, yLiability);
//        seriesLiability.setMarker(SeriesMarkers.NONE);
//        seriesLiability.setChartXYSeriesRenderStyle(Series_XY.ChartXYSeriesRenderStyle.Area);
//
//        Series_XY seriesPercentile75th = chart.addSeries("75th Percentile", xAges, yPercentile75th);
//        seriesPercentile75th.setMarker(SeriesMarkers.NONE);
//
//        Series_XY seriesPercentile50th = chart.addSeries("50th Percentile", xAges, yPercentile50th);
//        seriesPercentile50th.setMarker(SeriesMarkers.NONE);
//
//        Series_XY seriesPercentile25th = chart.addSeries("25th Percentile", xAges, yPercentile25th);
//        seriesPercentile25th.setMarker(SeriesMarkers.NONE);
//        return chart;
//    }

}
