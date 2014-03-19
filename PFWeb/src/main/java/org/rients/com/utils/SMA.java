package org.rients.com.utils;

import java.math.BigDecimal;
import java.util.LinkedList;


public class SMA implements Formula {
	
    private final LinkedList values = new LinkedList();
    private final int length;
    private BigDecimal sum = BigDecimal.ZERO;
    private BigDecimal average = BigDecimal.ZERO;

    /**
     *
     * @param length the maximum length
     */
    public SMA(final int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than zero");
        }

        this.length = length;
    }

    public BigDecimal currentAverage() {
        return average;
    }

    /**
     * Compute the moving average.
     * Synchronised so that no changes in the underlying data is made during calculation.
     * @param value The value
     * @return The average
     */
    public BigDecimal compute(final BigDecimal value) {
    	synchronized(values) {
	        if ((values.size() == length) && (length > 0)) {
	            sum = sum.subtract((BigDecimal) values.getFirst());
	            values.removeFirst();
	        }
	
	        sum = sum.add(value);
	        values.addLast(value);
	        average = sum.divide(new BigDecimal(values.size()), length);
	
	        return average;
    	}
    }
}
