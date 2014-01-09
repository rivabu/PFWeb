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
import org.rients.com.utils.Graph;
import org.rients.com.utils.HistoricalVotality;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.RSI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RSIGenerator {
    //private int DAYS = 225;
    private int DAGENTERUG = 25;
    
    @Autowired
    HandleFundData fundData;

    public ImageResponse getImage(String dir, String type) {
        RSILineGraph rsiImage = new RSILineGraph();

        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix(files.size(), Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);

        for (int i = 0; i < files.size(); i++) {
        	Graph graphCalculator = null;
        	if (type.equals("RSI")) {
        		graphCalculator = new RSI(DAGENTERUG);
        	} else {
        		graphCalculator = new HistoricalVotality(DAGENTERUG);
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
        // een matrix is een array van funddataholders.
        Matrix matrix = null;
        
        Graph graphCalculator = null;
    	if (type.equals("RSI")) {
    		matrix = new Matrix(2, Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
    		graphCalculator = new RSI(DAGENTERUG);
            FundDataHolder dataHolder = new FundDataHolder("RSI", Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
            matrix.setFundData(dataHolder, 0);
    	} else {
    		matrix = new Matrix(1, Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
    		graphCalculator = new HistoricalVotality(DAGENTERUG);
            FundDataHolder dataHolderVotaliteit = new FundDataHolder("Votaliteit", Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
            matrix.setFundData(dataHolderVotaliteit, 0);
    	}
        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
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
	        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG);
	        matrix.setFundData(dataHolderKoers, 1);
	        int records = Constants.NUMBEROFDAYSTOPRINT + DAGENTERUG;
	        if (records > rates.size()) {
	            records = rates.size();
	        }
	
	        for (int j = DAGENTERUG; j < records; j++) {
	                matrix.getFundData(1).addValue(rates.get(j).datum, MathFunctions.roundToInt(rates.get(j).relativeKoers));
	        }
        }
        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "normal", Constants.NUMBEROFDAYSTOPRINT);
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
