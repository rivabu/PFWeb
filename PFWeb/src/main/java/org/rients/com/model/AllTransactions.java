package org.rients.com.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.utils.FileUtils;

public class AllTransactions {

	private List<Transaction> allTransactions = new ArrayList<Transaction>();

	
	public void add(Transaction transaction) {
		allTransactions.add(transaction);
	}
	

	public void saveTransactions(String fundName) {
        String filename = Constants.TRANSACTIONDIR + Constants.SEP + fundName + "_trans.csv";
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


	public List<Transaction> getAllTransactions() {
		return allTransactions;
	}
	

}
