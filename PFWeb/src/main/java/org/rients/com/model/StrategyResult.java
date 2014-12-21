package org.rients.com.model;

import org.rients.com.utils.MathFunctions;

public class StrategyResult {

	private int numberOfTransactions;
	private double endResultPerc;
	private double endResult;
	private double avrResult;
	private double avrWin;
	private double avrLoss;
	private double maxProfit;
	private double maxLoss;
	private int numberOfWinners;
	private int numberOfLosers;
	
	public String toString() {
		return "numberOfTransactions: " + numberOfTransactions
				+ ", endResultPerc: " + MathFunctions.round(endResultPerc, 2) +  " %"
				+ ", endResult: " + MathFunctions.round(endResult, 2)
				+ ", avrResult: " + MathFunctions.round(avrResult, 2)
				+ ", avrWin: " + MathFunctions.round(avrWin, 2) + " %"
				+ ", avrLoss: " + MathFunctions.round(avrLoss, 2) + " %"
				+ ", maxProfit: " + MathFunctions.round(maxProfit, 2) + " %"
				+ ", maxLoss: " + MathFunctions.round(maxLoss, 2) + " %"
				+ ", numberOfWinners: " + numberOfWinners
				+ ", numberOfLosers: " + numberOfLosers;
	}
	public int getNumberOfTransactions() {
		return numberOfTransactions;
	}
	public void setNumberOfTransactions(int numberOfTransactions) {
		this.numberOfTransactions = numberOfTransactions;
	}
	public double getEndResult() {
		return endResult;
	}
	public void setEndResult(double endResult) {
		this.endResult = endResult;
	}
	public double getAvrResult() {
		return avrResult;
	}
	public void setAvrResult(double avrResult) {
		this.avrResult = avrResult;
	}
	public double getMaxProfit() {
		return maxProfit;
	}
	public void setMaxProfit(double maxProfit) {
		this.maxProfit = maxProfit;
	}
	public double getMaxLoss() {
		return maxLoss;
	}
	public void setMaxLoss(double maxLoss) {
		this.maxLoss = maxLoss;
	}
	public int getNumberOfWinners() {
		return numberOfWinners;
	}
	public void setNumberOfWinners(int numberOfWinners) {
		this.numberOfWinners = numberOfWinners;
	}
	public int getNumberOfLosers() {
		return numberOfLosers;
	}
	public void setNumberOfLosers(int numberOfLosers) {
		this.numberOfLosers = numberOfLosers;
	}
	public double getAvrWin() {
		return avrWin;
	}
	public void setAvrWin(double avrWin) {
		this.avrWin = avrWin;
	}
	public double getAvrLoss() {
		return avrLoss;
	}
	public void setAvrLoss(double avrLoss) {
		this.avrLoss = avrLoss;
	}
	public double getEndResultPerc() {
		return endResultPerc;
	}
	public void setEndResultPerc(double endResultPerc) {
		this.endResultPerc = endResultPerc;
	}
	
}
