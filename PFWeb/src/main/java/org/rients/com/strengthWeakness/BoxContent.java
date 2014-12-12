package org.rients.com.strengthWeakness;

public class BoxContent {
	private String fundName;
	private int id;
	private int numberOfStocks;
	private double value;
	public int getBeginDatum() {
		return beginDatum;
	}

	public void setBeginDatum(int beginDatum) {
		this.beginDatum = beginDatum;
	}

	public int getEindDatum() {
		return eindDatum;
	}

	public void setEindDatum(int eindDatum) {
		this.eindDatum = eindDatum;
	}
	private int beginDatum;
	private int eindDatum;

	
	public BoxContent(int id) {
		super();
		this.id = id;
	}

	public BoxContent(String fundName, int id, int numberOfStocks, double value) {
		super();
		this.fundName = fundName;
		this.id = id;
		this.numberOfStocks = numberOfStocks;
		
		this.value = value;
	}
	
	public boolean inVoorraad(int datum) {
		return (datum >= beginDatum && datum <= eindDatum);
	}
	
	public String getFundName() {
		return fundName;
	}
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumberOfStocks() {
		return numberOfStocks;
	}
	public void setNumberOfStocks(int numberOfStocks) {
		this.numberOfStocks = numberOfStocks;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}


}
