package org.rients.com.graph;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

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
import org.rients.com.model.Dagkoers;
import org.rients.com.model.ImageResponse;
import org.rients.com.model.Transaction;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.services.FileIOServiceImpl;
import org.rients.com.utils.Formula;
import org.rients.com.utils.SMA;

public class TransactionGraph {

    
    public ImageResponse generate() {
        final XYDataset dataset = createDataset();
        ImageResponse imageResponse = createImage(dataset);
        return imageResponse;
    }

    
    public TimeSeries createTransactionTimeSeries(TimeSeriesCollection dataset) {

        final TimeSeries t1 = new TimeSeries("");
        //final TimeSeries t2 = new TimeSeries("");
        try {
            FileIOServiceImpl fileIOService = new FileIOServiceImpl(null, null);

            List<Transaction> list = fileIOService.readFromTransactiesFile(Constants.TRANSACTIONDIR, Constants.ALL_TRANSACTIONS, null);
            //float totalScore1 = StrengthWeaknessConstants.sellAfterDays * 1000;
            //float totalScore2 = StrengthWeaknessConstants.sellAfterDays * 1000;
            //float totalScore3 = StrengthWeaknessConstants.sellAfterDays * 1000;
//            Map<Integer, Float> data = new TreeMap<Integer, Float>();
//            Map<Integer, Float> data2 = new TreeMap<Integer, Float>();
            
            Iterator<Transaction> iter = list.iterator();
            float totalScore = 100;
            while(iter.hasNext()) {
                Transaction trans = iter.next();
                System.out.println(trans);
                Integer date = new Integer(trans.getStartDate());
                String startDate = "" + date;
                int year = Integer.parseInt(startDate.substring(0, 4));
                int month = Integer.parseInt(startDate.substring(4, 6));
                int day = Integer.parseInt(startDate.substring(6));
                totalScore = totalScore * (1 + trans.getScorePerc() / 100);
                System.out.println(totalScore);
                t1.add(new Day(day, month, year), new Float(totalScore - 100));
            }
//            Iterator<Integer> iter2 = data.keySet().iterator();
//            while(iter2.hasNext()) {
//                Integer date = iter2.next();
//                Float value = data.get(date);
//                totalScore1 = totalScore1 + value;
//                String endDate = "" + date;
//               
//            }
//            Iterator<Integer> iter3 = data2.keySet().iterator();
//            while(iter3.hasNext()) {
//                Integer date = iter3.next();
//                Float value = data2.get(date);
//                totalScore2 = totalScore2 + value;
//                String endDate = "" + date;
//                int year = Integer.parseInt(endDate.substring(0, 4));
//                int month = Integer.parseInt(endDate.substring(4, 6));
//                int day = Integer.parseInt(endDate.substring(6));
//                t2.add(new Day(day, month, year), new Float(totalScore2));
//            }
//            System.out.println("totalScore1: " + totalScore1);
//            System.out.println("totalScore2: " + totalScore2);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        dataset.addSeries(t1);
        //dataset.addSeries(t2);

        return t1;
    }

    
//    public TimeSeries createWaardePortefeuilleTimeSeries(TimeSeriesCollection dataset) {
//
//        final TimeSeries t2 = new TimeSeries("");
//        final TimeSeries t3 = new TimeSeries("");
//        HandleFundData dataService = new  HandleFundData();
//        List<Dagkoers> data =  dataService.getFundRates("result", Constants.TRANSACTIONDIR);
//        Formula sma = new SMA(10, 0);
//        for (int i = 0; i < data.size(); i++) {
//        	Dagkoers k = data.get(i);
//            int year = Integer.parseInt(k.getDatum().substring(0, 4));
//            int month = Integer.parseInt(k.getDatum().substring(4, 6));
//            int day = Integer.parseInt(k.getDatum().substring(6));
//            t3.add(new Day(day, month, year), k.getClosekoers());
//            t2.add(new Day(day, month, year), sma.compute(new BigDecimal(k.getClosekoers())));
//        }
//        dataset.addSeries(t2);
//        dataset.addSeries(t3);
//        return t2;
//    }

    public XYDataset createDataset() {

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        final TimeSeries transactionsSeries = createTransactionTimeSeries(dataset);
        //final TimeSeries waardePortSeries = createWaardePortefeuilleTimeSeries(dataset);

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

        //dataset.addSeries(waardePortSeries);
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
