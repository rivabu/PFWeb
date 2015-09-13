package org.rients.com.boxes.betterthanindex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.rients.com.boxes.ComputeStrength;
import org.rients.com.boxes.Portfolio;
import org.rients.com.boxes.StrengthWeakness;
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
import org.rients.com.utils.ValueComparator;

public class BeatIndexExecutor {

    HandleFundData fundData = new HandleFundData();
    
    //private int totalDAYS;
    private int strengthOverDays;
    private int numberOfBoxes;

    
    
    
	public void process() {

		boolean save = true;
		Matrix matrix = fillKoersMatrix(BeatIndexConstants.strengthOverDays);
        Portfolio portfolio = handleMatrixForStrength(matrix, true);
        if (save) {
            String filename = Constants.TRANSACTIONDIR + Constants.SEP + Constants.ALL_TRANSACTIONS;
            portfolio.saveTransactions(filename);

        }
        System.out.println("strengthOverDays: "+ strengthOverDays + portfolio.getResultData());

	}
	
	public Matrix fillKoersMatrix(int strengthOverDays) {
		
        this.strengthOverDays = strengthOverDays;
        // voor iedere box 1 dag
        this.numberOfBoxes = BeatIndexConstants.numberOfBoxes;
//        List<Transaction> transactions = handleMatrixForStrength(matrix, true);
        String directory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
        // get aex rates
        List<Dagkoers> aexRates = getAexRates();
//        int lastDate = Integer.parseInt((aexRates.get(aexRates.size()-1)).datum);
//		System.out.println("last downloaded date " + lastDate);
		
        int nowString = Integer.parseInt("20" + TimeUtils.getNowString());
//		System.out.println("nowString " + nowString);
		
		List<String> files = FileUtils.getFiles(directory, "csv", false);
        int totalDAYS = aexRates.size();
        Matrix matrix = null;

//		if (TimeUtils.isBetween(9, 18) && (BeatIndexConstants.downloadIntradays || BeatIndexConstants.useIntradays)) {
//			// download de intradays
//			IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
//			Properties intradays = demo.process(BeatIndexConstants.downloadIntradays);
//			aexRates.add(new Dagkoers(nowString+"", 0));
//			totalDAYS = totalDAYS + 1;
//			matrix = createMatrix(aexRates, files, totalDAYS);
//	        fillMatrixWithData(matrix, directory, files, intradays, totalDAYS);
//		}
//		else {
			matrix = createMatrix(aexRates, files, totalDAYS);
	        fillMatrixWithData(matrix, directory, files, null, totalDAYS);
		//}
		return matrix;
	}
	


	private Portfolio handleMatrixForStrength(Matrix matrix, boolean strong) {
		int transactionId = 1;
		double startBedrag = BeatIndexConstants.startBedrag;
		Portfolio portfolio = new Portfolio();
		String[] dates = matrix.getDates();
		Dagkoers[] waarde = new Dagkoers[dates.length];
		for (int i = 0; i < waarde.length; i++) {
			waarde[i] = new Dagkoers(dates[i], (float) (startBedrag));
		}
		BoxContent[] boxes = initializeBoxes(startBedrag);
		double cash = startBedrag;
		Set<String> StocksInPortefeuille = new TreeSet<String>();
		int emptyBoxes = numberOfBoxes;

		for (int dagTeller = strengthOverDays; dagTeller < dates.length; dagTeller++) {

			String currentDate = dates[dagTeller];
			TreeMap<String, Double> sorted_map = findStrongStocks(matrix, dagTeller);
			//System.out.println(sorted_map);
			// bepaal welke aandelen in portefeuille moeten komen
			Set<String> StocksToBuy = getStocksToBuy(boxes, sorted_map);

			//System.out.println("toBuy: " + StocksToBuy);
			
			Iterator<String> bepaalVerkopen = StocksInPortefeuille.iterator();
			while(bepaalVerkopen.hasNext()) {
				String toSell = bepaalVerkopen.next();
				if (StocksToBuy.contains(toSell)) {
					// do nothing, already in portefeuille
				} else {
					// sell this stock
					for (int i = 0; i < boxes.length; i++) {
						if (boxes[i].getTrans() != null && boxes[i].getTrans().getFundName().equals(toSell)) {
							Transaction trans = boxes[i].getTrans();
							double koersVandaag = matrix.getFundData(toSell).getValueAsDouble(currentDate);
							trans.addSellInfo(new Integer(currentDate).intValue(), 0, new Double(koersVandaag).floatValue());
							portfolio.add(trans);
							boxes[i].setTrans(null);
							cash = cash + (trans.getPieces() * koersVandaag);
							emptyBoxes ++;
							break;
						}
					}
				}
			}

			Iterator<String> bepaalKopen = StocksToBuy.iterator();
			while(bepaalKopen.hasNext()) {
				String fundNameToBuy = bepaalKopen.next();
				if (StocksInPortefeuille.contains(fundNameToBuy)) {
					// do nothing, already in portefeuille
				} else {
					// buy this stock
					double koersVandaag = matrix.getFundData(fundNameToBuy).getValueAsDouble(currentDate);
					int pieces = bepaalAantalToBuy(cash, emptyBoxes, koersVandaag);
					Transaction trans = new Transaction(fundNameToBuy, new Integer(currentDate).intValue(), transactionId, new Double(koersVandaag).floatValue(), pieces, Type.LONG);
					transactionId++;
					emptyBoxes --;
					cash = cash - (pieces * koersVandaag);
					int emptyBoxId = getFistEmptyBox(boxes);
					boxes[emptyBoxId].setTrans(trans);
				}
			}
			StocksInPortefeuille = StocksToBuy;
			
			// bepaal waarde portefeuille
			if (dagTeller < dates.length) {
				double huidigeWaarde = 0;
				for (int i = 0; i < boxes.length; i++) {
						String fundname = boxes[i].getTrans().getFundName();
						double koersVandaag = matrix.getFundData(fundname).getValueAsDouble(currentDate);
						double boxWaarde = boxes[i].getTrans().getPieces() * koersVandaag;
						
						huidigeWaarde = huidigeWaarde + boxWaarde;
				}
				waarde[dagTeller].closekoers = new Float(huidigeWaarde + cash);
			}
			
		}

		// finished, empty boxes, nodig?
		for (int i = 0; i < boxes.length; i++) {
			Transaction trans = boxes[i].getTrans();
			trans.setType(Type.UNFINISHED);
			String currentDate = dates[dates.length - 1];
			double koersVandaag = matrix.getFundData(trans.getFundName()).getValueAsDouble(currentDate);
			trans.addSellInfo(new Integer(currentDate).intValue(), 0, new Double(koersVandaag).floatValue());
			portfolio.add(trans);
			boxes[i].setTrans(null);
		}
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + "result.csv";

		FileUtils.writeToFile(filename, new ArrayList<Dagkoers>(Arrays.asList(waarde)));

		//System.out.println("PROFIT (incl cash): " + MathFunctions.round(totalAmount - (BeatIndexConstants.numberOfBoxes * startBedrag), 2));
		return portfolio;
	}
	
	private int getFistEmptyBox(BoxContent[] boxes) {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].getTrans() == null) {
				return i;
			}
		}
		return 0;
	}

	private int bepaalAantalToBuy(double cash, int emptyBoxes, double koers) {
		double cashToSpend = cash / emptyBoxes;
		int pieces = new Double(Math.floor(cashToSpend / koers)).intValue();
		return pieces;
		
	}

	private Set<String> getStocksToBuy(BoxContent[] boxes, TreeMap<String, Double> sorted_map) {
		Iterator<String> iter = sorted_map.keySet().iterator();
		Set<String> StocksToBuy = new TreeSet<String>();
		for (int i = 0; i < boxes.length; i++) {
			String toBuy = iter.next();
			StocksToBuy.add(toBuy);
		}
		return StocksToBuy;
	}

	private TreeMap<String,Double> findStrongStocks(Matrix matrix, int dagTeller) {
		int aantalFunds = matrix.getAantalColumns();
		String[] dates = matrix.getDates();
		String currentDate = dates[dagTeller];
		TreeMap<String, Double> map = new TreeMap<String, Double>();
		ValueComparator bvc =  new ValueComparator(map);
		TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
		for (int fundCounter = 0; fundCounter < aantalFunds; fundCounter++) {
			if (matrix.getColumn(fundCounter).getValue(currentDate) instanceof StrengthWeakness) {
				StrengthWeakness strength = (StrengthWeakness) matrix.getColumn(fundCounter).getValue(currentDate);
				boolean isFundDataAvailable = isFundDataAvailable(matrix.getColumn(fundCounter), dagTeller, dates);
				if (strength != null && isFundDataAvailable) {
					// bepaal welke aandelen relatief sterk zijn
					map.put(matrix.getColumn(fundCounter).getColumnName(), strength.getStrength());
					if (currentDate.equals("20141015")) {
						System.out.println("stock: " + matrix.getColumn(fundCounter).getColumnName() + " strength: " + strength.getStrength());
					}
				}
			}
		}
		sorted_map.putAll(map);
		return sorted_map;
	}

	private BoxContent[] initializeBoxes(double startBedrag) {
		BoxContent[] boxes = new BoxContent[numberOfBoxes];
		for (int i=0; i< boxes.length; i++) {
			BoxContent box = new BoxContent();
			boxes[i] = box;
		}
		return boxes;
	}

	
	private boolean isFundDataAvailable(FundDataHolder dataHolder, int  dagTeller, String[] dates) {
		boolean returnValue = false;
		int verkoopDatumTeller = dagTeller;
		if (verkoopDatumTeller < dates.length) {
			if (dataHolder.getValue(dates[verkoopDatumTeller]) instanceof StrengthWeakness)
			returnValue = true;
		} else {
			String laatsteDatum = dates[dates.length - 1];
			if (dataHolder.getValue(laatsteDatum) instanceof StrengthWeakness) {
				returnValue = true;
			}
		}
		if (returnValue == false) {
			System.out.println("not available: " + dataHolder.getColumnName());
		}
		return returnValue;
	}



	private Matrix createMatrix(List<Dagkoers> aexRates, List<String> files, int totalDAYS) {
		Matrix matrix = new Matrix("StrengthVersusWeakness", files.size(), totalDAYS);
        fundData.setNumberOfDays(totalDAYS + strengthOverDays);
        matrix.fillDates(aexRates);
        for (int i = 0; i < files.size(); i++) {
        	//System.out.println(files.get(i));
            FundDataHolder dataHolder;
            dataHolder = new FundDataHolder(files.get(i), true);
            matrix.setColumn(dataHolder, i);
        }
		return matrix;
	}

	private List<Dagkoers> getAexRates() {
        String indexDir = Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP;

		fundData.setNumberOfDays(strengthOverDays);
        List<Dagkoers> aexRates = fundData.getFundRates(Constants.AEX_INDEX, indexDir, BeatIndexConstants.startDate, BeatIndexConstants.endDate, 0);
		SimpleCache.getInstance().addObject("RATES_" + Constants.AEX_INDEX, aexRates);

		return aexRates;
	}
	

	
	private void fillMatrixWithData(Matrix matrix, String directory, List<String> files, Properties intradays, int totalDAYS) {
        List<Dagkoers> rates = null;
        fundData.setNumberOfDays(strengthOverDays);
        String nowString = "20" + TimeUtils.getNowString();
        for (int file = 0; file < files.size(); file++) {
        		String filename = files.get(file);
        		System.out.println(filename);
                rates = fundData.getFundRates(filename, directory, BeatIndexConstants.startDate, BeatIndexConstants.endDate, 0);
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
                	matrix.getColumn(file).addValue(dates[j], null);
                }
                startValue = difference;
            }
            Formula computeStrength = new ComputeStrength(strengthOverDays); //15
            if (rates.size() + startValue == totalDAYS) {
                int koersenCounter = 0;
                for (int j = startValue; j < totalDAYS; j++) {
                	String datum = rates.get(koersenCounter).getDatum();
                	float koers = rates.get(koersenCounter).getClosekoers();
                	matrix.getColumn(file).addValue(datum, new StrengthWeakness(datum, koers, computeStrength.compute(new BigDecimal(koers)).doubleValue()));
                    koersenCounter++;
                }
            }
        }
    }
	public static void main(String[] args) {
		new BeatIndexExecutor().process();

	}

}
