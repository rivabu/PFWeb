package org.rients.com.strengthWeakness;

public class StrengthWeakness {
	public StrengthWeakness(String datum, float koers, double strength) {
		super();
		this.datum = datum;
		this.koers = koers;
		this.strength = strength;
	}
	String datum;
	float koers;
	double strength;
	
	@Override
	public String toString() {
		return ""+koers+","+strength;
	}
	
	
	
}
