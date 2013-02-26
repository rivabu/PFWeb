package org.rients.com.executables;

import java.awt.Color;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.time.Week;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

public class TestJFreeChart extends ApplicationFrame {

    private static final long serialVersionUID = -3921020683567997341L;

    static final public Color COLOR_SPAM = new Color(250, 112, 10);

    /**
     * Constructs a new demonstration application.
     * 
     * @param title
     *            the frame title.
     */
    public TestJFreeChart(String title) {
        super(title);
        JPanel panel = createDemoPanel();
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(panel);
    }

    /**
     * Creates a panel for the demo
     * 
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = getDemoChart();
        return new ChartPanel(chart);
    }

    /**
     * Get demo chart
     * 
     * @return
     */
    public static JFreeChart getDemoChart() {

        // Creating data
        TimeTableXYDataset dataset = new TimeTableXYDataset();

        LinkedHashMap<Date, Number> numbers = new LinkedHashMap<Date, Number>();

        numbers.put(getDate(1228700000 - (86400 * 7)), 768);
        numbers.put(getDate(1229295600 - (86400 * 7)), 1510);
        numbers.put(getDate(1229900400 - (86400 * 7)), 1791);
        numbers.put(getDate(1230505200 - (86400 * 7)), 1024);
        numbers.put(getDate(1231110000 - (86400 * 7)), 1792);
        numbers.put(getDate(1231714800 - (86400 * 7)), 585);
        numbers.put(getDate(1232319600 - (86400 * 7)), 216);
        numbers.put(getDate(1232924400 - (86400 * 7)), 208);
        numbers.put(getDate(1233529200 - (86400 * 7)), 168);
        numbers.put(getDate(1234134000 - (86400 * 7)), 232);
        numbers.put(getDate(1234738800 - (86400 * 7)), 16);

        int numberOfBars = numbers.size();

        for (Date date : numbers.keySet()) {
            Number value = numbers.get(date);
            dataset.add(new Week(date, TimeZone.getDefault(), Locale.getDefault()), value, "Spam", false);
        }

        // Creating chart
        JFreeChart chart = ChartFactory.createXYBarChart(null, // chart title
                null, // domain axis label
                true, "Amount", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips?
                false // URLs?
                );

        chart.setBorderVisible(false);
        chart.setAntiAlias(true);
        chart.setBackgroundPaint(java.awt.Color.white);
        chart.setPadding(new RectangleInsets(0.0, 0.0, 0.0, 0.0));

        XYPlot plot = (XYPlot) chart.getPlot();

        plot.setNoDataMessage("No data available");
        plot.setBackgroundPaint(java.awt.Color.white);
        plot.setOutlineVisible(false);
        plot.setAxisOffset(new RectangleInsets(0.0, 0.0, 0.0, 0.0));

        BlockFrame legendBorder = new BlockBorder(Color.white);

        LegendTitle legend = chart.getLegend();
        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
        legend.setFrame(legendBorder);
        legend.setBackgroundPaint(Color.white);
        legend.setPosition(RectangleEdge.RIGHT);

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setMargin(0.15);
        renderer.setSeriesPaint(0, COLOR_SPAM);
        renderer.setDrawBarOutline(true);

        // Configure the X axis
        DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
        dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        dateAxis.setVerticalTickLabels(true);
        dateAxis.setUpperMargin(0.00);
        dateAxis.setLowerMargin(0.00);

        DateTickUnit unit = new DateTickUnit(DateTickUnit.DAY, Math.max(Math.round(numberOfBars / 15), 1) * 7,
                new java.text.SimpleDateFormat("w yyyy"));
        dateAxis.setTickUnit(unit, false, true);

        // Configure the Y axis
        NumberAxis rangeAxis = new NumberAxis("Amount");
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperMargin(0.00);
        rangeAxis.setLowerMargin(0.00);

        return chart;
    }

    public static Date getDate(long time) {
        Date date = new Date();
        date.setTime(time * 1000);
        return date;
    }

    public static void main(String[] args) {

        TestJFreeChart demo = new TestJFreeChart("Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
}
