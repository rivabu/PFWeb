/**
 * 
 */
package rients.trading.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rients.com.constants.Constants;

import rients.trading.download.model.Dagkoers;
import rients.trading.utils.MathFunctions;

/**
 * @author Rients
 *
 */
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

 

}
