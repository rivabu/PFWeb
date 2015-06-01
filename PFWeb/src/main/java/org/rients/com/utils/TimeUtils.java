/**
 *
 */
package org.rients.com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;


/**
 * @author Rients
 *
 */
public class TimeUtils {
    private static final String DATE_FORMAT = "yyMMdd";
    private static final SimpleDateFormat DF = new SimpleDateFormat(DATE_FORMAT, Locale.UK);

    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
    
    /**
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static Date yymmddToDate(String dateString)
        throws ParseException {
        Date myDate = DF.parse(dateString);

        return myDate;
    }
    public static int today()
    {
      Date date = Calendar.getInstance().getTime();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      return Integer.parseInt(sdf.format(date));
    }
    
    public static int dateToInt(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(sdf.format(date));
    }
    
    public static void main(String args[]) {
        System.out.println(today());
    }
    
    public static String getNowString() {
		DateTime now = new DateTime();
		return now.toString("yyMMdd");
    }
    
    public static boolean isBeforeHour(int hour) {
    	boolean returnValue = false;
		DateTime now = new DateTime();
		int nine = hour * 60 * 60;
		if (now.getSecondOfDay() < nine) {
			returnValue = true;
		} 
		return returnValue;
    }

    public static boolean isBetween(int from, int to) {
    	return !isBeforeHour(from) && isBeforeHour(to);
    }
}
