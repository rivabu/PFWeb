package org.rients.com.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {
	
	public static double roundBigDecimal(BigDecimal value) {

		value = value.setScale(2, RoundingMode.HALF_UP);
		return value.doubleValue();

	}
	
	public static void main(String args[]) {
		BigDecimal a = new BigDecimal("1.2345678");
		double b = roundBigDecimal(a);
		System.out.println(b);
	}

}
