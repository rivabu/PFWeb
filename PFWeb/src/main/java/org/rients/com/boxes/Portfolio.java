package org.rients.com.boxes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.model.StrategyResult;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;

public class Portfolio {

	private List<Transaction> inStock = new ArrayList<Transaction>();
	private List<Transaction> allTransactions = new ArrayList<Transaction>();

	
	public void add(Transaction transaction) {

		inStock.add(transaction);
		allTransactions.add(transaction);

		removeTransactionFromPortfolio(transaction.getStartDate());
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
	
	// ik koop hem niet, want ik weet dat ie verlies op gaat leveren!
	// ik koop bij, want ik weet dat ie winst op gaat leveren!
	public double resultSoFar(String fundName, float huidigeKoers) {
		double returnValue = 0d;
		Iterator<Transaction> iter = inStock.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.getFundName().equals(fundName)) {
//				if (t.getScorePerc() > 0)
	 				double result = getScorePerc(t.startRate, huidigeKoers);
					returnValue = returnValue + result;
			}
		}
		return returnValue;
	}
	
   public float getScorePerc(float startRate, float endRate) {
    	if (startRate == 0) {
    		return 0;
    	}
    	float score = ((endRate - startRate) / startRate) * 100;
    	return score;
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
		double endResultPerc = 100;
		double resultWinners = 0;
		double resultLosers = 0;
		double maxWin = 0;
		double maxLoss = 0;
		result.setNumberOfTransactions(allTransactions.size());
		Iterator<Transaction> iter = allTransactions.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.type == Type.CASH) {
				continue;
			}
			System.out.println("trans: " + t);
			endResult = endResult + t.getScoreAbs();
			endResultPerc = endResultPerc * ((100 + t.getScorePerc()) / 100);
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
		result.setEndResultPerc(endResultPerc - 100);
		if (numberOfWinners> 0) {
			result.setAvrWin(resultWinners/numberOfWinners);
		} else {
			result.setAvrWin(0);
		}
		if (numberOfLosers> 0) {
			result.setAvrLoss(resultLosers/numberOfLosers);
		} else {
			result.setAvrLoss(0);
		}
		result.setMaxLoss(maxLoss);
		result.setMaxProfit(maxWin);
		if (allTransactions.size()> 0) {
			result.setAvrResult(endResult/allTransactions.size());
		} else {
			result.setAvrResult(0);
		}
		return result;
	}
	

}
