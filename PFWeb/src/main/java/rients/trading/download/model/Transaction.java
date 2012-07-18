/*
 * Created on Aug 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package rients.trading.download.model;

import rients.trading.utils.MathFunctions;

/**
 * @author Rients van Buren
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Transaction {
    public String startDate;
    public String endDate;
    public float startRate;
    public float endRate;
    public String buyComment;
    public String sellComment;
    public int numberOfDays;
    public int type; // 1 = long,  -1=short
    
    public float getScore() {
    	float score = ((endRate - startRate) / endRate) * 100 * type;
    	return score;
    }
    
    
    public String toString() {
        String sep = ",";
        String sign = "+";
        String temp =
            sign+sep+startDate + sep+ endDate + sep + startRate + sep + endRate + sep + type + sep + MathFunctions.round(getScore());

        return temp;
    }
	public String getStartDate() {
    	return startDate;
    }
	public void setStartDate(String startDate) {
    	this.startDate = startDate;
    }
	public String getEndDate() {
    	return endDate;
    }
	public void setEndDate(String endDate) {
    	this.endDate = endDate;
    }
	public float getStartRate() {
    	return startRate;
    }
	public void setStartRate(float startRate) {
    	this.startRate = startRate;
    }
	public float getEndRate() {
    	return endRate;
    }
	public void setEndRate(float endRate) {
    	this.endRate = endRate;
    }
	public String getBuyComment() {
    	return buyComment;
    }
	public void setBuyComment(String buyComment) {
    	this.buyComment = buyComment;
    }
	public String getSellComment() {
    	return sellComment;
    }
	public void setSellComment(String sellComment) {
    	this.sellComment = sellComment;
    }
	public int getNumberOfDays() {
    	return numberOfDays;
    }
	public void setNumberOfDays(int numberOfDays) {
    	this.numberOfDays = numberOfDays;
    }
	public int getType() {
    	return type;
    }
	public void setType(int type) {
    	this.type = type;
    }
 
}
