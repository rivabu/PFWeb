package org.rients.com.constants;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class Temp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // and percent format for each locale
        double myNumber = -1234.56;
        NumberFormat form;
        for (int j=0; j<4; ++j) {
            System.out.println("FORMAT");
                switch (j) {
                case 0:
                    form = NumberFormat.getInstance(); break;
                case 1:
                    form = NumberFormat.getIntegerInstance(); break;
                case 2:
                    form = NumberFormat.getCurrencyInstance(); break;
                default:
                    form = NumberFormat.getPercentInstance(); break;
                }
                if (form instanceof DecimalFormat) {
                    System.out.print(": " + ((DecimalFormat) form).toPattern());
                }
                System.out.print(" -> " + form.format(myNumber));
                try {
                    System.out.println(" -> " + form.parse(form.format(myNumber)));
                } catch (ParseException e) {}
        }


    }

}
