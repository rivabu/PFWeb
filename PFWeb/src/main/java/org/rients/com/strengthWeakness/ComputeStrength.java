package org.rients.com.strengthWeakness;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

import org.rients.com.utils.Formula;
import org.rients.com.utils.MathFunctions;

public class ComputeStrength implements Formula {

	private final LinkedList<BigDecimal> values = new LinkedList<BigDecimal>();
	private final int length;

	/**
	 * 
	 * @param length
	 *            the maximum length
	 */
	public ComputeStrength(final int length) {
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
		double total = 0;
		double result = 0;
        //double totalLosses = 0;
        double FACTOR = 1.1d;
        double vermenigvuldingsFactor = 1d;
        double vermenigvuldingsFactorCount = 0d;
        if ((values.size() == length) && (length > 0)) {
        	// begin algoritme
            for (int i = 0; i < length; i++) {
                BigDecimal currentValue = values.get(i);
                double procVerschil = MathFunctions.procVerschil(currentValue.doubleValue(), value.doubleValue());
                vermenigvuldingsFactorCount = vermenigvuldingsFactorCount + vermenigvuldingsFactor;
                procVerschil = procVerschil * vermenigvuldingsFactor;
               	total = total + procVerschil;
                vermenigvuldingsFactor = vermenigvuldingsFactor * FACTOR;
                
            }
            result = total / vermenigvuldingsFactorCount;
        }
        handleNewValue(value);
        return new BigDecimal(result).round(MathContext.DECIMAL32);
	}

	private void handleNewValue(final BigDecimal value) {
		if ((values.size() == length) && (length > 0)) {
            values.removeFirst();
        }
        values.add(value);
	}
}
