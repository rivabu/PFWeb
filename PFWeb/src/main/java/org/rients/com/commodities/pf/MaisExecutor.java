package org.rients.com.commodities.pf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.matrix.dataholder.FundDataHolder;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.AllTransactions;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.PFModel;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.pfweb.services.HandlePF;
import org.rients.com.pfweb.services.modelfunctions.ModelFunctions;
import org.rients.com.strengthWeakness.Portfolio;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.HistoricalVotality;
import org.rients.com.utils.HistoricalVotalityAvr;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.SMA;


public class MaisExecutor {

    private int DAGENTERUG = 45;
    private int TURNING_POINT = 1;
    private float STEPSIZE = 1.5f;
    
    private int VAR_KOERS = 0;

    HandleFundData fundData = new HandleFundData();
	public void process() {

        String directory = Constants.FAVORITESDIR;
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        for (int i = 0; i < files.size(); i++) {
        	if (files.get(i).equals("mais")) {
        		processFile(files.get(i));
        	}
        }
	}
	
	public void processFile(String fundName) {
        String pathFull = Constants.FAVORITESDIR;
		Matrix matrix = fillMatrix(fundName, pathFull, false);
		AllTransactions transactions = runRuleSet(matrix);
		matrix.setTransactions(transactions);
        matrix.writeToFile();
        transactions.saveTransactions(fundName);
        System.out.println("result: " + transactions.getResultData());

	}
	
	public AllTransactions runRuleSet(Matrix matrix) {
		
		AllTransactions transactions = new AllTransactions();
		HandlePF handlePF = new HandlePF();
        PFModel pfModel = handlePF.createPFData(matrix.getRates(), matrix.getFundname(), TURNING_POINT, STEPSIZE);
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
