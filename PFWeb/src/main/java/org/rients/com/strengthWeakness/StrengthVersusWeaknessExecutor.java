package org.rients.com.strengthWeakness;

import java.math.BigDecimal;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.constants.SimpleCache;
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
    private int sellAfterDays;
    private int numberOfBoxes;

    
    
    
	public void process() {

        boolean save = false;
//        for (int i=10; i<20; i++) {
//			for (int j=20; j<25; j++) {
//				fillKoersMatrix(i, j, save);
//				
//			}
//		}
		// fillKoersMatrix(11, 23, true);
		fillKoersMatrix(StrengthWeaknessConstants.strengthOverDays, StrengthWeaknessConstants.sellAfterDays , true);
	}
	
	public void fillKoersMatrix(int strengthOverDays, int sellAfterDays, boolean save) {
		
        this.strengthOverDays = strengthOverDays;
        this.sellAfterDays = sellAfterDays;
        // voor iedere box 1 dag
        this.numberOfBoxes = sellAfterDays;
//        List<Transaction> transactions = handleMatrixForStrength(matrix, true);
        String directory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
        // get aex rates
        List<Dagkoers> aexRates = getAexRates();
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        totalDAYS = aexRates.size();
        
        Matrix matrix = createMatrix(aexRates, files);
        fillMatrixWithData(matrix, directory, files);
        Portfolio portfolio = handleMatrixForStrength(matrix, true);
        //Portfolio portfolio = handleMatrixForStrength(matrix, false);
        if (save) {
        	portfolio.saveTransactions();
        }
        System.out.println("strengthOverDays: "+ strengthOverDays + " sellAfterDays: " + sellAfterDays + portfolio.getResultData());
	}

	private Portfolio handleMatrixForStrength(Matrix matrix, boolean strong) {
		int aantalFunds = matrix.getAantalFunds();
		double startBedrag = 3000d;
		Portfolio portfolio = new Portfolio();
		String[] dates = matrix.getDates();
		Double[] boxes = new Double[numberOfBoxes];
		for (int i = 0; i<boxes.length; i++) {
			boxes[i] = startBedrag;
		}
		int boxCounter = 0;
		int transId = 1;
		double cash = 0;
		double totaleWaardePortefeuille = numberOfBoxes * startBedrag;
		int days = 0;
		boolean allBoxesFilled = false;
		for (int dagTeller = strengthOverDays; dagTeller < dates.length; dagTeller++) {
			double maxStrength = -1000;
			double minStrength = 1000;
			String fundName = "";
			String currentDate = dates[dagTeller];
			String futureDate = null;
			double koopKoers = 0d;
			double verkoopKoers = 0d;
			Type typeAankoop = Type.LONG;
			for (int fundCounter = 0; fundCounter < aantalFunds; fundCounter++) {
				if (matrix.getFundData(fundCounter).getValue(currentDate) instanceof StrengthWeakness) {
					StrengthWeakness strength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(currentDate);
					if (strength != null) {
						if ((strong && strength.strength > maxStrength) || (!strong && strength.strength < minStrength)) {
							if (strong) {
								// maxStrength -> grootste stijger afgelopen tijd
								maxStrength = MathFunctions.round(strength.strength, 2);
							} else {
								// minStrength -> grootste daler afgelopen tijd
								minStrength = MathFunctions.round(strength.strength, 2);
							}
							int verkoopDatumTeller = dagTeller + sellAfterDays;
							if (verkoopDatumTeller < dates.length) {
								// enddate found
								if (matrix.getFundData(fundCounter).getValue(dates[verkoopDatumTeller]) != null) {
									koopKoers = MathFunctions.round(strength.koers, 2);
									fundName = matrix.getFundData(fundCounter).getFundName();
									StrengthWeakness futureStrength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(dates[verkoopDatumTeller]);
									futureDate = dates[verkoopDatumTeller];
									verkoopKoers =  MathFunctions.round(futureStrength.koers, 2);
									typeAankoop = Type.LONG;
								} else {
									// deze situatie komt voor als er van een fonds data mist, de matrix moet de afvangen!
									fundName = matrix.getFundData(fundCounter).getFundName();
									String datum = dates[verkoopDatumTeller];
									System.out.println("fund: " + fundName + " datum: " + datum + " not found in matrix!");
								}
							} else {
								// verkoopdatum nog niet bereikt, want die ligt in de toekomst, transactie kan niet afgesloten worden (=UNFINISHED)
								koopKoers = MathFunctions.round(strength.koers, 2);
								fundName = matrix.getFundData(fundCounter).getFundName();
								String laatsteDatum = dates[dates.length - 1];
								StrengthWeakness futureStrength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(laatsteDatum);
								futureDate = laatsteDatum;
								verkoopKoers =  MathFunctions.round(futureStrength.koers, 2);
								typeAankoop = Type.UNFINISHED;
							}
						}
					}
				}
			}
			if (dagTeller < dates.length) {
				// kopen als ik hem nog niet heb, of als ik op winst sta.
				if (!portfolio.hasInStock(fundName) || portfolio.resultSoFar(fundName) > 0) {
					double before = boxes[boxCounter];
					double aantalBought = boxes[boxCounter] / koopKoers;
					
					Transaction trans = new Transaction(fundName, new Integer(currentDate).intValue(), transId, new Double(koopKoers).floatValue(), aantalBought, typeAankoop);
					transId ++;
					trans.addSellInfo(new Integer(futureDate).intValue(), 0, new Double(verkoopKoers).floatValue());
					portfolio.add(trans);

					boxes[boxCounter] = aantalBought * trans.getEndRate();
					
					double diff = boxes[boxCounter] - before;
					totaleWaardePortefeuille = totaleWaardePortefeuille + diff;
					if (allBoxesFilled) {
						double avrBoxContent = totaleWaardePortefeuille / numberOfBoxes;
						double surplus = boxes[boxCounter] - avrBoxContent;
						if (surplus > 0) {
							// haal uit de box, plaats in cash;
							cash = cash + surplus;
							boxes[boxCounter] = boxes[boxCounter] - surplus;
						} else {
							double shortage = surplus * -1;
							if (cash > 0) {
								if (cash - shortage >= 0) {
									cash = cash - shortage;
									boxes[boxCounter] = boxes[boxCounter] + shortage;
								} else {
									// niet genoeg in cash om hele tekort aan te vullen
									cash  = 0;
									boxes[boxCounter] = boxes[boxCounter] + cash;
								}
							}
						}
					}
					days = debugBox1(boxes, boxCounter, days, dagTeller,
							currentDate, trans, diff);
					boxCounter++;
				}
			}
			if (boxCounter == numberOfBoxes) {
				boxCounter = 0;
				allBoxesFilled = true;
			}
		}
		//System.out.println("profit: " + MathFunctions.round(portfolio.getProfit(), 2));
		System.out.println("cash : " + cash);
		double totalAmount = 0d;
		for (int i = 0; i<boxes.length; i++) {
			totalAmount = totalAmount + boxes[i];
			System.out.println("i = " + i + " :" + MathFunctions.round(boxes[i] - 1000, 2));
		}
		System.out.println("totalAmount: " + MathFunctions.round(totalAmount - (sellAfterDays * 1000), 2));
		return portfolio;
	}

	private int debugBox1(Double[] amounts, int amountCounter, int days,
			int dagTeller, String currentDate, Transaction trans, double diff) {
		if (amountCounter == 1) {
			days = days + sellAfterDays;
			if (diff > 0)
				System.out.println(currentDate + " " + amounts[amountCounter] + " " + trans.getFundName() + " K " + trans.getStartRate() + " V " + trans.getEndRate() + " Profit %: " + trans.getScorePercStr() + " "+ trans.getScoreAbsStr() + " DIFF: " + diff);
			else
				System.err.println(currentDate + " " + amounts[amountCounter] + " " + trans.getFundName() + " K " + trans.getStartRate() + " V " + trans.getEndRate() + " Profit %: " + trans.getScorePercStr() + " "+ trans.getScoreAbsStr() + " DIFF: " + diff);
			System.out.println(days + " " + dagTeller);	
		}
		return days;
	}

	private Matrix createMatrix(List<Dagkoers> aexRates, List<String> files) {
		Matrix matrix = new Matrix(files.size(), totalDAYS);
        fundData.setNumberOfDays(totalDAYS + strengthOverDays);
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
		SimpleCache.getInstance().addObject("RATES_" + Constants.AEX_INDEX, aexRates);

		return aexRates;
	}
	

	
	private void fillMatrixWithData(Matrix matrix, String directory, List<String> files) {
        List<Dagkoers> rates = null;
        fundData.setNumberOfDays(strengthOverDays);
        for (int file = 0; file < files.size(); file++) {
        		System.out.println(files.get(file));
                rates = fundData.getFundRates(files.get(file), directory, StrengthWeaknessConstants.startDate, StrengthWeaknessConstants.endDate, 0);
    			SimpleCache.getInstance().addObject("RATES_" + files.get(file), rates);

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
            Formula computeStrength = new ComputeStrength(strengthOverDays); //15
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
