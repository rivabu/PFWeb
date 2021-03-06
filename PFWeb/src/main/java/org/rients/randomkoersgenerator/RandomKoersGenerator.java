package org.rients.randomkoersgenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.rients.com.constants.Constants;

import rients.trading.download.model.Dagkoers;
import rients.trading.services.FileIOService;
import rients.trading.services.FileIOServiceImpl;
import rients.trading.services.FundPropertiesService;
import rients.trading.services.FundPropertiesServiceImpl;
import rients.trading.services.HandleFundData;
import rients.trading.utils.FileUtils;
import rients.trading.utils.MathFunctions;
import rients.trading.utils.PropertiesUtils;
import rients.trading.utils.Shuffle;
import rients.trading.utils.Variance;

public class RandomKoersGenerator {
    public RandomKoersGenerator() {
    }

    
    public static String oneFundname = "aex-index";
    public static int AANTAL = 20;
    public static boolean save = false;
    public static boolean printTussenResults = false;
    public static boolean oneStock = true;

    
    private String changeExtension(final String filename, final String newExtension) {
        String newfilename = filename  + newExtension;
        if (filename.endsWith(".csv")) {
            newfilename = filename.substring(0, filename.lastIndexOf('.')) + newExtension;
        }
        return newfilename;
    }
    
    public void execute(String fundname) throws IOException {
        
        HandleFundData fundData = new HandleFundData();
        fundData.setNumberOfDays(600);
        List<Dagkoers> rates = fundData.getFundRates(fundname, Constants.READDIR);

        String array[] = new String[rates.size() - 1];
        float variance = Variance.variances(rates);
        float highest = Variance.getHighest(rates);
        float lowest = Variance.getLowest(rates);
        System.out.println(fundname+": "+variance+" l:"+lowest+" h:"+highest);
        for (int i = 0; i < rates.size() - 1; i++) {
            float eerste = ((Dagkoers) rates.get(i)).closekoers;
            float tweede = ((Dagkoers) rates.get(i + 1)).closekoers;
            float verschil = (tweede - eerste); 
            array[i] = "" + verschil;
        }

        int countLower = 0;
        int countHigher = 0;
        for(int j=1; j<=AANTAL; j++) {
            List l = Shuffle.shuffle(array);
            Iterator i = l.iterator();
            ArrayList dataout = new ArrayList();
            float eerstekoers = ((Dagkoers) rates.get(0)).closekoers;
            Dagkoers start = (Dagkoers) rates.get(0);
            dataout.add(start);
            for (int teller = 1; i.hasNext(); teller++) {
                Dagkoers dk = new Dagkoers();
                eerstekoers += Float.valueOf("" + i.next()).floatValue();
                dk.closekoers = (float) MathFunctions.round(eerstekoers, 2);
                dk.datum = ((Dagkoers) rates.get(teller)).datum;
                dataout.add(dk);
            }
            if(save) {
                String fundnameNew = fundname+"_"+j;
                FileIOService service = new FileIOServiceImpl(null, null);
                service.saveToFile(Constants.WRITEDIR, fundnameNew + ".csv", dataout);
                
                FundPropertiesService propService = new FundPropertiesServiceImpl();
                Map<String, String> fundProperties = propService.extractFundProperties(fundnameNew, dataout);
                fundnameNew = changeExtension(fundnameNew, Constants.PROPERTIES);
                PropertiesUtils.saveProperties(fundnameNew, fundProperties);

            }
            float varianceNew = Variance.variances(dataout);
            String sign = "+";
            if(varianceNew < variance) {
                sign = "-";
                countLower++;
            } else {
                countHigher++;
            }
            highest = Variance.getHighest(dataout);
            lowest = Variance.getLowest(dataout);
            if(printTussenResults) {
                System.out.println(sign+" "+j+" "+varianceNew+" l:"+lowest+" h:"+highest);
            }
        }
        System.out.println(" lower: "+countLower+"higher: "+countHigher);
    }

    public static void main(String[] args) throws IOException
    {
        RandomKoersGenerator a = new RandomKoersGenerator();
        if(!oneStock) {
            List<String> filenames = FileUtils.getFiles(Constants.READDIR, ".csv", true);
            for(int fondsTeller = 0; fondsTeller < filenames.size(); fondsTeller++)
            {
                String fundname = (String) filenames.get(fondsTeller);
                String fundnameShort = fundname.substring(0, fundname.length()-4);
                a.execute(fundnameShort);
            }
        } else {
            save = true;
            printTussenResults = true;
            a.execute(oneFundname);
        }
        
    }
}

