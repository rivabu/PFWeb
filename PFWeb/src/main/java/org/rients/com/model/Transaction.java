/*
 * Created on Aug 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.model;

import org.rients.com.utils.MathFunctions;

/**
 * @author Rients van Buren
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Transaction {
    public String fundName;
    public int startDate;
    public int endDate;
    public float startRate;
    public float endRate;
    public int numberOfDays;
    public Type type; // LONG, SHORT
    public int pieces;
    
    public float getScorePerc() {
        int value = 1;
        if (type == Type.SHORT) {
            value = -1;
        }
    	float score = ((endRate - startRate) / endRate) * 100 * value;
    	return score;
    }

    public float getScoreAbs() {

        float score = (endRate - startRate)  * pieces;
        return score;
    }

    
    
    public String toString() {
        String sep = ",";
        String sign = "+";
        String temp =
            sign+sep+startDate + sep+ endDate + sep + startRate + sep + endRate + sep + type + sep + MathFunctions.round(getScorePerc());

        return temp;
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

	public int getNumberOfDays() {
    	return numberOfDays;
    }
	public void setNumberOfDays(int numberOfDays) {
    	this.numberOfDays = numberOfDays;
    }
	public Type getType() {
    	return type;
    }
	public void setType(Type type) {
    	this.type = type;
    }


    /**
     * @return the startDate
     */
    public int getStartDate() {
        return startDate;
    }


    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }


    /**
     * @return the endDate
     */
    public int getEndDate() {
        return endDate;
    }


    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }


    /**
     * @return the fundName
     */
    public String getFundName() {
        return fundName;
    }


    /**
     * @param fundName the fundName to set
     */
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }


    /**
     * @return the pieces
     */
    public int getPieces() {
        return pieces;
    }


    /**
     * @param pieces the pieces to set
     */
    public void setPieces(int pieces) {
        this.pieces = pieces;
    }
 
}
