package org.rients.com.pfweb.performancepermonth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rients.com.boxes.Portfolio;
import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DayResult;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.RSI;

public class RSIModel implements ModelInterface {
    HandleFundData handleFundData = new HandleFundData();
    Reporting reporting = new Reporting();


    public float process(String directory, String fundName, Map<String, Object> input, boolean save) {
        
        int RSI_SHORT = (Integer) input.get("rsiValueShort");
        int RSI_MIDDLE = (Integer) input.get("rsiValueMiddle");
        
        Formula rsiCalculatorShort = new RSI(RSI_SHORT);
        Formula rsiCalculatorMiddle = new RSI(RSI_MIDDLE);
        
        List<Dagkoers> koersen = handleFundData.getAllFundRates(fundName, directory);
        DayResult[] waarde = new DayResult[koersen.size()];
        double startBedrag = 100000;
        int counter = 0;
        Dagkoers eersteKoers = koersen.get(0);
        Dagkoers koers = koersen.get(0);
        double benchMarkfactor = 100000 / eersteKoers.closekoers;
        Portfolio portfolio = new Portfolio();

        int transId = 1;
        int aantal = 0;
        double cash = startBedrag;
        double value = 0d;
        Transaction trans = null;
        int days = koersen.size();
        Boolean isLong = false;
        boolean firstTime = true;
        for (int j = 0; j < days; j++) {
            koers = koersen.get(j);
            int datum = new Integer(koers.datum).intValue();
            if (isLong || trans == null) {
                value = (aantal * koers.closekoers) + cash;
            } else {
                value = (aantal * (trans.startRate + (trans.startRate - koers.closekoers))) + cash;
            }
            waarde[counter] = new DayResult(koers.datum, (float) benchMarkfactor * koers.closekoers, (float) value, 0, 0);
            counter++;

            BigDecimal rsiShort = rsiCalculatorShort.compute(new BigDecimal(koersen.get(j).closekoers));
            BigDecimal rsiMiddle = rsiCalculatorMiddle.compute(new BigDecimal(koersen.get(j).closekoers));
            if (j >= RSI_MIDDLE ) {
                int dateInt = new Integer(koers.datum).intValue();

                if (rsiShort.floatValue() >= rsiMiddle.floatValue() && (!isLong || firstTime)) {
                    // buy signal
                    if (!firstTime) {
                        trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                        portfolio.add(trans);
                    }
                    aantal = (int) Math.floor(value / koers.closekoers);
                    cash = value - (aantal * koers.closekoers);
                    transId++;

                    trans = new Transaction(fundName, new Integer(koers.datum).intValue(), transId, new Double(koers.closekoers).floatValue(), aantal, Type.LONG);
                    isLong = true;
                    firstTime = false;
                } 
                if (rsiShort.floatValue() < rsiMiddle.floatValue() && (isLong || firstTime)) {
                    
                    if (!firstTime) {
                        trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                        portfolio.add(trans);
                    }
                    
                    aantal = (int) Math.floor(value / koers.closekoers);
                    cash = value - (aantal * koers.closekoers);
                    transId++;
                    trans = new Transaction(fundName, new Integer(koers.datum).intValue(), transId, new Double(koers.closekoers).floatValue(), aantal, Type.SHORT);
                    firstTime = false;
                    isLong=false;
                }
            }

        }
        koers = koersen.get(koersen.size() - 1);
        if (trans != null) {
            trans.addSellInfo(new Integer(koers.datum).intValue(), 0, new Double(koers.closekoers).floatValue());
            portfolio.add(trans);
        }
        if (save) {
            String filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + "_trans.csv";
            portfolio.saveTransactions(filename);
            
            filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + "_result.csv";
            FileUtils.writeToFile(filename, new ArrayList<DayResult>(Arrays.asList(waarde)));
            
        }
        float result = reporting.createMonthScore(fundName, waarde, save);            

        return result;

    }

}
