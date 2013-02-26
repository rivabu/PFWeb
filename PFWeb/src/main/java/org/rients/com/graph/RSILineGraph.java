package org.rients.com.graph;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.rients.com.indexpredictor.Matrix;
import org.rients.com.model.ImageResponse;

public class RSILineGraph {

    public ImageResponse generateRSIGraph(Matrix matrix, String type, int DAYS) {
        ImageResponse imageResponse = new ImageResponse();
        XYSeriesCollection dataset = new XYSeriesCollection();
        int aantalFunds = matrix.getAantalFunds();
        XYSeries middleLine = new XYSeries("");
        Integer fifty = new Integer(50);
        // rechte lijn in het midden
        for (int i = 0; i < DAYS; i++) {
            middleLine.add(i, fifty );
        }
        dataset.addSeries(middleLine);
        for (int counter = 0; counter < aantalFunds; counter++) {
            String fundName = matrix.getFundData(counter).getFundName();
            XYSeries points = new XYSeries(fundName);
            Iterator<Integer> iter = matrix.getFundData(counter).getValues().iterator();
            int size = matrix.getFundData(counter).getValues().size();
            int xas = 0;
            if (size < DAYS) {
                for (int teller = size; teller < DAYS; teller++) {
                    points.add(xas, fifty);
                    xas ++;
                }
            }
            while (iter.hasNext()) {
                points.add(xas, iter.next());
                xas ++;
            }
            dataset.addSeries(points);
        }
        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart("", // Title
                "", // x-axis Label
                "", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
                );
        
        chart.getPlot().setBackgroundPaint(Color.white);
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);
        chart.setBackgroundPaint(Color.white);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (type.equals("small")) {
                ChartUtilities.writeChartAsPNG(buffer, chart, 900, 200);
            } else {
                ChartUtilities.writeChartAsPNG(buffer, chart, 1250, 750);
            }
            imageResponse.setContent(buffer.toByteArray());
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
        return imageResponse;
    }

}
