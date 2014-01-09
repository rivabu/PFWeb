package org.rients.com.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

public class RSI implements Graph {

    private final LinkedList<BigDecimal> values = new LinkedList<BigDecimal>();
    private final int length;

    /**
     * 
     * @param length
     *            the maximum length
     */
    public RSI(final int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than zero");
        }

        this.length = length;
    }


    /* (non-Javadoc)
	 * @see org.rients.com.utils.Graph#compute(java.math.BigDecimal)
	 */
    public BigDecimal compute(final BigDecimal value) {
        double totalGain = 0;
        double totalLosses = 0;
        double FACTOR = 1.1d;
        double vermenigvuldingsFactor = 1d;
        double vermenigvuldingsFactorCount = 0d;
        BigDecimal RSIvalue = BigDecimal.ZERO;
        boolean first = true;
        BigDecimal previousValue = BigDecimal.ZERO;
        if ((values.size() == length) && (length > 0)) {
            values.removeFirst();
        }
        values.add(value);
        if ((values.size() == length) && (length > 0)) {
        	// begin algoritme
            for (int i = 0; i < length; i++) {
                BigDecimal currentValue = values.get(i);
                if (!first) {
                    double procVerschil = MathFunctions.procVerschil(previousValue.doubleValue(), currentValue.doubleValue());
                    vermenigvuldingsFactorCount = vermenigvuldingsFactorCount + vermenigvuldingsFactor;
                    procVerschil = procVerschil * vermenigvuldingsFactor;
                    if (procVerschil > 0) {
                        totalGain = totalGain + procVerschil;
                    }
                    if (procVerschil < 0) {
                        totalLosses = totalLosses + procVerschil;
                    }
                }
                first = false;
                previousValue = currentValue;
                vermenigvuldingsFactor = vermenigvuldingsFactor * FACTOR;
                
            }
            double avrGain = totalGain / vermenigvuldingsFactorCount;
            double avrLoss = (totalLosses / vermenigvuldingsFactorCount) * -1;
            double RS = 100;
            if (avrLoss != 0) {
                RS = avrGain / avrLoss;
            }
            double RSIDouble = 100 - (100 / (RS + 1));
            RSIvalue = new BigDecimal(RSIDouble);
            // end algorithme

        }
        return RSIvalue.round(MathContext.DECIMAL32);
    }
}
