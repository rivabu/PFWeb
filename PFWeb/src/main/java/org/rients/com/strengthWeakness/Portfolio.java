package org.rients.com.strengthWeakness;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.model.Transaction;

public class Portfolio {

	private List<Transaction> inStock = new ArrayList<Transaction>();
	
	public void add(Transaction transaction) {
		inStock.add(transaction);
		int today = transaction.getStartDate();
		
		Iterator<Transaction> iter = inStock.iterator();
		while (iter.hasNext()) {
			Transaction t = iter.next();
			if (t.getEndDate() < today) {
				iter.remove();
			}
		}
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

}
