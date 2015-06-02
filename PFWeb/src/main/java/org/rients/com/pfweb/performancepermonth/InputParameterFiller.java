package org.rients.com.pfweb.performancepermonth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class InputParameterFiller {

    public List<Map<String, Object>>  fillIterations() {
        List<Integer> longMonths1 = new ArrayList<Integer>(Arrays.asList(1, 5, 6, 7, 8, 9)); // jan, mei - sept
        List<Integer> longMonths2 = new ArrayList<Integer>(Arrays.asList(1, 5, 6, 7, 9)); // jan, mei - sept
        List<Integer> stopLosses = new ArrayList<Integer>(Arrays.asList(-10, -9)); 
        
        List<Map<String, Object>> elements = new ArrayList<Map<String, Object>>();
        for (Integer stopLoss: stopLosses) {
            Map<String, Object> inputData = new HashMap<String, Object>();
            inputData.put("LongMonths", longMonths1);
            inputData.put("StepSize", 1.3f);
            inputData.put("TurningPoint", 2);
            inputData.put("StopLoss", stopLoss);
            elements.add(inputData);
        }
        for (Integer stopLoss: stopLosses) {
            Map<String, Object> inputData = new HashMap<String, Object>();
            inputData.put("LongMonths", longMonths2);
            inputData.put("StepSize", 1.3f);
            inputData.put("TurningPoint", 2);
            inputData.put("StopLoss", stopLoss);
            elements.add(inputData);
        }
        return elements;
    }
}
