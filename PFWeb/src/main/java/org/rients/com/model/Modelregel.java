package org.rients.com.model;
public class Modelregel
{
    private int kolomnr;
    private int rijnr;
    private float koers;
    private String datum;
    private String hoogsteDatum;
    private float hoogsteKoers;
    private String laagsteDatum;
    private float laagsteKoers;
    private String sign;
    private int aantalDagen;
    private float RSI = 0;
    private DagkoersStatus status = DagkoersStatus.DEFAULT;

    public Modelregel()
    {
    }
    
    public String toString() {
    	return datum + " " + sign + " " + koers + " " + rijnr + " " + kolomnr;
    }
    /**
     * @return Returns the aantalDagen.
     */
    public int getAantalDagen() {
        return aantalDagen;
    }
    /**
     * @param aantalDagen The aantalDagen to set.
     */
    public void setAantalDagen(int aantalDagen) {
        this.aantalDagen = aantalDagen;
    }
    /**
     * @return Returns the datum.
     */
    public String getDatum() {
        return datum;
    }
    
    /**
     * @return Returns the datum.
     */
    public int getDatumInt() {
        return Integer.parseInt(datum);
    }
    /**
     * @param datum The datum to set.
     */
    public void setDatum(String datum) {
        this.datum = datum;
    }
    /**
     * @return Returns the koers.
     */
    public float getKoers() {
        return koers;
    }
    /**
     * @param koers The koers to set.
     */
    public void setKoers(float koers) {
        this.koers = koers;
    }
    /**
     * @return Returns the kolomnr.
     */
    public int getKolomnr() {
        return kolomnr;
    }
    /**
     * @param kolomnr The kolomnr to set.
     */
    public void setKolomnr(int kolomnr) {
        this.kolomnr = kolomnr;
    }
    /**
     * @return Returns the rijnr.
     */
    public int getRijnr() {
        return rijnr;
    }
    /**
     * @param rijnr The rijnr to set.
     */
    public void setRijnr(int rijnr) {
        this.rijnr = rijnr;
    }
    /**
     * @return Returns the sign.
     */
    public String getSign() {
        return sign;
    }
    /**
     * @param sign The sign to set.
     */
    public void setSign(String sign) {
        this.sign = sign;
    }
    
    
    public boolean isStijger() {
    	boolean returnValue = false;
    	if(sign.equals("+") || sign.equals("x")) {
    		returnValue = true;
    	}
    	return returnValue;
    }

	public DagkoersStatus getStatus() {
		return status;
	}

	public void setStatus(DagkoersStatus status) {
		this.status = status;
	}

    public String getHoogsteDatum() {
        return hoogsteDatum;
    }

    public void setHoogsteDatum(String hoogsteDatum) {
        this.hoogsteDatum = hoogsteDatum;
    }

    public float getHoogsteKoers() {
        return hoogsteKoers;
    }

    public void setHoogsteKoers(float hoogsteKoers) {
        this.hoogsteKoers = hoogsteKoers;
    }

    public String getLaagsteDatum() {
        return laagsteDatum;
    }

    public void setLaagsteDatum(String laagsteDatum) {
        this.laagsteDatum = laagsteDatum;
    }

    public float getLaagsteKoers() {
        return laagsteKoers;
    }

    public void setLaagsteKoers(float laagsteKoers) {
        this.laagsteKoers = laagsteKoers;
    }

    public void setRSI(float rSI) {
        RSI = rSI;
    }

    public float getRSI() {
        return RSI;
    }
}
