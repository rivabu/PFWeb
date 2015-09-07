package org.rients.com.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class HistoricalVotalityAvr extends HistoricalVotality {

	private final List<BigDecimal> allValues = new ArrayList<BigDecimal>();
	private final int length;
	private BigDecimal sum = BigDecimal.ZERO;
	private BigDecimal defValue = BigDecimal.ZERO;
	private int count;

	/**
	 * 
	 * @param length
	 *            the maximum length
	 */
	public HistoricalVotalityAvr(final int length, int defaultValue) {
		super(length, defaultValue);
		defValue = new BigDecimal(new Double(defaultValue).doubleValue());
		if (length <= 0) {
			throw new IllegalArgumentException(
					"length must be greater than zero");
		}

		this.length = length;
	}


	/**
	 * Compute the HistoricalVotality. Synchronised so that no changes in the
	 * underlying data is made during calculation.
	 * 
	 * @param value
	 *            The value
	 * @return The average
	 */
	public BigDecimal computeAvr(final BigDecimal value) {
		BigDecimal result = compute(value);
		if ((values.size() == length) && (length > 0)) {
			sum = sum.add(result);
			allValues.add(result);
			count++;
		}
		return result;

	}

	public BigDecimal getAvr() {
	    if (count != 0) {
	        return BigDecimal.ZERO;
	    }
		return sum.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
	}

	public BigDecimal getMeridiaanHigh(int perc) {
		Collections.sort(allValues);
		int aantal = allValues.size();
		int top = aantal - ((aantal / 100) * perc);
		return allValues.get(top);
	}

	public BigDecimal getMeridiaanLow(int perc) {
		Collections.sort(allValues);
		int aantal = allValues.size();
		int bottom = ((aantal / 100) * perc);
		return allValues.get(bottom);
	}
	
	
}
