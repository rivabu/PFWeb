package org.rients.com.executables;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.rients.com.constants.Constants;
import org.rients.com.model.Categories;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.PropertiesUtils;

public class WinnersAndLosersExecutor {
    
    TreeMap<String, Integer> score = new TreeMap<String, Integer>();
    TreeMap<String, String> lastDayScore = new TreeMap<String, String>();

    private static int TERUG = 10;
    private static int START = 0;
    
    public static void main(String[] args) throws Exception {
        WinnersAndLosersExecutor winnersAndLosers = new WinnersAndLosersExecutor();
        winnersAndLosers.process();

    }

    void process() {
       
        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + Categories.INTRADAY + "/_properties", ".properties", false);
        int aantal = files.size();
        int  counter = 1;
        boolean first = true;
        while (counter <= TERUG) {
            String dag = files.get(aantal - counter + START);
            String vorigeDag = files.get(aantal - counter - 1 + START);
            String filenaam = Constants.KOERSENDIR + Categories.INTRADAY + "/_properties/" + dag + ".properties";
            String vorigeFilenaam = Constants.KOERSENDIR + Categories.INTRADAY + "/_properties/" + vorigeDag + ".properties";
            Properties day = PropertiesUtils.getProperties(filenaam);
            if (first) {
                
                initialScore(day);
            }
            Properties vorigeDay = PropertiesUtils.getProperties(vorigeFilenaam);
            Enumeration<Object> fondsen = day.keys();
            Map<Double, String> ascSortedMap = new TreeMap<Double, String> ();
            while (fondsen.hasMoreElements()) {
                String fonds = (String) fondsen.nextElement();
                if (StringUtils.isBlank(vorigeDay.getProperty(fonds)) || StringUtils.isBlank(day.getProperty(fonds))) {
                    score.remove(fonds);
                    continue;
                }
                double verschil = MathFunctions.procVerschil(vorigeDay.getProperty(fonds), day.getProperty(fonds)) - 1;
                ascSortedMap.put(verschil, fonds);
                if (first) {
                    String score = new Double(MathFunctions.round(verschil * 100, 2)).toString();
                    //System.out.println(fonds + " " + score);
                    lastDayScore.put(fonds, score);
                }
            }
            //System.out.println(">>>>DATE: " + dag);
            
            handleMap(ascSortedMap);
            
            counter ++;
            first = false;
        }
        printMap(score);
    }

    private void initialScore(Properties day) {
        Enumeration<Object> fondsen = day.keys();
        while (fondsen.hasMoreElements()) {
            String fonds = (String) fondsen.nextElement();
            score.put(fonds, 0);
        }
        
    }

    public void printMap(Map<String, Integer> map) {
        @SuppressWarnings("unchecked")
        Map<String, Integer> sortedMap = new TreeMap<String, Integer>(new ValueComparator(map));
        sortedMap.putAll(map);
        Set<Entry<String, Integer>> set = sortedMap.entrySet();
        Iterator<Entry<String, Integer>> i = set.iterator();
        while(i.hasNext()) {
            Entry<String, Integer> entry =  i.next();
            String fonds = entry.getKey();
            String score = lastDayScore.get(fonds);
            System.out.println("Key : " + fonds + " Value : "+ entry.getValue() + " (" + score +"%)");
        }
        

    }
    


    
    public void handleMap(Map<Double, String> map) {
        Set<Entry<Double, String>> set = map.entrySet();
        Iterator<Entry<Double, String>> i = set.iterator();
        int punten = 0;
        while(i.hasNext()) {
            Entry<Double, String> entry  = i.next();
            //System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
            int current = score.get(entry.getValue());
            current = current + punten;
            score.put(entry.getValue().toString(), current);
            punten ++;
        }

    }
  
}
