package rients.trading.download.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClosedTransaction {
    
    public List<ClosedTransaction> sort(List<ClosedTransaction> transactions) {
        Collections.sort((ArrayList<ClosedTransaction>) transactions, new ClosedTransactionSort());
        return transactions;
    }

    public class ClosedTransactionSort implements Comparator<ClosedTransaction> {
        
        public int compare(ClosedTransaction obj1, ClosedTransaction obj2) {
            Integer key1 = new Integer(obj1.getStartDate());
            Integer key2 = new Integer(obj2.getStartDate());
            return key1.compareTo(key2);
            
        }

    }

    String startDate;
    String endDate;
    String fundName;
    LongShort longShort;
    
    public ClosedTransaction() {
        super();
    }
    public ClosedTransaction(String fundName, String startDate, String endDate, String type) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
        this.fundName = fundName;
        this.longShort = LongShort.valueOf(type);
    }
    
    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }
    
    public int getStartDateInt() {
        return Integer.parseInt(startDate);
    }
    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }
    
    public int getEndDateInt() {
        return Integer.parseInt(endDate);
    }
    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
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
     * @return the longShort
     */
    public LongShort getLongShort() {
        return longShort;
    }
    /**
     * @param longShort the longShort to set
     */
    public void setLongShort(LongShort longShort) {
        this.longShort = longShort;
    }
}
