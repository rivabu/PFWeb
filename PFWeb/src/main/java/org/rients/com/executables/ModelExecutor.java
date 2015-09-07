package org.rients.com.executables;

import java.util.List;
import java.util.Map;

import org.rients.com.constants.Constants;
import org.rients.com.pfweb.performancepermonth.InputParameterFiller;
import org.rients.com.pfweb.performancepermonth.Optimizer;
import org.rients.com.pfweb.performancepermonth.PerformancePerMonthModel;
import org.rients.com.utils.FileUtils;

public class ModelExecutor {
    
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
    PerformancePerMonthModel model = new PerformancePerMonthModel();
    

    private void process() {
        String directory = Constants.KOERSENDIR + Constants.INDEXDIR;
        List<String> files = FileUtils.getFiles(directory, "csv", false);
        float sum = 0;
        for (String filename: files) {
            // String filename = "midkap-ind";
            Map<String, Object> inputParams = optimizer.mergePropertiesToInput(filename + ".properties");
            System.out.println(inputParams);
            boolean save = true;
            float result = model.process(directory, filename, inputParams, save);
            System.out.println(filename + ": " + result);
            sum = sum + result;
        }
        System.out.println("SUM: " + sum);
    }

    public static void main(String[] args) {
        new ModelExecutor().process();

    }

}
