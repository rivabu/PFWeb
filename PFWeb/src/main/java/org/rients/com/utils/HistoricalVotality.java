package org.rients.com.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class HistoricalVotality implements Formula {

	protected final LinkedList<BigDecimal> values = new LinkedList<BigDecimal>();
	private final int length;
	private BigDecimal defValue = BigDecimal.ZERO;

	/**
	 * 
	 * @param length
	 *            the maximum length
	 */
	public HistoricalVotality(final int length, double defaultValue) {
		defValue = new BigDecimal(defaultValue);
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
	public BigDecimal compute(final BigDecimal value) {
		if ((values.size() == length) && (length > 0)) {
			values.removeFirst();
		}
		values.add(value);
        double FACTOR = 1.1d;
        double vermenigvuldingsFactor = 1d;
        double vermenigvuldingsFactorCount = 0d;

		if ((values.size() == length) && (length > 0)) {
			List<Double> priceChanges = new ArrayList<Double>();
			BigDecimal lastPrice = BigDecimal.ZERO;
			for (int x = 0; x < length; x++) {
				if (x == 0) {
					priceChanges.add(0d);
					lastPrice = values.get(x);
				} else {
					double priceChange = Math
							.log(values.get(x).doubleValue() / lastPrice.doubleValue()) * vermenigvuldingsFactor;
					priceChanges.add(priceChange);
					lastPrice = values.get(x);
					vermenigvuldingsFactorCount = vermenigvuldingsFactorCount + vermenigvuldingsFactor;
				}
                vermenigvuldingsFactor = vermenigvuldingsFactor * FACTOR;
			}
			Double[] priceChangesArr = priceChanges.toArray(new Double[0]);
			StandardDeviation stdDev = new StandardDeviation();
			double stdDevTmp = stdDev.evaluate(ArrayUtils
					.toPrimitive(priceChangesArr));
			double retTemp = stdDevTmp * Math.sqrt(length) * 500;
			BigDecimal result = new BigDecimal(Math.sqrt(retTemp));
			return result;

		}
		return defValue;
	}

}
