/*
 * 
 */
package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.rients.com.constants.Constants;
import org.rients.com.pfweb.utils.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping()
/**
 * Servlet implementation class Overview.
 */
public class IntradayOverviewServlet  {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    private static int numberOfDays = 100;



    @RequestMapping(value = "/Intraday", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            
            // maak een lijst van dagen: dayofweek, date, hoogste laagste, open, slot, vorige slot
            // deze waarden halen we uit een properties file
            // sorted on date
            // we gaan max 3 weken terug
            
            // 1 = maandag, 5 = vrijdag
            List<String> files = FileUtils.getFiles(Constants.INTRADAY_KOERSENDIR, "csv", false);
            int days = files.size();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
            Map<Integer, Map<Integer, String>> matrix = handIntradayData(files, formatter, days);
            request.setAttribute("matrix", matrix);
            request.setAttribute("dir", "intraday");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "intradayoverview";
    }



    private Map<Integer, Map<Integer, String>> handIntradayData(List<String> files, DateTimeFormatter formatter, int days) {
        Map<Integer, Map<Integer, String>> matrix = new HashMap<Integer, Map<Integer, String>>();
        int weekNumberOld = 0;
        Map<Integer, String> week = null;
        int weekNumber = 0;
        if (days < numberOfDays) {
            numberOfDays = days;
        }
        for (int i = days - numberOfDays; i < days; i++) {
            String file = files.get(i);
            DateTime dt = formatter.parseDateTime(file);
            weekNumber = dt.getWeekOfWeekyear();
            if (weekNumber != weekNumberOld) {
                if (week != null) {
                    matrix.put(-weekNumberOld, week);
                }
                weekNumberOld = weekNumber;
                week = new HashMap<Integer, String>();
                week.put(1, "");
                week.put(2, "");
                week.put(3, "");
                week.put(4, "");
                week.put(5, "");
            }
            week.put(dt.getDayOfWeek(), file);
            
            System.out.println("file: " + file + " dayofweek: " + dt.getDayOfWeek() + dt.getWeekOfWeekyear());
            
        }
        if (!matrix.containsKey(weekNumber)) {
            matrix.put(-weekNumber, week);
        }
        return matrix;
    }

}
