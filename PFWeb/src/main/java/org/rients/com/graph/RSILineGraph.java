package org.rients.com.graph;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.ImageResponse;

public class RSILineGraph {

    public ImageResponse generateRSIGraph(Matrix matrix, String type, int DAYS) {
        ImageResponse imageResponse = new ImageResponse();
        XYSeriesCollection dataset = new XYSeriesCollection();
        int aantalColumns = matrix.getAantalColumns();

        for (int counter = 0; counter < aantalColumns; counter++) {
        	if (matrix.getColumn(counter).isToGraph()) {
	            String fundName = matrix.getColumn(counter).getColumnName();
	            XYSeries points = new XYSeries(fundName);
	            Iterator<Object> iter = matrix.getColumn(counter).getValues().iterator();
	            int size = matrix.getColumn(counter).getValues().size();
	            int xas = 0;
	            if (size < DAYS) {
	                for (int teller = size; teller < DAYS; teller++) {
	                    xas ++;
	                }
	            }
	            while (iter.hasNext()) {
	                points.add(xas, (Double) iter.next());
	                xas ++;
	            }
	            dataset.addSeries(points);
        	}
        }
        
        
        
        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart("", // Title
                "", // x-axis Label
                "", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                false, // Show Legend
                false, // Use tooltips
                false // Configure chart to generate URLs?
                );
        
//        NumberAxis axis=new NumberAxis();
//        axis.setTickLabelsVisible(false);
//        //VerticalTickLabels(false);
//        axis.setAutoRange(true);
//        axis.setAutoRangeIncludesZero(false);
//        axis.setLowerMargin(0.0);
//        axis.setUpperMargin(0.0);
//        chart.getXYPlot().setDomainAxis(axis);
        
        
        final NumberAxis rangeAxis = (NumberAxis)  chart.getXYPlot().getRangeAxis();
        //.setAxisLineVisible(false);
        //rangeAxis.setVisible(false);
        //rangeAxis.setLabelPaint(Color.BLUE);
        //rangeAxis.setRange(0, 3);
        rangeAxis.setTickLabelsVisible(false);
        
        chart.getPlot().setBackgroundPaint(Color.BLACK);

        chart.setBackgroundPaint(Color.BLACK);
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (type.equals("small")) {
                ChartUtilities.writeChartAsPNG(buffer, chart, 900, 200);
            } else if (type.equals("normaal")) {
                ChartUtilities.writeChartAsPNG(buffer, chart, 1250, 300);
            } else if (type.equals("groot")) {
                ChartUtilities.writeChartAsPNG(buffer, chart, 1500, 400);
            }
            imageResponse.setContent(buffer.toByteArray());
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
        return imageResponse;
    }

}
