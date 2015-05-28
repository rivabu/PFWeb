/*
 * Created on Aug 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rients.com.utils.MathFunctions;

/**
 * @author Rients van Buren
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Transaction {
    public int buyId;
    public int sellId;
    public String fundName;
    public String realFundName;
    public int startDate;
    public int endDate;
    public float startRate;
    public float endRate;
    public int numberOfDays;
    public Type type; // LONG, SHORT
    public int pieces;
    public float maxLossPerc;
    
    public float determineMaxLoss(float currentKoers) {
        double procVerschil = MathFunctions.procVerschil(startRate, currentKoers);
        if (type == Type.SHORT) {
            procVerschil = procVerschil * -1;
        }
        if (type == Type.LONG && procVerschil < maxLossPerc) {
            maxLossPerc = (float) procVerschil;
        }
        if (type == Type.SHORT && procVerschil < maxLossPerc) {
            maxLossPerc = (float) procVerschil;
        }
        return maxLossPerc;
    }
    /**
     * @return the maxLossPerc
     */
    public float getMaxLossPerc() {
        return maxLossPerc;
    }

    /**
     * @param maxLossPerc the maxLossPerc to set
     */
    public void setMaxLossPerc(float maxLossPerc) {
        this.maxLossPerc = maxLossPerc;
    }

    public Transaction() {
        super();
    }
    
    public Transaction(String fundName, int startDate, int buyId, float startRate, int pieces, Type type) {
        super();
        this.fundName = fundName;
        this.startDate = startDate;
        this.buyId = buyId;
        this.startRate = startRate;
        this.type = type;
        this.pieces = pieces;
    }
    
    public void addSellInfo(int endDate, int sellId, float endRate) {
        this.endDate = endDate;
        this.sellId = sellId;
        this.endRate = endRate;
    }
    
    public float getScorePerc() {
    	if (startRate == 0) {
    		return 0;
    	}
    	float score = ((endRate - startRate) / startRate) * 100;
        if (type == Type.SHORT) {
        	score = score * -1;
        }
    	return score;
    }

    public float getScoreAbs() {
        float score = (endRate - startRate)  * new Double(pieces).floatValue();
        if (type == Type.SHORT) {
        	score = score * -1;
        }
        return score;
    }

    public String getScorePercStr() {
        float score = 0;
        if (startRate != 0) {
            score = ((endRate - startRate) / startRate) * 100;
        } else {
            score = 0;
        }
        if (type == Type.SHORT) {
        	score = score * -1;
        }
        return MathFunctions.round(score);
    }
    
    public BigDecimal getScorePercBD() {
        if (startRate != 0) {
            float score = ((endRate - startRate) / startRate) * 100;
            if (type == Type.SHORT) {
            	score = score * -1;
            }
            return new BigDecimal(MathFunctions.round(score));
        }
        return new BigDecimal(0);
    }
    

    public String getScoreAbsStr() {
        float score = 0;
        if (endRate != 0) {
            score = (endRate - startRate)  * new Double(pieces).floatValue();
            if (type == Type.SHORT) {
            	score = score * -1;
            }
        } else {
            score = 0;
        }
        return MathFunctions.round(score);
    }
    
    public BigDecimal getScoreAbsBD() {
        if (endRate != 0) {
            float score = (endRate - startRate)  * new Double(pieces).floatValue();
            if (type == Type.SHORT) {
            	score = score * -1;
            }
            return new BigDecimal(MathFunctions.round(score));
        }
        return new BigDecimal(0);
    }
    
    public String toString() {
        String sep = ",";

        String string =
            startDate + sep + endDate + sep + pieces + sep + fundName +  sep + buyId + sep + startRate + sep + endRate +  sep + type + sep + getScorePercStr() + sep + getScoreAbsStr() + sep + getMaxLossPerc();

        return string;
    }

    public List<Transaction> sort(List<Transaction> transactions) {
        Collections.sort((ArrayList<Transaction>) transactions, new TransactionComparator());
        return transactions;
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

    /**
     * @return the buyId
     */
    public int getBuyId() {
        return buyId;
    }

    /**
     * @param buyId the buyId to set
     */
    public void setBuyId(int buyId) {
        this.buyId = buyId;
    }

    /**
     * @return the sellId
     */
    public int getSellId() {
        return sellId;
    }

    /**
     * @param sellId the sellId to set
     */
    public void setSellId(int sellId) {
        this.sellId = sellId;
    }
    
    public String getDummy() {
        return "show graph";
    }

    /**
     * @return the realFundName
     */
    public String getRealFundName() {
        return realFundName;
    }

    /**
     * @param realFundName the realFundName to set
     */
    public void setRealFundName(String realFundName) {
        this.realFundName = realFundName;
    }
 
}
