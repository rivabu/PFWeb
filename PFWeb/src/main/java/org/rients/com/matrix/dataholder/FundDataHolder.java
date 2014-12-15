package org.rients.com.matrix.dataholder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rients.com.strengthWeakness.StrengthWeakness;

public class FundDataHolder {
    private String columnName;
    private boolean toGraph;
    private Map<String, Object> data = new TreeMap<String, Object>();
    
    public FundDataHolder(String fundName, int days, boolean toGraph) {
        this.setColumnName(fundName);
        this.toGraph = toGraph;
    }
    
    public void addValue(String date, Object value) {
        data.put(new String(date), value);
    }
    
    public Object getValue(String date) {
        if (data.containsKey(date)) {
            return data.get(date);
        }
        return 0;
    }
    
    public String getValueAsString(String date) {
        if (data.containsKey(date)) {
            return data.get(date).toString();
        }
        return "";
    }

    public float getKoers(String date) {
        if (data.containsKey(date)) {
        	if (data.get(date) instanceof StrengthWeakness) {
        		return ((StrengthWeakness) data.get(date)).getKoers();
        	}
        }
        return 0;
    }
    
    
    public int getSize() {
        return data.size();
    }
    
    public Set<String> getDates() {
        return data.keySet();
    }
    
    public Collection<Object> getValues() {
        return data.values();
    }

    public void setColumnName(String fundName) {
        this.columnName = fundName;
    }

    public String getColumnName() {
        return columnName;
    }

	public boolean isToGraph() {
		return toGraph;
	}

	public void setToGraph(boolean toGraph) {
		this.toGraph = toGraph;
	}
}
