/**
 *
 */
package org.rients.com.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.rients.com.model.Dagkoers;



/**
 * @author Rients
 *
 */
public class MathFunctions {
    /*
    
        public static String round(String koers) {
        double koersD = new Double(koers).doubleValue() * 100;
        int koersL = (int) Math.round(koersD);
    
        String temp =  ((double) koersL / 100) + "";
        if(temp.length() > 6) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>"+ temp);
        }
        return temp;
    }
     */
    
    
    public static String round(float rate) {
        if (rate == 0) {
            return "0";
        }
        double rateD = (new Double(rate).doubleValue() * 100) + 0.5;
        double rateL = Math.round(rateD);
        return (rateL / 100) + "";
    }
    
    public static int roundToInt(float rate) {
        double rateD = (new Double(rate).doubleValue()) + 0.5;
        long rateL = Math.round(rateD);
        return Integer.parseInt(rateL + "");
    }
    
    public static float calculateAverageRate(ArrayList rates, int dagteller) {
        //int[] wegingsfactoren = {4,3,2,1};
        int[] wegingsfactoren = {1,0,0,0};

        Dagkoers huidigeDagKoers = (Dagkoers) rates.get(dagteller);
        Dagkoers dagkoers_3 = (Dagkoers) rates.get(dagteller - 3);
        Dagkoers dagkoers_2 = (Dagkoers) rates.get(dagteller - 2);
        Dagkoers dagkoers_1 = (Dagkoers) rates.get(dagteller - 1);

        float avrKoers = 0;
        int sum = 0;
        for (int k = 0; k < wegingsfactoren.length; k++) {
            sum = sum + wegingsfactoren[k];
            switch (k) {
            case (0):
                avrKoers = avrKoers + huidigeDagKoers.closekoers * wegingsfactoren[0];
                break;
            case (1):
                avrKoers = avrKoers + dagkoers_1.closekoers * wegingsfactoren[1];
                break;
            case (2):
                avrKoers = avrKoers + dagkoers_2.closekoers * wegingsfactoren[2];
                break;
            case (3):
                avrKoers = avrKoers + dagkoers_3.closekoers * wegingsfactoren[3];
                break;
            }
        }
        avrKoers = avrKoers / sum;
        return avrKoers;
    }
    
    public static String round(String koers) {
        int dot = koers.indexOf(".") + 1;
        double koersD = Double.parseDouble(koers);
        int numberOfDecimals = 2;
        if (dot > -1) {
            numberOfDecimals = koers.length() - dot;
        } 
        String rounded = round(koersD, numberOfDecimals) + "";

        return rounded;
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    public static String divide(String koers1, String koers2) {
        return divide(new Double(koers1).doubleValue(), new Double(koers2).doubleValue());

    }
    
    public static float divide(double koers, int value) {
        double temp = koers / new Double(value).doubleValue();
        String rounded = round(temp, 2) + "";
        return new Float(rounded).floatValue();
    }
    
    public static String divide(double koers1, double koers2) {
        double temp = ((koers2 / koers1) * 100) - 100;
        String rounded = round(temp, 2) + "";
        return rounded;
    }

    public static double procVerschil(double oldKoers, double newKoers) {
        double temp = newKoers - oldKoers;
        double temp1 = temp / oldKoers;
        double temp2 = temp1 * 100;
        return temp2;
    }

    public static double procVerschil(String oldKoersString, String newKoersString) {
        double oldKoers = Double.parseDouble(oldKoersString);
        double newKoers = Double.parseDouble(newKoersString);

        double temp = 1 + ((newKoers - oldKoers) / oldKoers);

        return temp;
    }
}
