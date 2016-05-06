package org.rients.com.executables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.IntradayKoers;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.utils.FileUtils;

public class OHLCGenerator {

	public static void main(String[] args) throws IOException {
		OHLCGenerator generator = new OHLCGenerator();
		generator.processAll();
		// TODO Auto-generated method stub

	}
	
	public void processAll() throws IOException {
		String fondsenDirectory = Constants.INTRADAY_KOERSENDIR;
		List<String> dirs =  FileUtils.getSubdirs(fondsenDirectory);
		for (String dir: dirs) {
			if (!dir.equals("_properties")) {
				String filename = dir + ".csv";
				List<Dagkoers> koersen = new ArrayList<Dagkoers>();
				String fileDir = fondsenDirectory + dir;
				List<String> dagen = FileUtils.getFiles(fileDir, ".csv", true);
				for (String dag: dagen) {
					String intradayFilename = fileDir + "\\" + dag;
					List<IntradayKoers> intradayKoersen = new HandleFundData().getIntradayRates(intradayFilename);
					Dagkoers dagkoers = new Dagkoers();
					
					koersen.add(dagkoers);
				}
				
				FileUtils.writeToFile(filename, koersen);
			}
			System.out.println(dir);
		}
		
	}
	
	public void processOneDay(String day) {
		//TODO implement
	}

}
