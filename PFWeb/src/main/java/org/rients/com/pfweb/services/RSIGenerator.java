package org.rients.com.pfweb.services;

import java.math.BigDecimal;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.graph.RSILineGraph;
import org.rients.com.matrix.dataholder.FundDataHolder;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.ImageResponse;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.HistoricalVotality;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.RSI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RSIGenerator {
    //private int DAYS = 225;
    private int DAGENTERUG = 50;
    
    @Autowired
    HandleFundData fundData;

    public ImageResponse getImage(String dir) {
        RSILineGraph rsiImage = new RSILineGraph();

        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix("RSIMatrix", files.size(), Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);

        for (int i = 0; i < files.size(); i++) {
        	Formula graphCalculator = new RSI(DAGENTERUG);
            FundDataHolder dataHolder = new FundDataHolder(files.get(i), Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG, true);
            fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
            List<Dagkoers> rates = fundData.getFundRates(files.get(i), dirFull);
            if (i == 0) {
                matrix.fillDates(rates);
            }
            matrix.setColumn(dataHolder, i);
            int days = rates.size();
            for (int j = 0; j < days; j++) {
                BigDecimal value = graphCalculator.compute(new BigDecimal(rates.get(j).closekoers));
                if (j >= DAGENTERUG) {
                    matrix.getColumn(i).addValue(rates.get(j).datum, value.intValue());
                }
            }
        }

        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "large",  Constants.NUMBEROFDAYSTOPRINT);
        return imageResponse;
    }

    /*
     * DAYS is 225
     * DAGENTERUG = 25
     */
    public ImageResponse getImage(String dir, String fundName) {
        RSILineGraph rsiImage = new RSILineGraph();
        
        
        String pathFull = Constants.KOERSENDIR + dir + Constants.SEP;
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
        
        //double avr = 0;
//        Formula avrCalculator = new HistoricalVotality(DAGENTERUG, 0);
//        for (int j = 0; j < rates.size(); j++) {
//        	avrCalculator.compute(new BigDecimal(rates.get(j).closekoers));
//        }
        //avr = 30;
        int aantalDagenTonen = Math.min(rates.size(), Constants.NUMBEROFDAYSTOPRINT);
        // een matrix is een array van funddataholders.
        Matrix matrix = null;
        
        //Formula graphCalculator = null;
    		matrix = new Matrix(fundName, 1, aantalDagenTonen);
    		//graphCalculator = new RSI(DAGENTERUG);
            //FundDataHolder dataHolder = new FundDataHolder("RSI", aantalDagenTonen);
            //matrix.setColumn(dataHolder, 0);
	        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", aantalDagenTonen, true);
	        matrix.setColumn(dataHolderKoers, 0);
        fundData.setNumberOfDays(aantalDagenTonen );
        matrix.fillDates(rates);
        
        //int days = rates.size();
        //for (int j = 0; j < days; j++) {
            //BigDecimal value = graphCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            //matrix.getFundData(0).addValue(rates.get(j).datum, value.intValue());
            //matrix.getColumn(0).addValue(rates.get(j).datum, avr);
        //}
        calucateRelativeKoersen(rates);
        int records = aantalDagenTonen;
        if (records > rates.size()) {
            records = rates.size();
        }

        for (int j = 0; j < records; j++) {
                matrix.getColumn(0).addValue(rates.get(j).datum, new Double(rates.get(j).relativeKoers).doubleValue());
        }
        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "groot", aantalDagenTonen);
        return imageResponse;
    }
    
    /*
     * 60 -> 600
     * 
     * 0 -> 540
     * 
     * factor = 5.4
     * 
     * formule = koers.relative = (koers - minValue) / factor
     */
    
    private void calucateRelativeKoersen(List<Dagkoers> rates) {
        float minValue = 100000000f;
        float maxValue = 0f;
        int records = Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG;
        if (Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG > rates.size()) {
            records = rates.size();
        }
        
        for (int j = DAGENTERUG; j < records; j++) {
            float koers = rates.get(j).closekoers;
            if (koers < minValue) {
                minValue = koers;
            }
            if (koers > maxValue) {
                maxValue = koers;
            }
        }
        float factor = (maxValue - minValue) / 100;
        for (int j = DAGENTERUG; j < records; j++) {
            Dagkoers dagkoers = rates.get(j);
            dagkoers.relativeKoers = (dagkoers.closekoers - minValue) / factor;
        }
    }

    /**
     * @param fundData the fundData to set
     */
    public void setFundData(HandleFundData fundData) {
        this.fundData = fundData;
    }
}
