package org.rients.com.matrix.dataholder;

import java.util.ArrayList;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.utils.FileUtils;

public class Matrix {

	String[] dates = null;
	String name = null;
	FundDataHolder[] columns = null;
	int aantalColumns = 0;
	int aantalDays = 0;

	public Matrix(String name, int aantalColumns, int aantalDays) {
		super();
		this.name = name;
		columns = new FundDataHolder[aantalColumns];
		dates = new String[aantalDays];
		this.aantalColumns = aantalColumns;
		this.aantalDays = aantalDays;
	}

	public String[] getDates() {
		return dates;
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
		String filename = Constants.TRANSACTIONDIR + Constants.SEP + name + Constants.CSV;
		String[] dates = getDates();

		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < dates.length; i++) {
			String date = dates[i];
			StringBuffer sb = new StringBuffer(date);
			for (int j = 0; j < aantalColumns; j++) {
				String value = columns[j].getValueAsString(date);
				sb.append(", " + value);
			}
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

}
