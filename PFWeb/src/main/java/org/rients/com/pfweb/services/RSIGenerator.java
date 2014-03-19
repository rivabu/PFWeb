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
import org.rients.com.utils.SMA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RSIGenerator {
    //private int DAYS = 225;
    private int DAGENTERUG = 50;
    
    @Autowired
    HandleFundData fundData;

    public ImageResponse getImage(String dir, String type) {
        RSILineGraph rsiImage = new RSILineGraph();

        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix(files.size(), Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);

        for (int i = 0; i < files.size(); i++) {
        	Formula graphCalculator = null;
        	Formula sma = null;
        	if (type.equals("RSI")) {
        		graphCalculator = new RSI(DAGENTERUG);
        	} else {
        		graphCalculator = new HistoricalVotality(DAGENTERUG);
        		sma = new SMA(DAGENTERUG);
        		
        	}
            FundDataHolder dataHolder = new FundDataHolder(files.get(i), Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
            fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
            List<Dagkoers> rates = fundData.getFundRates(files.get(i), dirFull);
            if (i == 0) {
                matrix.fillDates(rates);
            }
            matrix.setFundData(dataHolder, i);
            int days = rates.size();
            for (int j = 0; j < days; j++) {
                BigDecimal value = graphCalculator.compute(new BigDecimal(rates.get(j).closekoers));
                if (type.equals("votality")) {
                	value = sma.compute(value);
                }
                if (j >= DAGENTERUG) {
                    matrix.getFundData(i).addValue(rates.get(j).datum, value.intValue());
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
    public ImageResponse getImage(String dir, String type, String fundName) {
        RSILineGraph rsiImage = new RSILineGraph();

        String pathFull = Constants.KOERSENDIR + dir + Constants.SEP;
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
        int aantalDagenTonen = Math.min(rates.size() - DAGENTERUG, Constants.NUMBEROFDAYSTOPRINT);
        // een matrix is een array van funddataholders.
        Matrix matrix = null;
        
        Formula graphCalculator = null;
    	if (type.equals("RSI")) {
    		matrix = new Matrix(2, aantalDagenTonen + DAGENTERUG);
    		graphCalculator = new RSI(DAGENTERUG);
            FundDataHolder dataHolder = new FundDataHolder("RSI", aantalDagenTonen + DAGENTERUG);
            matrix.setFundData(dataHolder, 0);
    	} else {
    		matrix = new Matrix(1, aantalDagenTonen + DAGENTERUG);
    		graphCalculator = new HistoricalVotality(DAGENTERUG);
            FundDataHolder dataHolderVotaliteit = new FundDataHolder("Votaliteit", aantalDagenTonen + DAGENTERUG);
            matrix.setFundData(dataHolderVotaliteit, 0);
    	}
        fundData.setNumberOfDays(aantalDagenTonen + DAGENTERUG);
        matrix.fillDates(rates);
        
        int days = rates.size();
        for (int j = 0; j < days; j++) {
            BigDecimal value = graphCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            if (j >= DAGENTERUG) {
                matrix.getFundData(0).addValue(rates.get(j).datum, value.intValue());
            }
        }
        if (type.equals("RSI")) {
	        calucateRelativeKoersen(rates);
	        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", aantalDagenTonen + DAGENTERUG);
	        matrix.setFundData(dataHolderKoers, 1);
	        int records = aantalDagenTonen + DAGENTERUG;
	        if (records > rates.size()) {
	            records = rates.size();
	        }
	
	        for (int j = DAGENTERUG; j < records; j++) {
	                matrix.getFundData(1).addValue(rates.get(j).datum, MathFunctions.roundToInt(rates.get(j).relativeKoers));
	        }
        }
        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "normaal", aantalDagenTonen);
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
