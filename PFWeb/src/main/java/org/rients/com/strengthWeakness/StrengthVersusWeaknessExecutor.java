package org.rients.com.strengthWeakness;

import java.math.BigDecimal;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.matrix.dataholder.FundDataHolder;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.Categories;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.MathFunctions;

public class StrengthVersusWeaknessExecutor {

    HandleFundData fundData = new HandleFundData();
    
    private int totalDAYS;
    private int strengthOverDays;
    private int sellAfterDays = 24;

    
    
    
	public void process() {

//        boolean save = false;
//        for (int i=1; i<20; i++) {
//			for (int j=20; j<25; j++) {
//				fillKoersMatrix(i, j, save);
//				
//			}
//		}
		fillKoersMatrix(11, 23, true);
	}
	
	public void fillKoersMatrix(int strengthOverDays_, int sellAfterDays_, boolean save) {
		
        strengthOverDays = strengthOverDays_;
        sellAfterDays = sellAfterDays_;
//        List<Transaction> transactions = handleMatrixForStrength(matrix, true);
        String directory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
        // get aex rates
        List<Dagkoers> aexRates = getAexRates();
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        totalDAYS = aexRates.size();
        
        Matrix matrix = createMatrix(aexRates, files);
        fillMatrixWithData(matrix, directory, files);
        Portfolio portfolio = handleMatrixForStrength(matrix, false);
        if (save) {
        	portfolio.saveTransactions();
        }
        System.out.println("strengthOverDays: "+ strengthOverDays + " sellAfterDays: " + sellAfterDays + portfolio.getResultData());
	}

	private Portfolio handleMatrixForStrength(Matrix matrix, boolean strong) {
		int aantalFunds = matrix.getAantalFunds();
		Portfolio portfolio = new Portfolio();
		String[] dates = matrix.getDates();
		Double[] amounts = new Double[sellAfterDays];
		for (int i = 0; i<amounts.length; i++) {
			amounts[i] = 1000d;
		}
		int amountCounter = 0;
		int transId = 1;
		for (int i=strengthOverDays; i<dates.length; i++) {
			double maxStrength = -1000;
			double minStrength = 1000;
			String fundName = "";
			String date = dates[i];
			String futureDate = null;
			double koopKoers = 0d;
			double verkoopKoers = 0d;
			for(int j=0; j<aantalFunds; j++) {
				StrengthWeakness strength = (StrengthWeakness) matrix.getFundData(j).getValue(date);
				if (strength != null) {
					if ((strong && strength.strength > maxStrength) || (!strong && strength.strength < minStrength)) {
						if (strong) {
							maxStrength = MathFunctions.round(strength.strength, 2);
						} else {
							minStrength = MathFunctions.round(strength.strength, 2);
						}
	
						if (i + sellAfterDays < dates.length) {
							fundName = matrix.getFundData(j).getFundName();
							koopKoers = MathFunctions.round(strength.koers, 2);
							StrengthWeakness futureStrength = (StrengthWeakness) matrix.getFundData(j).getValue(dates[i + sellAfterDays]);
							futureDate = dates[i + sellAfterDays];
							verkoopKoers =  MathFunctions.round(futureStrength.koers, 2);
						}
					}
				}
			}
			if (i + sellAfterDays < dates.length) {
				// kopen als ik hem nog niet heb, of als ik op winst sta.
				if (!portfolio.hasInStock(fundName) || portfolio.resultSoFar(fundName) > 0) {
					double aantalBought = amounts[amountCounter] / koopKoers;
					amounts[amountCounter] = aantalBought * verkoopKoers;
					Transaction trans = new Transaction(fundName, new Integer(date).intValue(), transId, new Double(koopKoers).floatValue(), aantalBought, Type.LONG);
					transId ++;
					trans.addSellInfo(new Integer(futureDate).intValue(), 0, new Double(verkoopKoers).floatValue());
					portfolio.add(trans);
				}
			}
			amountCounter++;
			if (amountCounter == sellAfterDays) {
				amountCounter = 0;
			}
		}
		//System.out.println("profit: " + MathFunctions.round(portfolio.getProfit(), 2));
		double totalAmount = 0d;
		for (int i = 0; i<amounts.length; i++) {
			totalAmount = totalAmount + amounts[i];
			System.out.println("i = " + i + " :" + MathFunctions.round(amounts[i] - 1000, 2));
		}
		System.out.println("totalAmount: " + MathFunctions.round(totalAmount - (sellAfterDays * 1000), 2));
		return portfolio;
	}

	private Matrix createMatrix(List<Dagkoers> aexRates, List<String> files) {
		Matrix matrix = new Matrix(files.size(), totalDAYS);
        fundData.setNumberOfDays(totalDAYS + StrengthWeaknessConstants.strengthOverDays);
        matrix.fillDates(aexRates);
        for (int i = 0; i < files.size(); i++) {
        	//System.out.println(files.get(i));
            FundDataHolder dataHolder;
            dataHolder = new FundDataHolder(files.get(i), totalDAYS);
            matrix.setFundData(dataHolder, i);
        }
		return matrix;
	}

	private List<Dagkoers> getAexRates() {
        String indexDir = Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP;

		fundData.setNumberOfDays(strengthOverDays);
        List<Dagkoers> aexRates = fundData.getFundRates(Constants.AEX_INDEX, indexDir, StrengthWeaknessConstants.startDate, StrengthWeaknessConstants.endDate, 0);
		return aexRates;
	}
	

	
	private void fillMatrixWithData(Matrix matrix, String directory, List<String> files) {
        List<Dagkoers> rates = null;
        fundData.setNumberOfDays(strengthOverDays);
        for (int file = 0; file < files.size(); file++) {
                rates = fundData.getFundRates(files.get(file), directory, StrengthWeaknessConstants.startDate, StrengthWeaknessConstants.endDate, 0);
            int startValue = 0;
            if (rates.size() < totalDAYS && rates.size() > strengthOverDays) {
                int difference = totalDAYS - rates.size();
                String[] dates = matrix.getDates();
                for (int j = 0; j < difference; j++) {
                	// dummy data
                	matrix.getFundData(file).addValue(dates[j], null);
                }
                startValue = difference;
            }
            Formula computeStrength = new ComputeStrength(strengthOverDays);
            if (rates.size() + startValue == totalDAYS) {
                int koersenCounter = 0;
                for (int j = startValue; j < totalDAYS; j++) {
                	String datum = rates.get(koersenCounter).getDatum();
                	float koers = rates.get(koersenCounter).getClosekoers();
                	matrix.getFundData(file).addValue(datum, new StrengthWeakness(datum, koers, computeStrength.compute(new BigDecimal(koers)).doubleValue()));
                    koersenCounter++;
                }
            }
        }
    }
	public static void main(String[] args) {
		new StrengthVersusWeaknessExecutor().process();

	}

}
