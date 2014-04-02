package org.rients.com.strengthWeakness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.rients.com.constants.Constants;
import org.rients.com.constants.SimpleCache;
import org.rients.com.executables.IntradayDownloadExecutor;
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
import org.rients.com.utils.TimeUtils;

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
        int lastDate = Integer.parseInt((aexRates.get(aexRates.size()-1)).datum);
		System.out.println("last downloaded date " + lastDate);
		
        int nowString = Integer.parseInt("20" + TimeUtils.getNowString());
		System.out.println("nowString " + nowString);
		
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        totalDAYS = aexRates.size();
        Matrix matrix = null;

		if (TimeUtils.isBetween(9, 18) && (StrengthWeaknessConstants.downloadIntradays || StrengthWeaknessConstants.useIntradays)) {
			// download de intradays
			IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
			Properties intradays = demo.process(StrengthWeaknessConstants.downloadIntradays);
			aexRates.add(new Dagkoers(nowString+"", 0));
			totalDAYS = totalDAYS + 1;
			matrix = createMatrix(aexRates, files);
	        fillMatrixWithData(matrix, directory, files, intradays);
		}
		else {
			matrix = createMatrix(aexRates, files);
	        fillMatrixWithData(matrix, directory, files, null);
		}
        Portfolio portfolio = handleMatrixForStrength(matrix, true);
        if (save) {
        	portfolio.saveTransactions();
        }
        System.out.println("strengthOverDays: "+ strengthOverDays + " sellAfterDays: " + sellAfterDays + portfolio.getResultData());
	}

	private Portfolio handleMatrixForStrength(Matrix matrix, boolean strong) {
		int aantalFunds = matrix.getAantalFunds();
		double startBedrag = 1000d;
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
			boolean somethingBought = false;
			for (int fundCounter = 0; fundCounter < aantalFunds; fundCounter++) {
				if (matrix.getFundData(fundCounter).getValue(currentDate) instanceof StrengthWeakness) {
					StrengthWeakness strength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(currentDate);
					boolean isFundDataAvailable = isFundDataAvailable(matrix.getFundData(fundCounter), dagTeller, dates);
					if (strength != null && isFundDataAvailable) {
						if ((strong && strength.strength > maxStrength) || (!strong && strength.strength < minStrength)) {
							if (strong) {
								// maxStrength -> grootste stijger afgelopen tijd
								maxStrength = MathFunctions.round(strength.strength, 2);
							} else {
								// minStrength -> grootste daler afgelopen tijd
								minStrength = MathFunctions.round(strength.strength, 2);
							}
							fundName = matrix.getFundData(fundCounter).getFundName();
							koopKoers = MathFunctions.round(strength.koers, 2);
							int verkoopDatumTeller = dagTeller + sellAfterDays;
							if (verkoopDatumTeller < dates.length) {
								// enddate found
								StrengthWeakness futureStrength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(dates[verkoopDatumTeller]);
								futureDate = dates[verkoopDatumTeller];
								verkoopKoers =  MathFunctions.round(futureStrength.koers, 2);
								typeAankoop = Type.LONG;
							} else {
								String laatsteDatum = dates[dates.length - 1];
								// verkoopdatum nog niet bereikt, want die ligt in de toekomst, transactie kan niet afgesloten worden (=UNFINISHED)
								StrengthWeakness futureStrength = (StrengthWeakness) matrix.getFundData(fundCounter).getValue(laatsteDatum);
								futureDate = laatsteDatum;
								verkoopKoers =  MathFunctions.round(futureStrength.koers, 2);
								typeAankoop = Type.UNFINISHED;
							}
							somethingBought = true;
						}
					}
				}
			}
			if (dagTeller < dates.length && somethingBought) {
				// kopen als ik hem nog niet heb, of als ik op winst sta.
				if (!portfolio.hasInStock(fundName) || portfolio.resultSoFar(fundName) > 0) {
					double before = boxes[boxCounter];
					double aantalBought = boxes[boxCounter] / koopKoers;
					
					Transaction trans = new Transaction(fundName, new Integer(currentDate).intValue(), transId, new Double(koopKoers).floatValue(), aantalBought, typeAankoop);
					transId ++;
					if (futureDate == null) {
						System.out.println("futureDate is null" + verkoopKoers + " " + fundName);
					}
					trans.addSellInfo(new Integer(futureDate).intValue(), 0, new Double(verkoopKoers).floatValue());
					portfolio.add(trans);

					boxes[boxCounter] = aantalBought * trans.getEndRate();
					
					double diff = boxes[boxCounter] - before;
					//System.out.println("maxStrength: " + maxStrength + " profit " + diff);
					totaleWaardePortefeuille = totaleWaardePortefeuille + diff;
					if (allBoxesFilled) {
						cash = reOrderBoxes(boxes, boxCounter, cash, totaleWaardePortefeuille);
						System.out.println("cash: " + MathFunctions.round(cash, 2));
					}
//					days = debugBox1(boxes, boxCounter, days, dagTeller,
//							currentDate, trans, diff);
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
		if (cash > 0) {
			String laatsteDatum = dates[dates.length - 1];
			Transaction trans = new Transaction("cash", new Integer(laatsteDatum).intValue(), transId, new Double(0).floatValue(), 1, Type.CASH);
			transId ++;
			trans.addSellInfo(new Integer(laatsteDatum).intValue(), 0, new Double(cash).floatValue());
			portfolio.add(trans);
			
		}
		double totalAmount = cash;
		for (int i = 0; i<boxes.length; i++) {
			totalAmount = totalAmount + boxes[i];
			System.out.println("i = " + i + " :" + MathFunctions.round(boxes[i], 2));
		}
		System.out.println("totalAmount: " + MathFunctions.round(totalAmount - (sellAfterDays * startBedrag), 2));
		return portfolio;
	}

	
	private boolean isFundDataAvailable(FundDataHolder dataHolder, int  dagTeller, String[] dates) {
		boolean returnValue = false;
		int verkoopDatumTeller = dagTeller + sellAfterDays;
		if (verkoopDatumTeller < dates.length) {
			if (dataHolder.getValue(dates[verkoopDatumTeller]) instanceof StrengthWeakness)
			returnValue = true;
		} else {
			String laatsteDatum = dates[dates.length - 1];
			if (dataHolder.getValue(laatsteDatum) instanceof StrengthWeakness) {
				returnValue = true;
			}
		}
		return returnValue;
	}
	private double reOrderBoxes(Double[] boxes, int boxCounter, double cash,
			double totaleWaardePortefeuille) {
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
					// genoeg in cash om shortage op te vangen
					cash = cash - shortage;
					boxes[boxCounter] = boxes[boxCounter] + shortage;
				} else {
					// niet genoeg in cash om hele tekort aan te vullen
					boxes[boxCounter] = boxes[boxCounter] + cash;
					cash  = 0;
				}
			}
		}
		return cash;
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
	

	
	private void fillMatrixWithData(Matrix matrix, String directory, List<String> files, Properties intradays) {
        List<Dagkoers> rates = null;
        fundData.setNumberOfDays(strengthOverDays);
        String nowString = "20" + TimeUtils.getNowString();
        for (int file = 0; file < files.size(); file++) {
        		String filename = files.get(file);
        		System.out.println(filename);
                rates = fundData.getFundRates(filename, directory, StrengthWeaknessConstants.startDate, StrengthWeaknessConstants.endDate, 0);
                if (intradays != null && intradays.containsKey(filename)) {
                	rates.add(new Dagkoers(nowString, new Float(intradays.get(filename).toString()).floatValue()));
                }
    			SimpleCache.getInstance().addObject("RATES_" + filename, rates);

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
