package org.rients.com.executables;

import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.rients.com.constants.Constants;
import org.rients.com.pfweb.services.PerformancePerMonthService;
import org.rients.com.utils.FileUtils;

public class PerformancePerMonthExecutor {
    
    /*
     * 
     * aex-index.csv
dax.xetra.csv
dj-indust.csv
ftse.100.csv
midkap-ind.csv
nasdaqcomp.csv
nikkei.csv
out.dat
s.p.500.csv

     */
    
    PerformancePerMonthService service = new PerformancePerMonthService();
    
    

    private void process() {
        List<String> files = FileUtils.getFiles( Constants.KOERSENDIR + Constants.INDEXDIR, "csv", false);
        for (String filename: files) {
       // String filename = "midkap-ind";
            System.out.println("--- " + filename + " ---");
            float endResult = service.createTransactions(filename, Constants.KOERSENDIR + Constants.INDEXDIR);
            System.out.println("result: " + endResult);
            
        }
    }
    public static void main(String[] args) {
        new PerformancePerMonthExecutor().process();

    }

}
