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
	List<Dagkoers> rates;

	FundDataHolder[] columns = null;
	int aantalColumns = 0;
	int aantalDays = 0;
	FundDataHolder transactionsPercInverse = null;
	FundDataHolder transactionsPerc = null;
	FundDataHolder transactionsAantal = null;

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
					if (transactionsPercInverse != null) {
						value = transactionsPercInverse.getValueAsString(date);
						sb.append(", " + value);
					}
					if (transactionsAantal != null) {
						value = transactionsAantal.getValueAsString(date);
						sb.append(", " + value);
					}
					
				}
			}
			// laatste kolom
			lines.add(sb.toString());
		}
		FileUtils.writeToFile(filename, lines);
	}
	
	public String getEndResult() {
		String returnValue = "0";
		if (transactionsPerc != null) {
			returnValue = transactionsPerc.getValueAsString(dates[dates.length - 1]);
		}
		return returnValue;
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
		transactionsPercInverse = new FundDataHolder("transactions", true);
		transactionsAantal = new FundDataHolder("transactions", true);
		int dateCounter = 0;
		double koers = 0;
		double koersVorig = 0;
		Iterator<Transaction> iter = transactions.getAllTransactions().iterator();
		double currentValue = 100;
		double vorigeValue = 100;
		double newValue = 100;
		double result = 0;
		while (dateCounter < dates.length) {
			transactionsPerc.addValue(dates[dateCounter], 100);
			transactionsPercInverse.addValue(dates[dateCounter], 100);
			transactionsAantal.addValue(dates[dateCounter], 0);
			dateCounter++;
		}
		while (iter.hasNext()) {
			Transaction t = iter.next();
			dateCounter = 0;
			while (Integer.parseInt(dates[dateCounter]) <= t.startDate ) {
				dateCounter++;
			}
			
			while (Integer.parseInt(dates[dateCounter]) <= t.endDate) {

				int currentAantal = transactionsAantal.getValueAsInt(dates[dateCounter]);
				transactionsAantal.addValue(dates[dateCounter], currentAantal + 1);
				dateCounter++;
				if (dateCounter >= dates.length) {
					break;
				}
			}
			if (dateCounter >= dates.length) {
				break;
			}
			if (t.endDate < Integer.parseInt(dates[dateCounter])) {
				continue;
			}
				
		}

		dateCounter = 0;
		while (dateCounter < dates.length) {
			int aantal =  transactionsAantal.getValueAsInt(dates[dateCounter]);
			currentValue = transactionsPerc.getValueAsDouble(dates[dateCounter]);
			koers = koersen.getValueAsDouble(dates[dateCounter]);
			if (aantal == 0) {
				// result blijft gelijk
				currentValue = vorigeValue;
				transactionsPerc.addValue(dates[dateCounter], currentValue);
			} else {
				//TODO is nog niet af, wordt geen rekening gehouden met aantal!
				result = (1 + MathFunctions.procVerschil(koersVorig, koers) / 100);
				currentValue = vorigeValue * result ;
				transactionsPerc.addValue(dates[dateCounter], currentValue);
			}
			dateCounter++;
			vorigeValue = currentValue;
			koersVorig = koers;
		}
		dateCounter = 0;
		vorigeValue = 100;
		koersVorig = koersen.getValueAsDouble(dates[0]);
		while (dateCounter < dates.length) {
			int aantal =  transactionsAantal.getValueAsInt(dates[dateCounter]);
			currentValue = transactionsPercInverse.getValueAsDouble(dates[dateCounter]);
			koers = koersen.getValueAsDouble(dates[dateCounter]);
			if (aantal == 0) {
				// result blijft gelijk
				result = (1 + MathFunctions.procVerschil(koersVorig, koers) / 100);
				currentValue = vorigeValue * result ;
				transactionsPercInverse.addValue(dates[dateCounter], currentValue);
			} else {
				currentValue = vorigeValue;
				transactionsPercInverse.addValue(dates[dateCounter], currentValue);
				//TODO is nog niet af, wordt geen rekening gehouden met aantal!
			}
			dateCounter++;
			vorigeValue = currentValue;
			koersVorig = koers;
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
	
	public List<Dagkoers> getRates() {
		return rates;
	}

	public void setRates(List<Dagkoers> rates) {
		this.rates = rates;
	}


}
