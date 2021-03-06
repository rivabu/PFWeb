/**
 *
 */
package rients.trading.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * @author Rients
 *
 */
public class TimeUtils {
    private static final String DATE_FORMAT = "yyMMdd";
    private static final SimpleDateFormat DF = new SimpleDateFormat(DATE_FORMAT, Locale.UK);

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
    public static String today()
    {
      Date date = Calendar.getInstance().getTime();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      return sdf.format(date);
    }
    public static void main(String args[]) {
        System.out.println(today());
    }
}
