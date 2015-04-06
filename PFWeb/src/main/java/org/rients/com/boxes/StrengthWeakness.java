package org.rients.com.boxes;

public class StrengthWeakness {
	public StrengthWeakness(String datum, float koers, double strength) {
		super();
		this.datum = datum;
		this.koers = koers;
		this.strength = strength;
	}

	String datum;
	public float koers;
	public double strength;
	
	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public float getKoers() {
		return koers;
	}

	public void setKoers(float koers) {
		this.koers = koers;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}
	
	public String toString() {
		return ""+koers+","+strength;
	}
	
	
	
}
