/*
 * Created on Aug 13, 2007
 * 
 * Copyright (c) K.L.M. Koninklijke Luchtvaart Maatschappij N.V. All rights
 * reserved.
 */
package rients.trading.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Helper class for the conversion of Calendar object to xml schema compatible date strings and vice versa.
 */
public final class DateParser {

    private static final String FORMAT_DATE = "yyyy-MM-dd";
    private static final String FORMAT_DATE_WITHOUT_DAY = "MMyy";
    private static final String FORMAT_TIME_WITHOUT_MILLIS = "HH:mm:ss";
    private static final String FORMAT_DATETIME = FORMAT_DATE + "'T'" + FORMAT_TIME_WITHOUT_MILLIS;
    private static final DatatypeFactory DATATYPE_FACTORY;
    private static final String FORMAT_DATE_G_YEAR_MONTH = "yyyy-MM";

    static {
        try {
            DATATYPE_FACTORY = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prevent instantiation of this class.
     */
    private DateParser() {
    }

    /**
     * Parse the given xs:date compatible string representation of a date to a calendar object. The input Date format is "yyyy-MM-dd".
     * 
     * @param value xs:date compatible string representation of a date ("yyyy-MM-dd")
     * @return Calendar object representing the given date
     */
    public static Calendar parseDate(String value) {
        Calendar cal = null;

        if (value != null) {
            cal = Calendar.getInstance();
            try {
                cal.setTime(getDateFormat().parse(value));
                cal.setLenient(false);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    /**
     * Parse the given xs:date compatible string representation of a date to a calendar object. The input Date format is "yMMyy".
     * 
     * @param value xs:date compatible string representation of a date ("MMyy")
     * @return Calendar object representing the given date
     */
    public static Calendar parseDateWithoutDay(String value) {
        Calendar cal = null;

        if (value != null) {
            cal = Calendar.getInstance();
            try {
                cal.setTime(getDateFormatWithoutDay().parse(value));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    /**
     * Print the given calendar object to an xs:date compatible string representation of that date.
     * 
     * @param value Calendar object to print
     * @return The string representation of the given date
     */
    public static String printDate(final Calendar value) {
        String date = null;
        if (value != null) {
            // Defect fix:4610.Added mapping for TimeZone in SimpleDateFormat
            date = getDateFormat(value.getTimeZone(), FORMAT_DATE).format(value.getTime());
        }
        return date;
    }

    /**
     * Print the given calendar object to an xs:date compatible string representation of that date.
     * 
     * @param value Calendar object to print
     * @param formatDate set the specific format for the date
     * @return The string representation of the given date
     */
    public static String printDate(final Calendar value, String formatDate) {
        String date = null;
        if (value != null) {
            date = getDateFormat(value.getTimeZone(), formatDate).format(value.getTime());
        }
        return date;
    }
    
    /**
     * Print the given calendar object to an xs:gYearMonth compatible string representation of that date.
     * 
     * @param value Calendar object to print
     * @return The string representation of the given date
     */
    public static String printDateWithoutDay(final Calendar value) {
        String date = null;
        if (value != null) {
            date = getDateFormatWithoutDay().format(value.getTime());
        }
        return date;
    }

    /**
     * Print the given calendar object to an xs:gYearMonth compatible string representation of that date.
     * 
     * @param value Calendar object to print
     * @return The string representation of the given date
     */
    public static String printDategYearMonth(final Calendar value) {
        String date = null;
        if (value != null) {
            date = getDateFormatgYearMonth().format(value.getTime());
        }
        return date;
    }

    /**
     * Parse the given xs:time compatible string representation of a time to a calendar object. The year, month and date fields of the calendar are set to 1970, 01 and 01, respectively. The input time format is "HH:mm:ss".
     * 
     * @param value xs:time compatible string representation of a time ("HH:mm:ss")
     * @return Calendar object representing the given time
     */
    public static Calendar parseTime(String value) {
        Calendar cal = null;

        if (value != null) {
            cal = Calendar.getInstance();
            cal.setLenient(false);
            try {
                cal.setTime(getTimeFormat().parse(value));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    /**
     * Print the given calendar object to an xs:time compatible string representation of that time. The year, month and date fields of the calendar are ignored.
     * 
     * @param value Calendar object to print
     * @return The string representation of the given time
     */
    public static String printTime(Calendar value) {
        String date = null;
        if (value != null) {
            date = getTimeFormat().format(value.getTime());
        }
        return date;
    }

    /**
     * Print the given calendar object to an xs:time compatible string representation of that time. The year, month and date fields of the calendar are ignored.
     * 
     * @param value Calendar object to print
     * @param pattern String time format pattern
     * @return The string representation of the given time
     */
    public static String printTime(Calendar value, String pattern) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        String date = null;
        if (value != null) {
            date = timeFormat.format(value.getTime());
        }
        return date;
    }

    /**
     * Parse the given xs:dateTime compatible string representation of a date and time to a calendar object.
     * 
     * @param value xs:dateTime compatible string representation of a date and time
     * @return Calendar object representing the given date and time
     */
    public static Calendar parseDateTime(String value) {
        Calendar cal = null;

        if (value != null) {
            cal = Calendar.getInstance();
            cal.setLenient(false);
            try {
                cal.setTime(getDateTimeFormat().parse(value));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return cal;
    }

    /**
     * Print the given calendar object to an xs:dateTime compatible string representation of that date and time.
     * 
     * @param value Calendar object to print
     * @return The string representation of the given date and time
     */
    public static String printDateTime(Calendar value) {
        String date = null;
        if (value != null) {
            date = getDateTimeFormat().format(value.getTime());
        }
        return date;
    }

    /**
     * Convert a java.util.Calendar into an XMLGregorianCalendar Date representation. (All time related fields are disabled)
     * 
     * @param calendar java.util.Calendar
     * @return XMLGregorianCalendar (Date only)
     */
    public static XMLGregorianCalendar toXMLDate(Calendar calendar) {
        return DATATYPE_FACTORY.newXMLGregorianCalendar(printDate(calendar));
    }

    /**
     * Convert a java.util.Calendar into an XMLGregorianCalendar Date representation. (All time related fields are disabled)
     * 
     * @param calendar java.util.Calendar
     * @return XMLGregorianCalendar (Date only)
     */
    public static XMLGregorianCalendar toXMLgYearMonth(Calendar calendar) {
        return DATATYPE_FACTORY.newXMLGregorianCalendar(printDategYearMonth(calendar));
    }

    /**
     * Convert a java.util.Calendar into an XMLGregorianCalendar Time representation. (All date related fields are disabled)
     * 
     * @param calendar java.util.Calendar
     * @return XMLGregorianCalendar (Time only)
     */
    public static XMLGregorianCalendar toXMLTime(Calendar calendar) {
        return DATATYPE_FACTORY.newXMLGregorianCalendar(printTime(calendar));
    }

    /**
     * Convert a java.util.Calendar into an XMLGregorianCalendar DateTime representation.
     * 
     * @param calendar java.util.Calendar
     * @return XMLGregorianCalendar DateTime
     */
    public static XMLGregorianCalendar toXMLDateTime(Calendar calendar) {
        XMLGregorianCalendar xmlCalendar = DATATYPE_FACTORY.newXMLGregorianCalendar((GregorianCalendar) calendar);
        xmlCalendar.setTimezone(Integer.MIN_VALUE);
        xmlCalendar.setFractionalSecond(null);
        return xmlCalendar;
    }

    /**
     * Create an empty XMLGregorianCalendar implementation
     * 
     * @return a new instance of an {@link XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar createGregorianCalendar() {
        return DATATYPE_FACTORY.newXMLGregorianCalendar();
    }

    /**
     * Method for creating SimpleDateFormat based on TimeZone
     * 
     * @param timeZone TimeZone
     * @return SimpleDateFormat object
     */
    private static SimpleDateFormat getDateFormat(TimeZone timeZone, String formatdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatdate, Locale.ENGLISH);
        dateFormat.setLenient(false);
        dateFormat.setTimeZone(timeZone);
        return dateFormat;
    }
    
    private static SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE, Locale.ENGLISH);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    private static SimpleDateFormat getDateFormatWithoutDay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_WITHOUT_DAY, Locale.ENGLISH);
        dateFormat.setLenient(false);

        return dateFormat;
    }

    private static SimpleDateFormat getDateFormatgYearMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_G_YEAR_MONTH, Locale.ENGLISH);
        dateFormat.setLenient(false);

        return dateFormat;
    }

    private static SimpleDateFormat getTimeFormat() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(FORMAT_TIME_WITHOUT_MILLIS);
        timeFormat.setLenient(false);

        return timeFormat;
    }

    private static SimpleDateFormat getDateTimeFormat() {
        SimpleDateFormat timeFormat = new SimpleDateFormat(FORMAT_DATETIME);
        timeFormat.setLenient(false);

        return timeFormat;
    }

}
