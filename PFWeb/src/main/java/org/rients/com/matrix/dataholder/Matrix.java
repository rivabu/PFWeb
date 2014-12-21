package org.rients.com.matrix.dataholder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rients.com.constants.Constants;
import org.rients.com.model.AllTransactions;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.MathFunctions;

public class Matrix {

	String[] dates = null;
	String fundname = null;

	FundDataHolder[] columns = null;
	int aantalColumns = 0;
	int aantalDays = 0;
	FundDataHolder transactionsPerc = null;

	public Matrix(String fundname, int aantalColumns, int aantalDays) {
		super();
		this.fundname = fundname;
		columns = new FundDataHolder[aantalColumns];
		dates = new String[aantalDays];
		this.aantalColumns = aantalColumns;
		this.aantalDays = aantalDays;
	}

	public String[] getDates() {
		return dates;
	}
	
	public String getFundname() {
		return fundname;
	}


	public int getMaxFundnameLength() {
		int maxLength = 0;
		for (int i = 0; i < this.getAantalColumns(); i++) {
			if (this.getColumn(i).getColumnName().length() > maxLength) {
				maxLength = this.getColumn(i).getColumnName().length();
			}
		}
		return maxLength;
	}

	public void fillDates(List<Dagkoers> rates) {
		int counter = 0;
		for (Dagkoers dagkoers : rates) {
			dates[counter] = dagkoers.getDatum();
			counter++;
			if (counter == dates.length) {
				break;
			}
		}
	}

	public FundDataHolder getColumn(int number) {
		return columns[number];
	}

	public FundDataHolder getFundData(String fundName) {
		for (int i = 0; i < this.getAantalColumns(); i++) {
			if (this.getColumn(i).getColumnName().equals(fundName)) {
				return columns[i];
			}
		}
		return null;
	}

	public void writeToFile() {
		String filename = Constants.TRANSACTIONDIR + Constants.SEP + fundname + Constants.CSV;
		String[] dates = getDates();

		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < dates.length; i++) {
			String date = dates[i];
			StringBuffer sb = new StringBuffer(date);
			for (int j = 0; j < aantalColumns; j++) {
				String value = columns[j].getValueAsString(date);
				sb.append(", " + value);
				if (columns[j].getColumnName().equals("Koers")) {
					if (transactionsPerc != null) {
						value = transactionsPerc.getValueAsString(date);
						sb.append(", " + value);
					}
					
				}
			}
			// laatste kolom
			lines.add(sb.toString());
		}
		FileUtils.writeToFile(filename, lines);
	}

	public void setColumn(FundDataHolder data, int number) {
		columns[number] = data;
	}

	public int getAantalColumns() {
		return this.aantalColumns;
	}
	

	public int getAantalDays() {
		return aantalDays;
	}

	public void setTransactions(AllTransactions transactions) {
		FundDataHolder koersen = this.getColumnByName("Koers");
		transactionsPerc = new FundDataHolder("transactions", true);
		int dateCounter = 0;
		double koers = 0;
		double koersVorig = 0;
		Iterator<Transaction> iter = transactions.getAllTransactions().iterator();
		double currentValue = 100;
		double newValue = 100;
		while (iter.hasNext()) {
			Transaction t = iter.next();
			while (Integer.parseInt(dates[dateCounter]) <= t.startDate ) {
				koers = koersen.getValueAsDouble(dates[dateCounter]);
				transactionsPerc.addValue(dates[dateCounter], currentValue);
				dateCounter++;
			}
			if (dateCounter >= dates.length) {
				break;
			}
			while (Integer.parseInt(dates[dateCounter]) <= t.endDate) {
				koersVorig = koers;
				koers = koersen.getValueAsDouble(dates[dateCounter]);
				newValue = currentValue * (1 + MathFunctions.procVerschil(koersVorig, koers) / 100);
				transactionsPerc.addValue(dates[dateCounter], newValue);
				currentValue = newValue;
				dateCounter++;
				if (dateCounter >= dates.length) {
					break;
				}
			}
				
		}
		while (dateCounter < dates.length ) {
			koersVorig = koers;
			koers = koersen.getValueAsDouble(dates[dateCounter]);
			if (koersVorig > 0) {
				newValue = currentValue * (1 + MathFunctions.procVerschil(koersVorig, koers) / 100);
			} 
			transactionsPerc.addValue(dates[dateCounter], newValue);
			currentValue = newValue;
			dateCounter++;
		}
	}

	private FundDataHolder getColumnByName(String name) {
		for (int j = 0; j < aantalColumns; j++) {
			if (columns[j].getColumnName().equals(name)) {
				return columns[j];
			}
		}
		return null;
	}

}
