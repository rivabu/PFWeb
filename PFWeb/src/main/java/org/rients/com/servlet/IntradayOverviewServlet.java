/*
 * 
 */
package org.rients.com.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.rients.com.constants.Constants;

import rients.trading.utils.FileUtils;
import rients.trading.utils.PropertiesUtils;

/**
 * Servlet implementation class Overview.
 */
public class IntradayOverviewServlet extends HttpServlet {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    private static int numberOfDays = 100;

    /**
     * Instantiates a new overview servlet.
     * 
     * @see HttpServlet#HttpServlet()
     */
    public IntradayOverviewServlet() {
        super();
    }

    /**
     * Do get.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            
            // maak een lijst van dagen: dayofweek, date, hoogste laagste, open, slot, vorige slot
            // deze waarden halen we uit een properties file
            // sorted on date
            // we gaan max 3 weken terug
            
            // 1 = maandag, 5 = vrijdag
            
            // Set the attribute and Forward to hello.jsp
            List<String> files = FileUtils.getFiles(Constants.INTRADAY_KOERSENDIR, "csv", false);

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
            int days = files.size();
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
            request.setAttribute("matrix", matrix);
            request.setAttribute("dir", "intraday");
            getServletConfig().getServletContext().getRequestDispatcher("/intradayoverview.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the file properties.
     * 
     * @param files
     *            the files
     * @return the file properties
     */
    private List<Properties> getFileProperties(List<String> files) {
        List<Properties> props = new ArrayList<Properties>();
        for (String file : files) {
            String fileName = file + Constants.PROPERTIES;
            Properties prop = PropertiesUtils.getPropertiesFromClasspath(fileName);
            if (!prop.containsKey("graphParameters")) {
                String[][] turningPoints = { { "1", "0.75" }, { "2", "1" }, { "1", "1" }, { "1", "1.5" }, { "1", "2" } };
                prop.put("graphParameters", turningPoints);
            } else {
                prop.put("graphParameters", convertGraphParameters(prop.getProperty("graphParameters")));
            }
            if (prop.containsKey(Constants.LAST_TEN_DAYS)) {
                prop.put("lastTenDays", convertLastTenDays(prop.getProperty(Constants.LAST_TEN_DAYS)));
            }

            props.add(prop);

        }
        return props;
    }

    private String[][] convertGraphParameters(String line) {
        String[][] graphParameters = new String[5][2];
        StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), ":");
        if (stringtokenizer.countTokens() == 5) {
            for (int i = 0; i < 5; i++) {
                String token = stringtokenizer.nextToken().trim();
                int indexDubbelePunt = token.indexOf(",");
                String turningPoint = token.substring(0, indexDubbelePunt);
                String stepSize = token.substring(indexDubbelePunt + 1);
                graphParameters[i][0] = turningPoint;
                graphParameters[i][1] = stepSize;
            }
        }
        return graphParameters;
    }

    private String[][] convertLastTenDays(String line) {
        String[][] lastTenDays = new String[10][3];
        StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), "\\:");
        int aantal = stringtokenizer.countTokens();
        double lowest = 10000000000000d;
        double highest = 0d;
        int highestNumber = 0;
        int lowestNumber = 0;
        for (int i = 0; i < aantal ; i++) {
            String token = stringtokenizer.nextToken().trim();
            int indexDubbelePunt = token.indexOf(",");
            String datum = token.substring(0, indexDubbelePunt);
            String koers = token.substring(indexDubbelePunt + 1);
            if (Double.parseDouble(koers) >= highest) {
                highest = Double.parseDouble(koers);
                highestNumber = i;
            }
            if (Double.parseDouble(koers) <= lowest) {
                lowest = Double.parseDouble(koers);
                lowestNumber = i;
            }
            lastTenDays[i][0] = datum;
            lastTenDays[i][1] = koers;
            lastTenDays[i][2] = "normal";
        }
        lastTenDays[highestNumber][2] = "highest";
        lastTenDays[lowestNumber][2] = "lowest";
        
        return lastTenDays;
    }

}
