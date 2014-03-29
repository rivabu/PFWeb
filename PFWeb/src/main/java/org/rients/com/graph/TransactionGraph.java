package org.rients.com.graph;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.rients.com.constants.Constants;
import org.rients.com.model.ImageResponse;
import org.rients.com.model.Transaction;
import org.rients.com.services.FileIOServiceImpl;

public class TransactionGraph {

    
    public ImageResponse generate() {
        final XYDataset dataset = createDataset();
        ImageResponse imageResponse = createImage(dataset);
        return imageResponse;
    }

    
    public TimeSeries createTimeSeries() {

        final TimeSeries t1 = new TimeSeries("");
        try {
            FileIOServiceImpl fileIOService = new FileIOServiceImpl(null, null);

            List<Transaction> list = fileIOService.readFromTransactiesFile(Constants.TRANSACTIONDIR, Constants.ALL_TRANSACTIONS, null);
            Iterator<Transaction> iter = list.iterator();
            float totalScore = 60000;
            Map<Integer, Float> data = new TreeMap<Integer, Float>();
            
            while(iter.hasNext()) {
                Transaction trans = iter.next();
                Integer date = new Integer(trans.getEndDate());
                if (trans.getEndRate() > 0) {
                    if (data.containsKey(date)) {
                        Float value = data.get(date);
                        value = value + new Float(trans.getScoreAbs());
                        data.put(date, value);
                    } else {
                        data.put(date, new Float(trans.getScoreAbs()));
                    }
                }
            }
            Iterator<Integer> iter2 = data.keySet().iterator();
            while(iter2.hasNext()) {
                Integer date = iter2.next();
                Float value = data.get(date);
                totalScore = totalScore + value;
                String endDate = "" + date;
                int year = Integer.parseInt(endDate.substring(0, 4));
                int month = Integer.parseInt(endDate.substring(4, 6));
                int day = Integer.parseInt(endDate.substring(6));
                t1.add(new Day(day, month, year), new Float(totalScore));
            }
            System.out.println("totalScore: " + totalScore);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return t1;
    }
    public XYDataset createDataset() {

        final TimeSeries eur = createTimeSeries();

        // ****************************************************************************
        // * JFREECHART DEVELOPER GUIDE                                               *
        // * The JFreeChart Developer Guide, written by David Gilbert, is available   *
        // * to purchase from Object Refinery Limited:                                *
        // *                                                                          *
        // * http://www.object-refinery.com/jfreechart/guide.html                     *
        // *                                                                          *
        // * Sales are used to provide funding for the JFreeChart project - please    * 
        // * support us so that we can continue developing free software.             *
        // ****************************************************************************

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(eur);
        
        return dataset;

    }
    @SuppressWarnings("deprecation")
    private ImageResponse createImage(XYDataset dataset) {
        ImageResponse imageResponse = new ImageResponse();
        
        
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Transaction Overview", 
                "", 
                "",
                dataset, 
                true, 
                true, 
                false
            );
            final XYItemRenderer renderer = chart.getXYPlot().getRenderer();
            final StandardXYToolTipGenerator g = new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")
            );
            renderer.setToolTipGenerator(g);
            chart.setBackgroundPaint(Color.white);
        
        
        /*
        
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
        */
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(buffer, chart, 1100, 600);
            //ChartUtilities.writeChartAsPNG(buffer, chart, 1250, 750);
            imageResponse.setContent(buffer.toByteArray());
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
        }
        return imageResponse;
    }



}
