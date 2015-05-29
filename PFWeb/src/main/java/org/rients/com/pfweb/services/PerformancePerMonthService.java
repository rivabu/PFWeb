/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.pfweb.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rients.com.boxes.Portfolio;
import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DayResult;
import org.rients.com.model.PFModel;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.MathFunctions;
import org.springframework.stereotype.Service;



@Service
public class PerformancePerMonthService {
    HandleFundData handleFundData = new HandleFundData();
    HandlePF pfHandler = new HandlePF();
    
    
    

    public float createTransactions(String fundName, String directory) {
        List<Dagkoers> koersen = getKoersen(fundName, directory);
        
        PFModel pfModel = pfHandler.createPFData(koersen, fundName, 2.5f, 2);
        //PFModel pfModel = pfHandler.createPFData(koersen, fundName, 2.5f, 2); midkap 1100000
        
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
        for (Dagkoers koers: koersen) {
            int dateInt = new Integer(koers.datum).intValue();
            if (trans != null) {
                float maxLoss = trans.determineMaxLoss(koers.closekoers);
                if (maxLoss < -10) {
                    // sell and wait
                    trans.addSellInfo(dateInt, 0, new Double(koers.closekoers).floatValue());
                    portfolio.add(trans);
                    cash = cash + aantal * koers.closekoers;
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
            waarde[counter] = new DayResult(koers.datum, (float) benchMarkfactor * koers.closekoers, (float) value);
            counter ++;
            
        }
        dagKoers = koersen.get(koersen.size() - 1);
        if (trans != null) {
            trans.addSellInfo(new Integer(dagKoers.datum).intValue(), 0, new Double(dagKoers.closekoers).floatValue());
            portfolio.add(trans);
        }
        portfolio.saveTransactions();
        //StrategyResult result = portfolio.getResultData();
        //System.out.println(result);
        
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + "result_" + fundName + ".csv";
        FileUtils.writeToFile(filename, new ArrayList<DayResult>(Arrays.asList(waarde)));
        
        createMonthScore(fundName, waarde);
        return waarde[counter - 1].getKoers();
        
    }

	
    private boolean isLongPeriod(Dagkoers dagKoers) {
        int currentMonth =  Integer.parseInt(dagKoers.datum.substring(4, 6)) ;
        int day = Integer.parseInt(dagKoers.datum.substring(6));
        boolean returnValue = true;
        if (currentMonth == 1 || currentMonth == 5 || currentMonth == 6 || currentMonth == 7 || currentMonth == 8 || currentMonth == 9) { // jan +  mei - sept
            returnValue = false;
        }
        return returnValue;
    }

    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
    
    public void createMonthScore(String fundName, DayResult[] waarde) {
        List<DayResult> scorePerMaand = new ArrayList<DayResult>();
        DayResult[] _12Months = new DayResult[12];

        for (int counter = 0; counter < 12; counter++) {
            DayResult month = new DayResult(0, 0);
            month.setMonth(theMonth(counter));
            _12Months[counter] = month;
        }
        
        DayResult dagKoers = waarde[0];
        int currentMonth =  Integer.parseInt(dagKoers.getDate().substring(4, 6)) - 1;
        float benchmarkKoers = dagKoers.getBenchMark();
        float myKoers = dagKoers.getKoers();
        
        for (DayResult koers: waarde) {
            int newMonth =  Integer.parseInt(koers.getDate().substring(4, 6)) - 1;
            if (currentMonth != newMonth) {
                float scoreBenchmark = (float) MathFunctions.procVerschil(benchmarkKoers, koers.getBenchMark());
                float scoreMyKoers = (float) MathFunctions.procVerschil(myKoers, koers.getKoers());
                
                //System.out.println("month: " + currentMonth + " score: " + score);
                DayResult monthResult = new DayResult(koers.getDate(), MathFunctions.round(scoreBenchmark, 2), MathFunctions.round(scoreMyKoers, 2));
                scorePerMaand.add(monthResult);
                currentMonth = newMonth;
                benchmarkKoers = koers.getBenchMark();
                myKoers = koers.getKoers();
                
                DayResult theMonth = _12Months[newMonth];
                theMonth.setBenchMark(MathFunctions.round(theMonth.getBenchMark() + scoreBenchmark, 2));
                theMonth.setKoers(MathFunctions.round(theMonth.getKoers() + scoreMyKoers, 2));
            }
        }
        
        
        
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + "maandscore" + fundName + ".csv";
        FileUtils.writeToFile(filename, scorePerMaand);
        filename = Constants.TRANSACTIONDIR + Constants.SEP + "12_maanden" + fundName + ".csv";
        FileUtils.writeToFile(filename, new ArrayList<DayResult>(Arrays.asList(_12Months)));

    }
    
    


    private List<Dagkoers> getKoersen(String fundName, String directory) {
        handleFundData.setNumberOfDays(-1); // all
        List<Dagkoers> koersen = handleFundData.getFundRates(fundName, directory);
        //System.out.println("aantal: " + koersen.size());
        return koersen;
    }


}
