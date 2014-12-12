package org.rients.com.utils;

import java.math.BigDecimal;

public interface Formula {

	/**
	 * Compute the moving average. Synchronised so that no changes in the
	 * underlying data is made during calculation.
	 * 
	 * @param value
	 *            The value
	 * @return The average
	 */
	public abstract BigDecimal compute(BigDecimal value);
	
	public abstract BigDecimal getAvr();

}