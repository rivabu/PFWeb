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
import org.rients.com.model.PFModel;
import org.rients.com.model.StrategyResult;
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
        
        Dagkoers[] waarde = new Dagkoers[koersen.size()];
        double startBedrag = 100000;
        int counter = 0;
        Dagkoers dagKoers = koersen.get(0);
        Portfolio portfolio = new Portfolio();
        boolean winter = false;
        Type typeAankoop = Type.LONG;
        if (isWinter(dagKoers)) {
            winter = true;
        } else {
            typeAankoop = Type.SHORT;
        }
        int transId = 1;
        int aantal = (int) Math.floor(startBedrag / dagKoers.closekoers);
        Transaction trans = new Transaction(fundName, new Integer(dagKoers.datum).intValue(), transId, new Double(dagKoers.closekoers).floatValue(), aantal, typeAankoop);
        double cash = startBedrag - (aantal * dagKoers.closekoers);
        //waarde[counter] = new Dagkoers(dagKoers.datum, (float) startBedrag);
        //counter ++;
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
            if (winter && isWinter(koers)) {
                // donothing
                value = (aantal * koers.closekoers) + cash;
            }
            else if (!winter && !isWinter(koers)) {
                // donothing
                if (trans != null) {
                    value = (aantal * (trans.startRate + (trans.startRate - koers.closekoers))) + cash;
                } else {
                    value = cash;
                }
            }
            else if (winter && !isWinter(koers)) {
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
                    winter = false;
                } 
            }
            else if (!winter && isWinter(koers)) {
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
                    winter = true;
                }
                
            }
            waarde[counter] = new Dagkoers(koers.datum, (float) value);
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
        FileUtils.writeToFile(filename, new ArrayList<Dagkoers>(Arrays.asList(waarde)));
        return waarde[counter - 1].closekoers;
        
    }
    
    private void calculateMaxLoss(Transaction trans, float startRate) {
        
    }
	
    private boolean isWinter(Dagkoers dagKoers) {
        int currentMonth =  Integer.parseInt(dagKoers.datum.substring(4, 6)) ;
        int day = Integer.parseInt(dagKoers.datum.substring(6));
        boolean returnValue = true;
        if (currentMonth == 1 || currentMonth == 5 || currentMonth == 6 || currentMonth == 7 || currentMonth == 8 || currentMonth == 9) { // jan +  mei - sept
            returnValue = false;
        }
        return returnValue;
    }


    public TreeMap<Integer, String> createMonthScore(String fundName, String directory) {
        List<Dagkoers> koersen = getKoersen(fundName, directory);
        TreeMap<Integer, ArrayList<Float>> map1 = new TreeMap<Integer, ArrayList<Float>>();
        for (int i = 0; i < 12; i++) {
            map1.put(i, new ArrayList<Float>());
        }
        float totalScore = 0;
        int totalMonths = 0;
        
        Dagkoers dagKoers = koersen.get(0);
        int currentMonth =  Integer.parseInt(dagKoers.datum.substring(4, 6)) - 1;
        float currentKoers = dagKoers.closekoers;
        
        for (Dagkoers koers: koersen) {
            int newMonth =  Integer.parseInt(koers.datum.substring(4, 6)) - 1;
            if (currentMonth != newMonth) {
                float score = (float) MathFunctions.procVerschil(currentKoers, koers.closekoers);
                //System.out.println("month: " + currentMonth + " score: " + score);
                ArrayList<Float> data = map1.get(currentMonth);
                data.add(score);
                currentMonth = newMonth;
                currentKoers = koers.closekoers;
                totalScore = totalScore + score;
                totalMonths ++;
            }
        }
        
        double avrPerMonth = MathFunctions.divide(totalScore, totalMonths);
        //System.out.println("avr per month: " + avrPerMonth);

        TreeMap<Integer, String> map2 = new TreeMap<Integer, String>();

        for (int i = 0; i < 12; i++) {
            ArrayList<Float> data = map1.get(i);
            float sum = 0;
            for (float score : data) {
                sum = sum + score;
            }
            float score = sum / data.size();
            map2.put(i, MathFunctions.round(score));
        }
        
        return map2;
    }


    private List<Dagkoers> getKoersen(String fundName, String directory) {
        handleFundData.setNumberOfDays(-1); // all
        List<Dagkoers> koersen = handleFundData.getFundRates(fundName, directory);
        //System.out.println("aantal: " + koersen.size());
        return koersen;
    }


}
