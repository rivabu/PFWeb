/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.pfweb.performancepermonth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.rients.com.boxes.Portfolio;
import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DayResult;
import org.rients.com.model.PFModel;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.pfweb.services.HandlePF;
import org.rients.com.utils.FileUtils;
import org.springframework.stereotype.Service;



@Service
public class PerformancePerMonthModel implements ModelInterface {
    HandleFundData handleFundData = new HandleFundData();
    HandlePF pfHandler = new HandlePF();
    Reporting reporting = new Reporting();
    List<Integer> longMonths;
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 2.5f, 2); 1048
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 2f, 2); 1048
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 1.2f, 2); 1116
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 1.4f, 2); 1160
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 1.3f, 2); 1190
    //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 1.2f, 1); 1181


    @SuppressWarnings("unchecked")
    public float process(String directory, String fundName, Map<String, Object> input, boolean save) {
        longMonths = (List<Integer>) input.get("LongMonths");
        List<Dagkoers> koersen = handleFundData.getAllFundRates(fundName, directory);
        
        
        PFModel pfModel = pfHandler.createPFData(koersen, fundName, (Float) input.get("StepSize"), (Integer) input.get("TurningPoint"));
        
        DayResult[] waarde = new DayResult[koersen.size()];
        double startBedrag = 100000;
        int counter = 0;
        Dagkoers dagKoers = koersen.get(0);
        double benchMarkfactor = 100000 / dagKoers.closekoers;
        Portfolio portfolio = new Portfolio();
        boolean longPeriod = false;
        Type typeAankoop = Type.LONG;
        if (isLongPeriod(dagKoers)) {
            longPeriod = true;
        } else {
            typeAankoop = Type.SHORT;
        }
        int transId = 1;
        int aantal = (int) Math.floor(startBedrag / dagKoers.closekoers);
        Transaction trans = new Transaction(fundName, new Integer(dagKoers.datum).intValue(), transId, new Double(dagKoers.closekoers).floatValue(), aantal, typeAankoop);
        double cash = startBedrag - (aantal * dagKoers.closekoers);
        double value = 0d;
        int stopLoss = (Integer) input.get("StopLoss");
        for (Dagkoers koers: koersen) {
            int dateInt = new Integer(koers.datum).intValue();
            if (trans != null) {
                float maxLoss = trans.determineMaxLoss(koers.closekoers);
                if (maxLoss < stopLoss) {
                    // sell and wait
                    trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                    portfolio.add(trans);
                    if (trans.getType() == Type.LONG) {
                        cash = cash + aantal * koers.closekoers;
                    } else {
                        cash = cash + (aantal * (trans.getStartRate() + trans.getStartRate() - koers.closekoers));
                    }
                    aantal = 0;
                    trans = null;
                }
            }
            if (longPeriod && isLongPeriod(koers)) {
                // donothing
                value = (aantal * koers.closekoers) + cash;
            }
            else if (!longPeriod && !isLongPeriod(koers)) {
                // donothing
                if (trans != null) {
                    value = (aantal * (trans.startRate + (trans.startRate - koers.closekoers))) + cash;
                } else {
                    value = cash;
                }
            }
            else if (longPeriod && !isLongPeriod(koers)) {
                value = (aantal * koers.closekoers) + cash;
                if (!pfModel.isPlusOnDate(dateInt)) {
                    
                    if (trans != null) {
                        // sell winter
                        trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                        portfolio.add(trans);
                    }
                    // buy summer
                    aantal = (int) Math.floor(value / koers.closekoers);
                    cash = value - (aantal * koers.closekoers);
                    transId++;
                    trans = new Transaction(fundName, dateInt, transId, new Double(koers.closekoers).floatValue(), aantal, Type.SHORT);
                    longPeriod = false;
                } 
            }
            else if (!longPeriod && isLongPeriod(koers)) {
                if (trans != null) {
                    value = (aantal * (trans.startRate + (trans.startRate - koers.closekoers))) + cash;
                } else {
                    value = cash;
                }
                if (pfModel.isPlusOnDate(dateInt)) {
                    if (trans != null) {
                        // sell summer
                        trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                        portfolio.add(trans);
                    }
                    // buy winter
                    aantal = (int) Math.floor(value / koers.closekoers);
                    cash = value - (aantal * koers.closekoers);
                    transId++;
                    trans = new Transaction(fundName, dateInt, transId, new Double(koers.closekoers).floatValue(), aantal, Type.LONG);
                    longPeriod = true;
                }
                
            }
            waarde[counter] = new DayResult(koers.datum, (float) benchMarkfactor * koers.closekoers, (float) value, 0, 0);
            counter ++;
            
        }
        dagKoers = koersen.get(koersen.size() - 1);
        if (trans != null) {
            trans.addSellInfo(new Integer(dagKoers.datum).intValue(), 0, new Double(dagKoers.closekoers).floatValue());
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

	
    private boolean isLongPeriod(Dagkoers dagKoers) {
        int currentMonth =  Integer.parseInt(dagKoers.datum.substring(4, 6)) ;
        int day = Integer.parseInt(dagKoers.datum.substring(6));
        boolean returnValue = true;
        if (longMonths.contains(currentMonth))
        if (currentMonth == 1 || currentMonth == 5 || currentMonth == 6 || currentMonth == 7 || currentMonth == 8 || currentMonth == 9) { // jan +  mei - sept
            returnValue = false;
        }
        return returnValue;
    }



}
