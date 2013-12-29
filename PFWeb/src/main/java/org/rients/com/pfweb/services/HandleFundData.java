/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.pfweb.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.PropertiesUtils;
import org.springframework.stereotype.Service;


/**
 * @author Rients van Buren
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
@Service
public class HandleFundData {

    private int numberOfDays;
    
	public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    


    
    
    public List<Dagkoers> getFundRates(String fundName, String directory) {
        List<Dagkoers> sublistRecords = new ArrayList<Dagkoers>();
        List<Dagkoers> records = getAllFundRates(fundName, directory);
        if (records != null) {
            int aantalRecords = records.size();
            int fromIndex = 0;
            if (aantalRecords > numberOfDays) {
                fromIndex = aantalRecords - numberOfDays;
            }
            for (int i = fromIndex; i < aantalRecords; i++) {
                sublistRecords.add(records.get(i));
            }
        }
        else {
            System.out.println("NOTING FOUND: getAllFundRates( " + fundName + ", " + directory + " )");
        }
        return sublistRecords;
    }


    public List<Dagkoers> getFundRates(String fundName, String directory, int beginDate, int endDate) {
        List<Dagkoers> sublistRecords = new ArrayList<Dagkoers>();
        
        List<Dagkoers> records = getAllFundRates(fundName, directory);
        if (records != null) {
            int aantalRecords = records.size();

            int beginDateIndex = findIndex(beginDate, records);
            if (beginDateIndex == -1) {
                System.out.println("date: " + beginDate + " not found in " + fundName);
            }
            if (beginDateIndex - numberOfDays > 0) {
                beginDateIndex = beginDateIndex - numberOfDays;
            }
            else {
                beginDateIndex = 0;
            }
            int endDateIndex = findIndex(beginDate, records);
            if (endDateIndex == -1) {
                System.out.println("date: " + endDate + " not found in " + fundName);
            }
            if (endDateIndex + numberOfDays < aantalRecords) {
                endDateIndex = endDateIndex + numberOfDays;
            }
            else {
                endDateIndex = aantalRecords;
            }
            for (int i = beginDateIndex; i < endDateIndex; i++) {
                sublistRecords.add(records.get(i));
            }
        }
        else {
            System.out.println("NOTING FOUND: getAllFundRates( " + fundName + ", " + directory + " )");
        }
        return sublistRecords;
    }
    
    public int findIndex(int date, List<Dagkoers> records) {
        int counter = -1;
        Iterator<Dagkoers> iter = records.iterator();
        while (iter.hasNext()) {
            counter ++;
            Dagkoers dagkoers = iter.next();
            if (dagkoers.getDatumInt() == date) {
                break;
            }
        }
        
        
        return counter;
        
    }

    public List<Dagkoers> getAllFundRates(String fundName, String directory) {
        Properties prop = null;
        if (!directory.contains("intraday")) {
            //String fileName = Constants.FUND_PROPERTIESDIR + fundName + Constants.PROPERTIES;
            prop = PropertiesUtils.getPropertiesFromClasspath(fundName + Constants.PROPERTIES);
        }
		// boolean old = false;
		List<Dagkoers> records = new ArrayList<Dagkoers>();
		String separator = ",";
		BufferedReader bufferedreader = null;
		bufferedreader = FileUtils.openInputFile(directory + fundName + Constants.CSV);
		if (bufferedreader != null) {
			String regel = null;
			float prevRate = 0;
			try {
				while ((regel = bufferedreader.readLine()) != null) {
					StringTokenizer stringtokenizer = new StringTokenizer(regel.trim(), separator);
					int tokens = stringtokenizer.countTokens();
					Dagkoers dagkoers = new Dagkoers();
					if (tokens == 6) {
						dagkoers.datum = stringtokenizer.nextToken();
						dagkoers.openkoers = Float.valueOf(stringtokenizer.nextToken()).floatValue();
						dagkoers.highkoers = Float.valueOf(stringtokenizer.nextToken()).floatValue();
						dagkoers.lowkoers = Float.valueOf(stringtokenizer.nextToken()).floatValue();
						dagkoers.closekoers = Float.valueOf(stringtokenizer.nextToken()).floatValue();
						dagkoers.volume = Long.valueOf(stringtokenizer.nextToken()).longValue();
						if (prevRate == 0)
							prevRate = dagkoers.closekoers;
						dagkoers.prevRate = prevRate;
						if (dagkoers.closekoers == 0) {
							System.err.println("Koersbestand.leesFile closekoers = 0 in file: " + fundName);
							System.err.println("regel: " + regel);
							continue;
						}
						records.add(dagkoers);
					} else if (tokens == 2) {
						dagkoers.datum = stringtokenizer.nextToken();
						if (dagkoers.datum.length() == 6)
							dagkoers.datum = "20" + dagkoers.datum;
						float closekoers = Float.valueOf(stringtokenizer.nextToken()).floatValue();
						if (prop != null && prop.containsKey(Constants.ISCURRENCY)) {
						    closekoers = closekoers * 10;
						}
						dagkoers.openkoers = closekoers;
						dagkoers.highkoers = closekoers;
						dagkoers.lowkoers = closekoers;
						dagkoers.closekoers = closekoers;
						dagkoers.volume = 0;
						if (prevRate == 0)
							prevRate = dagkoers.closekoers;
						dagkoers.prevRate = prevRate;
						records.add(dagkoers);
					} else {
						System.err.println("Koersbestand.leesFile aantal tokens != 6 error in file: " + fundName);
						System.err.println("regel: " + regel);
						// TODO throw technical exception here!!
						return null;
					}
					prevRate = dagkoers.closekoers;
				}
				records.get(records.size() -1).setStatus(DagkoersStatus.LATESTDAY);
				bufferedreader.close();

			} catch (IOException ioexception) {
				ioexception.printStackTrace();
				System.err.println("error in file: " + fundName + "error: " + ioexception.getMessage());
				// TODO throw technical exception here!!
				return null;
			}
			return records;
		} else {
			return null;
		}
	}

}