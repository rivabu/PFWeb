package org.rients.com.votality;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.rients.com.boxes.Portfolio;
import org.rients.com.constants.Constants;
import org.rients.com.matrix.dataholder.FundDataHolder;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.AllTransactions;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.HistoricalVotality;
import org.rients.com.utils.HistoricalVotalityAvr;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.SMA;


public class VotalityExecutor {

    private int DAGENTERUG = 45;
    
    private int VAR_KOERS = 0;
    private int VAR_VOTALITEIT = 1;
    private int VAR_GEMIDDELDE = 2;
    private int VAR_VOORSCHRIJDEND_GEMIDDELDE_KORT = 3;
    private int VAR_VOORSCHRIJDEND_GEMIDDELDE_LANG = 4;
    private int VAR_MERIDIAAN_HOOG = 5;
    private int VAR_MERIDIAAN_LAAG = 6;
    
    private int VOORSCHRIJDEND_GEMIDDELDE_KORT = 5;
    private int VOORSCHRIJDEND_GEMIDDELDE_LANG = 9;
    
    private int TOP_PERC = 5;
    private int BOTTOM_PERC = 5;

    HandleFundData fundData = new HandleFundData();
	public void process() {

        String directory = Constants.FAVORITESDIR;
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        for (int i = 0; i < files.size(); i++) {
            processFile(files.get(i));
        }
	}
	
	public void processFile(String fundName) {
        String pathFull = Constants.FAVORITESDIR;
		Matrix matrix = fillMatrix(fundName, pathFull, false);
		AllTransactions transactions = runRuleSet(matrix);
		matrix.setTransactions(transactions);
        matrix.writeToFile();
        //if (save) {
        transactions.saveTransactions(fundName);
        //}
        System.out.println("result: " + transactions.getResultData());

	}
	
	public AllTransactions runRuleSet(Matrix matrix) {
		
		String[] dates = matrix.getDates();
		float koopkoers = 0;
		boolean gekocht = false;
		double gemiddelde_lang_vorig = 1000;
		double gemiddelde_kort_vorig = 1000;
		double koop_gemiddelde_lang = 0;
		int days = 0;
		int totalGekochteDays = 0;
		double profit = 0;
		double totalProfit = 100;
		float vorigekoers = 0;
		float koers = 0;
		float top = 0;
		int transId = 0;
		double topgrens = (Double) matrix.getColumn(VAR_MERIDIAAN_HOOG).getValue(dates[0]);
		double bodemgrens = (Double) matrix.getColumn(VAR_MERIDIAAN_LAAG).getValue(dates[0]);
		double gemiddelde = (Double) matrix.getColumn(VAR_GEMIDDELDE).getValue(dates[0]);
		AllTransactions transactions = new AllTransactions();
		Transaction trans = null;
		String date = null;
		for (int i = DAGENTERUG; i < dates.length; i++) {
			date = dates[i];
			koers = (Float) matrix.getColumn(VAR_KOERS).getValue(date);
			double gemiddelde_lang = (Double) matrix.getColumn(VAR_VOORSCHRIJDEND_GEMIDDELDE_LANG).getValue(date);
			double gemiddelde_kort = (Double) matrix.getColumn(VAR_VOORSCHRIJDEND_GEMIDDELDE_KORT).getValue(date);
			//System.out.println("date: " + date + " koers: " + koers + " gemiddelde lang: " + gemiddelde_lang);
			if (gemiddelde_kort_vorig > gemiddelde_kort && !gekocht  && gemiddelde_kort > gemiddelde) {
				koopkoers = koers;
				koop_gemiddelde_lang = gemiddelde_lang;
				top = koopkoers;
				trans = new Transaction(matrix.getFundname(), new Integer(date).intValue(), transId, koers, 1, Type.LONG);
				transId ++;

				gekocht = true;
			} 
			if (gemiddelde_kort_vorig < gemiddelde_kort && gekocht && vorigekoers > koers && gemiddelde_lang < gemiddelde) {
//			if (gemiddelde_lang_vorig < gemiddelde_lang && gekocht && vorigekoers > koers) {
				trans.addSellInfo(new Integer(date).intValue(), 0, koers);
				transactions.add(trans);
				gekocht = false;
				profit = MathFunctions.procVerschil(koopkoers, koers);
				top = 0;
				totalProfit = totalProfit * ((profit + 100) / 100);
				System.out.println("koopkoers: "+ koopkoers + " profit: " + profit + " totalProfit: " + totalProfit + " koop_gemiddelde_lang: " + koop_gemiddelde_lang + " days: " + days);
				days = 0;
				
			}
//			if (koers < stoploss(top)) {
//				gekocht = false;
//				profit = MathFunctions.procVerschil(koopkoers, koers);
//				top = 0;
//				totalProfit = totalProfit * ((profit + 100) / 100);
//				System.out.println("koopkoers: "+ koopkoers + " profit: " + profit + " totalProfit: " + totalProfit + " koop_gemiddelde_lang: " + koop_gemiddelde_lang + " days: " + days);
//				days = 0;
//			}
			if (gekocht) {
				if (koers > top) {
					top = koers;
				}
				days++;
				totalGekochteDays ++;
			}
			vorigekoers = koers;
			gemiddelde_lang_vorig = gemiddelde_lang;
			gemiddelde_kort_vorig = gemiddelde_kort;
			
		}
		if (gekocht) {
			trans.addSellInfo(new Integer(date).intValue(), 0, koers);
			transactions.add(trans);
			profit = MathFunctions.procVerschil(koopkoers, koers);
			totalProfit = totalProfit * ((profit + 100) / 100);
		}
		totalProfit = totalProfit - 100;
		int allDays = dates.length - DAGENTERUG;
		
		System.out.println("totalProfit: " + totalProfit + " totalDays: " + totalGekochteDays  + "( " + allDays + " )");
		return transactions;
		
	}
	
	public double stoploss(double koers) {
		return koers * .85;
	}
	
	public Matrix fillMatrix(String fundName, String pathFull, boolean forImage) {
		System.out.println("filename: " + fundName);
        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, pathFull);
        
        HistoricalVotalityAvr avrCalculator = getAvr(rates);
        double avr = MathFunctions.roundBigDecimal(avrCalculator.getAvr());
    	double highMeridiaan = MathFunctions.roundBigDecimal(avrCalculator.getMeridiaanHigh(TOP_PERC));;
    	double lowMeridiaan = MathFunctions.roundBigDecimal(avrCalculator.getMeridiaanLow(BOTTOM_PERC));;

        
        int aantalDagenTonen = Math.min(rates.size(), Constants.NUMBEROFDAYSTOPRINT);
        // een matrix is een array van funddataholders.
        
        Formula graphCalculator = null;
        Formula smaLong = null;
        Formula smaShort = null;
        Matrix matrix = new Matrix(fundName, 7, aantalDagenTonen);
        matrix.fillDates(rates);
        matrix.setRates(rates);

        graphCalculator = new HistoricalVotality(DAGENTERUG, avr);
		smaLong = new SMA(VOORSCHRIJDEND_GEMIDDELDE_LANG, avr);
		smaShort = new SMA(VOORSCHRIJDEND_GEMIDDELDE_KORT, avr);
		
        FundDataHolder dataHolderKoers = new FundDataHolder("Koers", false);
        dataHolderKoers.setKoers(true);
        matrix.setColumn(dataHolderKoers, VAR_KOERS);
        
        FundDataHolder dataHolderVotaliteit = new FundDataHolder("Votaliteit", true);
        matrix.setColumn(dataHolderVotaliteit, VAR_VOTALITEIT);

        FundDataHolder dataHolderGemiddelde = new FundDataHolder("Gemiddelde", true);
        matrix.setColumn(dataHolderGemiddelde, VAR_GEMIDDELDE);
        
        FundDataHolder dataHolderSMA5 = new FundDataHolder("Gewogen gemiddelde 5", true);
        matrix.setColumn(dataHolderSMA5, VAR_VOORSCHRIJDEND_GEMIDDELDE_KORT);

        FundDataHolder dataHolderSMA10 = new FundDataHolder("Gewogen gemiddelde 10", true);
        matrix.setColumn(dataHolderSMA10, VAR_VOORSCHRIJDEND_GEMIDDELDE_LANG);
        
        FundDataHolder dataHolderMeridiaanHoog = new FundDataHolder("VAR_MERIDIAAN_HOOG", true);
        matrix.setColumn(dataHolderMeridiaanHoog, VAR_MERIDIAAN_HOOG);

        FundDataHolder dataHolderMeridiaanLaag = new FundDataHolder("VAR_MERIDIAAN_LAAG", true);
        matrix.setColumn(dataHolderMeridiaanLaag, VAR_MERIDIAAN_LAAG);

        
        int days = rates.size();
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

            BigDecimal value = graphCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            matrix.getColumn(VAR_VOTALITEIT).addValue(rates.get(j).datum, MathFunctions.roundBigDecimal(value));
            
            matrix.getColumn(VAR_GEMIDDELDE).addValue(rates.get(j).datum, avr);
            double smaValue5 = MathFunctions.roundBigDecimal(smaShort.compute(value));
        	matrix.getColumn(VAR_VOORSCHRIJDEND_GEMIDDELDE_KORT).addValue(rates.get(j).datum, smaValue5);
            double smaValue10 = MathFunctions.roundBigDecimal(smaLong.compute(value));
        	matrix.getColumn(VAR_VOORSCHRIJDEND_GEMIDDELDE_LANG).addValue(rates.get(j).datum, smaValue10);
        	matrix.getColumn(VAR_MERIDIAAN_HOOG).addValue(rates.get(j).datum, highMeridiaan);
        	matrix.getColumn(VAR_MERIDIAAN_LAAG).addValue(rates.get(j).datum, lowMeridiaan);
        }
        return matrix;

	}

	private HistoricalVotalityAvr getAvr(List<Dagkoers> rates) {
		int avr;
		HistoricalVotalityAvr avrCalculator = new HistoricalVotalityAvr(DAGENTERUG, 0);
        for (int j = 0; j < rates.size(); j++) {
        	avrCalculator.computeAvr(new BigDecimal(rates.get(j).closekoers));
        }
        
		return avrCalculator;
	}


	public static void main(String[] args) {
		new VotalityExecutor().process();

	}

}
