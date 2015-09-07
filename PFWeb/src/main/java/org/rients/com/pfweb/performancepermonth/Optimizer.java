package org.rients.com.pfweb.performancepermonth;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.rients.com.constants.Constants;
import org.rients.com.utils.PropertiesUtils;

public class Optimizer {
    private ModelInterface classToOptimize; 

    /**
     * @param classToOptimize the classToOptimize to set
     */
    public void setClassToOptimize(ModelInterface classToOptimize) {
        this.classToOptimize = classToOptimize;
    }

    public float optimize(String directory, String fundName, List<Map<String, Object>> params) {
        float maxResult = -1000000;
        boolean save = false;
        Map<String, Object> bestVariables = params.get(0);
        Iterator<Map<String, Object>> iter = params.iterator();
        while (iter.hasNext()) {
            Map<String, Object> input = iter.next();
            System.out.println("--- " + fundName + " ---");
            float endResult = classToOptimize.process(directory, fundName, input, save);
            System.out.println("result: " + endResult);
            if (endResult > maxResult) {
                maxResult = endResult;
                bestVariables = input;
            }
        }
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + ".properties";
        PropertiesUtils.saveProperties(filename, mergeInputToProperties(bestVariables));
        // save the winning parameters
        return maxResult;
    }
    
    private Properties mergeInputToProperties(Map<String, Object> input) {
        Properties p = new Properties();
        Set<String> keys = input.keySet();
        for (String key: keys) {
            p.put(key, input.get(key).toString());
        }
        return p;
    }
    
    public Map<String, Object> mergePropertiesToInput(String filename) {
        Properties p = PropertiesUtils.getPropertiesFromDir(Constants.TRANSACTIONDIR , filename);
        Map<String, Object> inputData = new HashMap<String, Object>();
        
        Set<Object> keys = p.keySet();
        for (Object key: keys) {
            String keyString = key.toString();
            
            if (keyString.equals("LongMonths")) {
                String value = p.get(key).toString();
                value  = StringUtils.remove(value, ", ");
                value  = StringUtils.remove(value, "[");
                value  = StringUtils.remove(value, "]");
                int[] intArray = new int[value.length()];
                for (int i = 0; i < value.length(); i++) {
                    intArray[i] = Integer.parseInt(String.valueOf(value.charAt(i)));
                }
                List<Integer> longMonths = Arrays.asList(ArrayUtils.toObject(intArray));
                inputData.put("LongMonths", longMonths);
                
            }
            if (keyString.equals("StepSize")) {
                Float value = new Float(p.get(key).toString());
                inputData.put("StepSize", value);
            }
            if (keyString.equals("TurningPoint")) {
                Integer value = new Integer(p.get(key).toString());
                inputData.put("TurningPoint", value);
            }
            if (keyString.equals("StopLoss")) {
                Integer value = new Integer(p.get(key).toString());
                inputData.put("StopLoss", value);
            }
            if (keyString.equals("rsiValueShort")) {
                Integer value = new Integer(p.get(key).toString());
                inputData.put("rsiValueShort", value);
            }
            if (keyString.equals("rsiValueMiddle")) {
                Integer value = new Integer(p.get(key).toString());
                inputData.put("rsiValueMiddle", value);
            }
            if (keyString.equals("rsiLength")) {
                Integer value = new Integer(p.get(key).toString());
                inputData.put("rsiLength", value);
            }
        }
        return inputData;
    }


}
