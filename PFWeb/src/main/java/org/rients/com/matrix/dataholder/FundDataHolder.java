package org.rients.com.matrix.dataholder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FundDataHolder {
    private String fundName;
    private Map<String, Integer> data = new TreeMap<String, Integer>();
    //int[] data;
    
    public FundDataHolder(String fundName, int days) {
        this.setFundName(fundName);
    }
    
    public void addValue(String date, int value) {
        data.put(new String(date), new Integer(value));
    }
    
    public int getValue(String date) {
        if (data.containsKey(date)) {
            return ((Integer) data.get(date)).intValue();
        }
        return 0;
    }
    
    public int getSize() {
        return data.size();
    }
    
    public Set<String> getDates() {
        return data.keySet();
    }
    
    public Collection<Integer> getValues() {
        return data.values();
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getFundName() {
        return fundName;
    }
}
