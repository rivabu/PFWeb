package org.rients.com.executables;

import java.util.List;
import java.util.Map;

import org.rients.com.constants.Constants;
import org.rients.com.pfweb.performancepermonth.InputParameterFiller;
import org.rients.com.pfweb.performancepermonth.Optimizer;
import org.rients.com.pfweb.performancepermonth.PerformancePerMonthModel;
import org.rients.com.pfweb.performancepermonth.RSIModel;
import org.rients.com.utils.FileUtils;

public class RSIModelOptimizerExecutor {
    
    /*
     * 
     *  aex-index.csv
        dax.xetra.csv
        dj-indust.csv
        ftse.100.csv
        midkap-ind.csv
        nasdaqcomp.csv
        nikkei.csv
        s.p.500.csv

     */
    
    Optimizer optimizer = new Optimizer();
    InputParameterFiller inputParameterFiller = new InputParameterFiller();
    

    private void process() {
        optimizer.setClassToOptimize(new RSIModel());
        List<Map<String, Object>> inputParams = inputParameterFiller.fillRSIIterations();
        String directory = Constants.KOERSENDIR + Constants.INDEXDIR;
        List<String> files = FileUtils.getFiles(directory, "csv", false);
        float sum = 0;
        for (String filename: files) {
            // String filename = "midkap-ind";

            float result = optimizer.optimize(directory, filename, inputParams);
            System.out.println(filename + ": " + result);
            sum = sum + result;
        }
        
        System.out.println("SUM: " + sum);
    }

    public static void main(String[] args) {
        new RSIModelOptimizerExecutor().process();

    }

}
