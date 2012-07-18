package rients.trading.services;

import java.math.BigDecimal;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.indexpredictor.FundDataHolder;
import org.rients.com.indexpredictor.Matrix;
import org.rients.com.model.ImageResponse;

import rients.trading.download.model.Dagkoers;
import rients.trading.graph.RSILineGraph;
import rients.trading.utils.FileUtils;
import rients.trading.utils.MathFunctions;
import rients.trading.utils.RSI;

public class RSIGenerator {
    private int DAYS = 225;
    private int DAGENTERUG = 25;
    
    HandleFundData fundData = new HandleFundData();

    public ImageResponse getImage(String dir) {
        RSILineGraph rsiImage = new RSILineGraph();

        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix(files.size(), DAYS);

        for (int i = 0; i < files.size(); i++) {
            RSI rsiCalculator = new RSI(DAGENTERUG);
            FundDataHolder dataHolder = new FundDataHolder(files.get(i), DAYS);
            fundData.setNumberOfDays(DAYS);
            List<Dagkoers> rates = fundData.getFundRates(files.get(i), dirFull);
            if (i == 0) {
                matrix.fillDates(rates);
            }
            matrix.setFundData(dataHolder, i);
            int days = rates.size();
            for (int j = 0; j < days; j++) {
                BigDecimal rsi = rsiCalculator.compute(new BigDecimal(rates.get(j).closekoers));
                if (j >= DAGENTERUG) {
                    matrix.getFundData(i).addValue(rates.get(j).datum, rsi.intValue());
                }
            }
        }

        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "large");
        return imageResponse;
    }

    public ImageResponse getImage(String dir, String fundName) {
        RSILineGraph rsiImage = new RSILineGraph();

        String pathFull = Constants.KOERSENDIR + dir + Constants.SEP;
        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix(2, DAYS);
        RSI rsiCalculator = new RSI(DAGENTERUG);
        FundDataHolder dataHolder = new FundDataHolder("RSI", DAYS);
        fundData.setNumberOfDays(DAYS);
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
        matrix.fillDates(rates);
        matrix.setFundData(dataHolder, 0);
        int days = rates.size();
        for (int j = 0; j < days; j++) {
            BigDecimal rsi = rsiCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            if (j >= DAGENTERUG) {
                matrix.getFundData(0).addValue(rates.get(j).datum, rsi.intValue());
            }
        }
        calucateRelativeKoersen(rates);
        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", DAYS);
        matrix.setFundData(dataHolderKoers, 1);
        for (int j = DAGENTERUG; j < DAYS; j++) {
                matrix.getFundData(1).addValue(rates.get(j).datum, MathFunctions.roundToInt(rates.get(j).relativeKoers));
        }
        ImageResponse imageResponse = rsiImage.generateRSIGraph(matrix, "small");
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
        
        for (int j = DAGENTERUG; j < DAYS; j++) {
            float koers = rates.get(j).closekoers;
            if (koers < minValue) {
                minValue = koers;
            }
            if (koers > maxValue) {
                maxValue = koers;
            }
        }
        float factor = (maxValue - minValue) / 100;
        for (int j = DAGENTERUG; j < DAYS; j++) {
            Dagkoers dagkoers = rates.get(j);
            dagkoers.relativeKoers = (dagkoers.closekoers - minValue) / factor;
        }
    }
}
