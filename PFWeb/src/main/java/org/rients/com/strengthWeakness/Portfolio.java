package org.rients.com.strengthWeakness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.model.StrategyResult;
import org.rients.com.model.Transaction;
import org.rients.com.utils.FileUtils;

public class Portfolio {

	private List<Transaction> inStock = new ArrayList<Transaction>();
	private List<Transaction> allTransactions = new ArrayList<Transaction>();

	
	public void add(Transaction transaction) {
		inStock.add(transaction);
		allTransactions.add(transaction);
		int today = transaction.getStartDate();
		
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
