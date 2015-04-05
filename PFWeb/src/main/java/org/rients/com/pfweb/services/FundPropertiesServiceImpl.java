/**
 * 
 */
package org.rients.com.pfweb.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.PropertiesUtils;
import org.springframework.stereotype.Service;


/**
 * @author Rients
 *
 */
@Service
public class FundPropertiesServiceImpl implements FundPropertiesService {


	public Map<String, String> extractFundProperties(String fundName, List<Dagkoers> koersen) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(Constants.FUNDNAME, fundName); 
	    if (koersen.size() > 1) {
	        int aantalTerug = koersen.size() - 10;
	        if (aantalTerug < 0) {
	            aantalTerug = 0;
	        }
	        
	        String tenDaysBack = "";
	        for (int aantal = aantalTerug; aantal < koersen.size(); aantal++) {
	            Dagkoers day = koersen.get(aantal);
                tenDaysBack += day.getDatum() + "," + day.getClosekoers();
	            if (aantal < koersen.size()) {
	                tenDaysBack += ":";
	            }
	        }
	        Dagkoers lastDay = koersen.get(koersen.size() - 1);
	        Dagkoers previousDay = koersen.get(koersen.size() - 2);
    		String lastDate = lastDay.getDatum();
    		String lastRate = "" + lastDay.getClosekoers();
    		String previousRate = "" + previousDay.getClosekoers();
    		String procVerschil = MathFunctions.divide(previousRate, lastRate);
    		properties.put(Constants.LASTDATE, lastDate);
    		properties.put(Constants.PROC_VERSCHIL, procVerschil);
            properties.put(Constants.LASTRATE, lastRate);
            properties.put(Constants.LAST_TEN_DAYS, tenDaysBack.toString());
            
	    }
		return properties;
	}

    /**
     * Gets the file properties.
     * 
     * @param files
     *            the files
     * @return the file properties
     */
    public List<Properties> getFileProperties(List<String> files) {
        List<Properties> props = new ArrayList<Properties>();
        for (String file : files) {
            //String fileName = Constants.FUND_PROPERTIESDIR + file + Constants.PROPERTIES;
            Properties prop = PropertiesUtils.getPropertiesFromPropertiesDir(file + Constants.PROPERTIES);
            if (!prop.containsKey("graphParameters")) {
                String[][] turningPoints = { { "1", "0.75" }, { "2", "1" }, { "1", "1" }, { "1", "1.5" }, { "1", "2" } };
                prop.put("graphParameters", turningPoints);
            } else {
                prop.put("graphParameters", convertGraphParameters(prop.getProperty("graphParameters")));
            }
            if (prop.containsKey(Constants.LAST_TEN_DAYS)) {
                prop.put("lastTenDays", convertLastTenDays(prop.getProperty(Constants.LAST_TEN_DAYS)));
            }

            props.add(prop);

        }
        return props;
    }

    private String[][] convertGraphParameters(String line) {
        String[][] graphParameters = new String[5][2];
        StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), ":");
        if (stringtokenizer.countTokens() == 5) {
            for (int i = 0; i < 5; i++) {
                String token = stringtokenizer.nextToken().trim();
                int indexDubbelePunt = token.indexOf(",");
                String turningPoint = token.substring(0, indexDubbelePunt);
                String stepSize = token.substring(indexDubbelePunt + 1);
                graphParameters[i][0] = turningPoint;
                graphParameters[i][1] = stepSize;
            }
        }
        return graphParameters;
    }

    private String[][] convertLastTenDays(String line) {
        String[][] lastTenDays = new String[10][3];
        StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), "\\:");
        int aantal = stringtokenizer.countTokens();
        double lowest = 10000000000000d;
        double highest = 0d;
        int highestNumber = 0;
        int lowestNumber = 0;
        for (int i = 0; i < aantal ; i++) {
            String token = stringtokenizer.nextToken().trim();
            int indexDubbelePunt = token.indexOf(",");
            String datum = token.substring(0, indexDubbelePunt);
            String koers = token.substring(indexDubbelePunt + 1);
            if (Double.parseDouble(koers) >= highest) {
                highest = Double.parseDouble(koers);
                highestNumber = i;
            }
            if (Double.parseDouble(koers) <= lowest) {
                lowest = Double.parseDouble(koers);
                lowestNumber = i;
            }
            lastTenDays[i][0] = datum;
            lastTenDays[i][1] = koers;
            lastTenDays[i][2] = "normal";
        }
        lastTenDays[highestNumber][2] = "highest";
        lastTenDays[lowestNumber][2] = "lowest";
        
        return lastTenDays;
    }



}
