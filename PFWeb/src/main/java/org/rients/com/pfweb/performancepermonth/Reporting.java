package org.rients.com.pfweb.performancepermonth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.model.DayResult;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.TimeUtils;

public class Reporting {

    public float createMonthScore(String fundName, DayResult[] waarde) {
        List<DayResult> scorePerMaand = new ArrayList<DayResult>();
        DayResult[] _12Months = new DayResult[12];

        for (int counter = 0; counter < 12; counter++) {
            DayResult month = new DayResult(0, 0);
            month.setMonth(TimeUtils.theMonth(counter));
            _12Months[counter] = month;
        }
        float sumMonths = 0;
        
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
                sumMonths = sumMonths + (MathFunctions.round(scoreMyKoers, 2) - MathFunctions.round(scoreBenchmark, 2));
                currentMonth = newMonth;
                benchmarkKoers = koers.getBenchMark();
                myKoers = koers.getKoers();
                
                DayResult theMonth = _12Months[newMonth];
                theMonth.setBenchMark(MathFunctions.round(theMonth.getBenchMark() + scoreBenchmark, 2));
                theMonth.setKoers(MathFunctions.round(theMonth.getKoers() + scoreMyKoers, 2));
            }
        }
        
        
        
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + "_maandscore.csv";
        FileUtils.writeToFile(filename, scorePerMaand);
        filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + "_12_maanden.csv";
        FileUtils.writeToFile(filename, new ArrayList<DayResult>(Arrays.asList(_12Months)));
        
        return sumMonths;
    }

}
