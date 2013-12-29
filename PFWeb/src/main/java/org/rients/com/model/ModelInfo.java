/*
 * Created on Aug 4, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rients.com.model;

/**
 * @author Rients van Buren
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ModelInfo {
    private int highestModelValue = 0;
    private int lowestModelValue = 0;
    private int firstModelRule = 0;
    private int stopAtColumnNumber = 0;
    private int maxColumnNumber = 0;
    private String lastDate;
    /**
     * @return Returns the highestModelValue.
     */
    public int getHighestModelValue() {
        return highestModelValue;
    }
    /**
     * @param highestModelValue The highestModelValue to set.
     */
    public void setHighestModelValue(int highestModelValue) {
        this.highestModelValue = highestModelValue;
    }
    /**
     * @return Returns the lowestModelValue.
     */
    public int getLowestModelValue() {
        return lowestModelValue;
    }
    /**
     * @param lowestModelValue The lowestModelValue to set.
     */
    public void setLowestModelValue(int lowestModelValue) {
        this.lowestModelValue = lowestModelValue;
    }
    /**
     * @return Returns the firstModelRule.
     */
    public int getFirstModelRule() {
        return firstModelRule;
    }
    /**
     * @param firstModelRule The firstModelRule to set.
     */
    public void setFirstModelRule(int firstModelRule) {
        this.firstModelRule = firstModelRule;
    }
    /**
     * @return Returns the stopAtColumnNumber.
     */
    public int getStopAtColumnNumber() {
        return stopAtColumnNumber;
    }
    /**
     * @param stopAtColumnNumber The stopAtColumnNumber to set.
     */
    public void setStopAtColumnNumber(int stopAtColumnNumber) {
        this.stopAtColumnNumber = stopAtColumnNumber;
    }
	public int getMaxColumnNumber() {
    	return maxColumnNumber;
    }
	public void setMaxColumnNumber(int maxColumnNumber) {
    	this.maxColumnNumber = maxColumnNumber;
    }
	public String getLastDate() {
		return lastDate;
	}
	public void setLastDate(String lastDate) {
		this.lastDate = lastDate;
	}
}
