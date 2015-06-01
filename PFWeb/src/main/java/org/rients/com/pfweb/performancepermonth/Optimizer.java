package org.rients.com.pfweb.performancepermonth;

import java.util.Iterator;
import java.util.List;

public class Optimizer {
    private ModelInterface classToOptimize; 



    /**
     * @param classToOptimize the classToOptimize to set
     */
    public void setClassToOptimize(ModelInterface classToOptimize) {
        this.classToOptimize = classToOptimize;
    }


    public float optimize(String directory, String fundName, List<InputParameters> params) {
        
        float maxResult = 0;
        Iterator<InputParameters> iter = params.iterator();
        while (iter.hasNext()) {
            InputParameters input = iter.next();
            System.out.println("--- " + fundName + " ---");
            float endResult = classToOptimize.process(directory, fundName, input);
            System.out.println("result: " + endResult);
            if (endResult > maxResult) {
                maxResult = endResult;
            }
            
        }
        return maxResult;
    }


}
