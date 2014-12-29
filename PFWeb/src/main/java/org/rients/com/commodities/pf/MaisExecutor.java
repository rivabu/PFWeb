package org.rients.com.commodities.pf;

import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.matrix.dataholder.FundDataHolder;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.AllTransactions;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.PFModel;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.pfweb.services.HandlePF;
import org.rients.com.pfweb.services.modelfunctions.ModelFunctions;
import org.rients.com.utils.FileUtils;


public class MaisExecutor {

	// best: 1, 1.3
    private int TURNING_POINT = 1;
    private float STEPSIZE = 1.3f;
    
    private int VAR_KOERS = 0;

    HandleFundData fundData = new HandleFundData();
	public void process() {

        String directory = Constants.FAVORITESDIR;
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        for (int i = 0; i < files.size(); i++) {
        	if (files.get(i).equals("gold")) {
        		for (int tp = 1; tp < 6; tp++) {
        			for (float sz = 0.1f; sz < 3f; sz = sz + 0.1f) {
        				processFile(files.get(i), sz, tp);
        			}
        		}
				//processFile(files.get(i), 3, 2.1f);
        		
        	}
        }
	}
	
	public void processFile(String fundName, float sz, int tp) {
        String pathFull = Constants.FAVORITESDIR;
		Matrix matrix = fillMatrix(fundName, pathFull, false);
		AllTransactions transactions = runRuleSet(matrix, sz, tp);
		matrix.setTransactions(transactions);
        matrix.writeToFile();
        transactions.saveTransactions(fundName);
        System.out.println("sz: " + sz + " tp: "+ tp + transactions.getResultData());

	}
	
	public AllTransactions runRuleSet(Matrix matrix, float sz, int tp) {
		
		AllTransactions transactions = new AllTransactions();
		HandlePF handlePF = new HandlePF();
        PFModel pfModel = handlePF.createPFData(matrix.getRates(), matrix.getFundname(), sz, tp);
        ModelFunctions mf = new ModelFunctions(matrix.getFundname());
        mf.setPFData(pfModel.getPfModel());
        mf.setRates(matrix.getRates());
        mf.handleSimplePFRules(transactions);
		
		return transactions;
		
	}
	
	
	public Matrix fillMatrix(String fundName, String pathFull, boolean forImage) {
		System.out.println("filename: " + fundName);
        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
        
        int aantalDagenTonen = Math.min(rates.size(), Constants.NUMBEROFDAYSTOPRINT);
        Matrix matrix = new Matrix(fundName, 1, aantalDagenTonen);
        matrix.fillDates(rates);
        matrix.setRates(rates);

		
        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", false);
        dataHolderKoers.setKoers(true);
        matrix.setColumn(dataHolderKoers, VAR_KOERS);
        
        int records = aantalDagenTonen;
        if (records > rates.size()) {
            records = rates.size();
        }

        for (int j = 0; j < records; j++) {
        	if (!forImage) {
                matrix.getColumn(VAR_KOERS).addValue(rates.get(j).datum, rates.get(j).closekoers);
        	} else {
                matrix.getColumn(VAR_KOERS).addValue(rates.get(j).datum, 0d);
        		
        	}
        }
        return matrix;

	}



	public static void main(String[] args) {
		new MaisExecutor().process();

	}

}