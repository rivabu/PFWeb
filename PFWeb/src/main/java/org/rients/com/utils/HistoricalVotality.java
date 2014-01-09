package org.rients.com.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class HistoricalVotality implements Graph {

	private final LinkedList<BigDecimal> values = new LinkedList<BigDecimal>();
	private final int length;

	/**
	 * 
	 * @param length
	 *            the maximum length
	 */
	public HistoricalVotality(final int length) {
		if (length <= 0) {
			throw new IllegalArgumentException(
					"length must be greater than zero");
		}

		this.length = length;
	}

	/**
	 * Compute the moving average. Synchronised so that no changes in the
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
		if ((values.size() == length) && (length > 0)) {
			List<Double> priceChanges = new ArrayList<Double>();
			BigDecimal lastPrice = BigDecimal.ZERO;
			for (int x = 0; x < length; x++) {
				if (x == 0) {
					priceChanges.add(0d);
					lastPrice = values.get(x);
				} else {
					double priceChange = Math
							.log(values.get(x).doubleValue() / lastPrice.doubleValue());
					priceChanges.add(priceChange);
					lastPrice = values.get(x);
				}
			}
			Double[] priceChangesArr = priceChanges.toArray(new Double[0]);
			StandardDeviation stdDev = new StandardDeviation();
			double stdDevTmp = stdDev.evaluate(ArrayUtils
					.toPrimitive(priceChangesArr));
			double retTemp = stdDevTmp * Math.sqrt(length) * 500;
			return new BigDecimal(retTemp);

		}
		return BigDecimal.ZERO;
	}

//	public float getHistoricalVolatility(List<> stockPrices, int idx,
//			int period, int tradingDays) {
//		if (period == 0) {
//			period = 20;
//		}
//		if (tradingDays == 0) {
//			tradingDays = 252;
//		}
//
//		if (idx >= period) { // User 'period', instead of (period -1) since the
//								// first calc is period+1
//			List<Double> priceChanges = new ArrayList<Double>();
//			float lastPrice = 0;
//			for (int x = 0; x < stockPrices.size(); x++) {
//				if (x == 0) {
//					priceChanges.add(0.0);
//					lastPrice = stockPrices.get(x).closing_price;
//				} else {
//					double priceChange = Math
//							.log(stockPrices.get(x).closing_price / lastPrice);
//					priceChanges.add(priceChange);
//					lastPrice = stockPrices.get(x).closing_price;
//				}
//			}
//			// Grab an array of price changes over the selected period
//			int tmp_price_changes_st = idx - (period - 1);
//			int tmp_price_changes_end = tmp_price_changes_st + period;
//			List<Double> tmp_price_changes = priceChanges.subList(
//					tmp_price_changes_st, tmp_price_changes_end);
//			Double[] priceChangesArr = tmp_price_changes.toArray(new Double[0]);
//			StandardDeviation stdDev = new StandardDeviation();
//			double stdDevTmp = stdDev.evaluate(ArrayUtils
//					.toPrimitive(priceChangesArr));
//			double retTemp = stdDevTmp * Math.sqrt(tradingDays);
//			return (float) retTemp;
//		} else {
//			return 0.0f;
//		}
//	}
}
