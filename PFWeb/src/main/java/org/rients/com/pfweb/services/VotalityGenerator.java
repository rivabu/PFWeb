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
import org.rients.com.utils.NumberUtils;
import org.rients.com.utils.RSI;
import org.rients.com.votality.VotalityExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VotalityGenerator {
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
                    matrix.getColumn(i).addValue(rates.get(j).datum, NumberUtils.roundBigDecimal(value));
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
        VotalityExecutor votalityExecutor = new VotalityExecutor();
        String pathFull = Constants.KOERSENDIR + dir + Constants.SEP;
        
        Matrix matrix = votalityExecutor.fillMatrix(fundName, pathFull, true);
    
        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "groot",  matrix.getAantalDays());
        return imageResponse;
    }
    


    /**
     * @param fundData the fundData to set
     */
    public void setFundData(HandleFundData fundData) {
        this.fundData = fundData;
    }
}
