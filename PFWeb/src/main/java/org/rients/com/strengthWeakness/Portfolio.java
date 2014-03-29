package org.rients.com.strengthWeakness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.constants.SimpleCache;
import org.rients.com.model.Categories;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.Modelregel;
import org.rients.com.model.PFModel;
import org.rients.com.model.StrategyResult;
import org.rients.com.model.Transaction;
import org.rients.com.pfweb.services.HandlePF;
import org.rients.com.utils.FileUtils;

public class Portfolio {

	private List<Transaction> inStock = new ArrayList<Transaction>();
	private List<Transaction> allTransactions = new ArrayList<Transaction>();

	
	public void add(Transaction transaction) {
//		if (transaction.getBuyId() == 659) {
//			System.out.println("found");
//		}
		addPFRules(transaction);
		inStock.add(transaction);
		allTransactions.add(transaction);

		removeTransactionFromPortfolio(transaction.getStartDate());
	}
	
	private void addPFRules(Transaction transaction) {
		HandlePF handlePF = new HandlePF();
		String directory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
		@SuppressWarnings("unchecked")
		PFModel pfModel = (PFModel) SimpleCache.getInstance().getObject("PFMODEL_" + transaction.getFundName());
		List<Dagkoers> rates = (List<Dagkoers>) SimpleCache.getInstance().getObject("RATES_" + transaction.getFundName());
		if (pfModel == null) {
			pfModel = handlePF.createPFData(rates, transaction.getFundName(), "EXP", directory, 1, new Float(0.5));
			SimpleCache.getInstance().addObject("PFMODEL_" + transaction.getFundName(), pfModel);
		}
//		if (!pfModel.isPlusOnDate(transaction.getStartDate())) {
//			// go to the next day with a plus
//			Modelregel mr = pfModel.findNextPlus(transaction.getStartDate());
//			if (mr.getDatumInt() < transaction.getEndDate()) {
//				Dagkoers nextDag = findKoersByDate(rates, mr.getDatumInt());
//				if (nextDag != null) {
//					transaction.setStartDate(Integer.parseInt(nextDag.datum));
//					transaction.setStartRate(nextDag.closekoers);
//				}
//				
//			}
//		}
//		while (pfModel.isPlusOnDate(transaction.getEndDate())) {
//			Dagkoers nextDag = findKoersAfterByDate(rates, transaction.getEndDate());
//			transaction.setEndDate(nextDag.getDatumInt());
//			transaction.setEndRate(nextDag.closekoers);
//		}
		
//		sellBelowTop(rates, transaction);
//		Dagkoers eenNaLaatsteDag = findKoersBeforeByDate(rates, transaction.getEndDate());
//		if (!pfModel.isPlusOnDate(eenNaLaatsteDag.getDatumInt())) {
//			transaction.setEndDate(eenNaLaatsteDag.getDatumInt());
//			transaction.setEndRate(eenNaLaatsteDag.closekoers);
//		}
		
	}
	
	private void sellBelowTop(List<Dagkoers> rates, Transaction transaction) {
		float maxKoers = transaction.getStartRate();
		boolean start = false;
		for (Dagkoers dk : rates) {
			if (dk.datum.equals(new Integer(transaction.getEndDate()).toString())) {
				break;
			}
			if (start) {
				if (dk.closekoers > maxKoers) {
					maxKoers = dk.closekoers;
				}
				if (dk.closekoers < (maxKoers * 0.75)) {
					transaction.setEndDate(dk.getDatumInt());
					transaction.setEndRate(dk.closekoers);
				}
			}
			if (dk.datum.equals(new Integer(transaction.getStartDate()).toString())) {
				start = true;
				
			}
		}
	}

//	private Dagkoers findNextDay(List<Dagkoers> rates, int datum) {
//		boolean getNext = false;
//		for (Dagkoers dk : rates) {
//			if (getNext) {
//				return dk;
//			}
//			if (dk.datum.equals(new Integer(datum).toString())) {
//				getNext = true;
//			}
//		}
//		return null;
//	}

	private Dagkoers findKoersByDate(List<Dagkoers> rates, int datum) {
		for (Dagkoers dk : rates) {
			if (dk.datum.equals(new Integer(datum).toString())) {
				return dk;
			}
		}
		return null;
	}
	
	private Dagkoers findKoersBeforeByDate(List<Dagkoers> rates, int datum) {
		Dagkoers dkBefore = null;
		for (Dagkoers dk : rates) {
			if (dk.datum.equals(new Integer(datum).toString())) {
				return dkBefore;
			}
			dkBefore = dk;
		}
		return null;
	}
	
	private Dagkoers findKoersAfterByDate(List<Dagkoers> rates, int datum) {
		boolean found = false;
		for (Dagkoers dk : rates) {
			if (found) {
				return dk;
			}
			if (dk.datum.equals(new Integer(datum).toString())) {
				found = true;
			}
		}
		return null;
	}
	

	private void removeTransactionFromPortfolio(int today) {
		Iterator<Transaction> iter = inStock.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.getEndDate() < today) {
				iter.remove();
			}
		}
	}
	
	public List<Transaction> getAllTransactions() {
		return allTransactions;
	}
	
	public boolean hasInStock(String fundName) {
		boolean returnValue = false;
		Iterator<Transaction> iter = inStock.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.getFundName().equals(fundName)) {
				returnValue = true;
				break;
			}
		}
		return returnValue;
	}
	
	public double resultSoFar(String fundName) {
		double returnValue = 0d;
		Iterator<Transaction> iter = inStock.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.getFundName().equals(fundName)) {
				double result = t.getScorePerc();
				returnValue = returnValue + result;
			}
		}
		return returnValue;
	}
	
	public double getProfit() {
		double returnValue = 0d;
		Iterator<Transaction> iter = allTransactions.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			returnValue = returnValue + t.getScoreAbs();
		}
		return returnValue;
	}
	
	public void saveTransactions() {
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + Constants.ALL_TRANSACTIONS;
        FileUtils.writeToFile(filename, allTransactions);
	}
	
	public StrategyResult getResultData() {
		StrategyResult result = new StrategyResult();
		int numberOfWinners = 0;
		int numberOfLosers = 0;
		double endResult = 0;
		double resultWinners = 0;
		double resultLosers = 0;
		double maxWin = 0;
		double maxLoss = 0;
		result.setNumberOfTransactions(allTransactions.size());
		Iterator<Transaction> iter = allTransactions.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			endResult = endResult + t.getScoreAbs();
			if (t.getScorePerc() >= 0) {
				if (maxWin < t.getScorePerc()) {
					maxWin = t.getScorePerc();
				}
				resultWinners = resultWinners + t.getScorePerc();
				numberOfWinners++;
			} else {
				if (maxLoss > t.getScorePerc()) {
					maxLoss = t.getScorePerc();
				}
				resultLosers = resultLosers + t.getScorePerc();
				numberOfLosers++;
			}
		}
		result.setNumberOfWinners(numberOfWinners);
		result.setNumberOfLosers(numberOfLosers);
		result.setEndResult(endResult);
		result.setAvrWin(resultWinners/numberOfWinners);
		result.setAvrLoss(resultLosers/numberOfLosers);
		result.setMaxLoss(maxLoss);
		result.setMaxProfit(maxWin);
		result.setAvrResult(endResult / allTransactions.size());
		return result;
	}
	

}
