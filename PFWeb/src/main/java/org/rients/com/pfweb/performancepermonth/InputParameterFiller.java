package org.rients.com.pfweb.performancepermonth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputParameterFiller {

    public List<InputParameters>  fillIterations() {
        List<Integer> longMonths = new ArrayList<Integer>(Arrays.asList(1, 5, 6, 7, 8, 9)); // jan, mei - sept
        List<Integer> stopLosses = new ArrayList<Integer>(Arrays.asList(-10, -9)); 
        
        List<InputParameters> elements = new ArrayList<InputParameters>();
        for (Integer stopLoss: stopLosses) {
            InputParameters input = new InputParameters();

            input.setLongMonths(longMonths);
            input.setStepSize(1.3f);
            input.setTurningPoint(2);
            input.setStopLoss(stopLoss);
            elements.add(input);
        }
        return elements;
    }
}
