package org.rients.com.matrix.dataholder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.rients.com.strengthWeakness.StrengthWeakness;
import org.rients.com.utils.MathFunctions;

public class FundDataHolder {
    private String columnName;
    private boolean toGraph;
    private boolean isKoers;
    private Object firstValue;
    
    private Map<String, Object> data = new TreeMap<String, Object>();
    
    public FundDataHolder(String columnName, boolean toGraph) {
        this.setColumnName(columnName);
        this.columnName = columnName;
        this.toGraph = toGraph;
    }
    
    public void addValue(String date, Object value) {
    	if (isKoers && data.isEmpty()) {
    		firstValue = value;
    	}
        data.put(new String(date), value);
    }
    
    public Object getValue(String date) {
        if (data.containsKey(date)) {
            return data.get(date);
        }
        return 0;
    }
    
    public String getValueAsString(String date) {
    	String returnValue = "";

        if (data.containsKey(date)) {
        	if (!isKoers || firstValue == null) {
        		try {
        			Object value = data.get(date);
        			double koers = 0d;
        			if (value instanceof StrengthWeakness) {
        				koers = ((StrengthWeakness) value).getKoers();
        			}
               		returnValue = MathFunctions.round(new Double(koers).doubleValue(), 2) + "";
        		} catch (NumberFormatException nfe) {
        			System.out.println("columnName: " + columnName + " date: " + date + " value: " + ((StrengthWeakness) data.get(date)).toString());
        		}
        	} else {
        		returnValue = data.get(date).toString() + ", " + MathFunctions.round(100 * MathFunctions.procVerschil(firstValue.toString(), data.get(date).toString()), 2);
        	}
        }
        return returnValue;
    }

    public double getValueAsDouble(String date, String fundname) {
    	Double d = 0d;
    	if (data.containsKey(date)) {
    		try {
    			Object value = data.get(date);
    			double koers = 0d;
    			if (value instanceof StrengthWeakness) {
    				koers = ((StrengthWeakness) value).getKoers();
    			}
    			d = new Double(koers).doubleValue();
    		}
            catch (NumberFormatException nfe)  {
            	System.out.println("NFE: " + fundname + " " + date + " " + ((StrengthWeakness) data.get(date)).toString());
            }
        }
        return d;
    }
    
    public int getValueAsInt(String date) {
    	if (data.containsKey(date)) {
            return new Integer(data.get(date).toString()).intValue();
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

	public boolean isKoers() {
		return isKoers;
	}

	public void setKoers(boolean isKoers) {
		this.isKoers = isKoers;
	}
}
