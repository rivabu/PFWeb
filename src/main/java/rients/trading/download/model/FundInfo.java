package rients.trading.download.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class FundInfo.
 *
 * @author Rients
 */
public class FundInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /** The fund name. */
    private String fundName;
    
    /** The first date. */
    private String firstDate;
    
    /** The div name. */
    private String divName;
    
    /**
     * Gets the fund name.
     *
     * @return the fund name
     */
    public String getFundName() {
        return fundName;
    }
    
    /**
     * Sets the fund name.
     *
     * @param fundName the new fund name
     */
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }
    
    /**
     * Gets the first date.
     *
     * @return the first date
     */
    public String getFirstDate() {
        return firstDate;
    }
    
    /**
     * Sets the first date.
     *
     * @param firstDate the new first date
     */
    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }
    
    /**
     * Gets the div name.
     *
     * @return the div name
     */
    public String getDivName() {
        return divName;
    }
    
    /**
     * Sets the div name.
     *
     * @param divName the new div name
     */
    public void setDivName(String divName) {
        this.divName = divName;
    }
}
