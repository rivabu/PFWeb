package org.rients.com.model;

public class DayResult {
    
    private String date;
    private float benchMark;
    private float koers;
    private float cummKoers;
    private float cummRel;

    public DayResult(float benchMark, float koers) {
        super();
        this.benchMark = benchMark;
        this.koers = koers;
    }
    
    public DayResult(String date, float benchMark, float koers, float cummKoers, float cummRel) {
        super();
        this.date = date;
        this.benchMark = benchMark;
        this.koers = koers;
        this.cummKoers = cummKoers;
        this.setCummRel(cummRel);
    }

    private String month;
    /**
     * @return the month
     */
    public String getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(String month) {
        this.month = month;
    }


    /**
     * @return the koers
     */
    public float getKoers() {
        return koers;
    }

    public static String separator = ",";
    

    
    public String toString() {
        String returnValue = "";
        if (date != null) {
            returnValue = this.date + separator + this.benchMark + separator + this.koers + separator + this.cummKoers + separator + this.cummRel; 
        }
        if (month != null) {
            returnValue = this.month + separator + this.benchMark + separator + this.koers + separator + this.cummKoers + separator + this.cummRel; 
        }
        return returnValue;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @return the benchMark
     */
    public float getBenchMark() {
        return benchMark;
    }

    /**
     * @return the separator
     */
    public static String getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public static void setSeparator(String separator) {
        DayResult.separator = separator;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @param benchMark the benchMark to set
     */
    public void setBenchMark(float benchMark) {
        this.benchMark = benchMark;
    }

    /**
     * @param koers the koers to set
     */
    public void setKoers(float koers) {
        this.koers = koers;
    }

    /**
     * @return the cummKoers
     */
    public float getCummKoers() {
        return cummKoers;
    }

    /**
     * @param cummKoers the cummKoers to set
     */
    public void setCummKoers(float cummKoers) {
        this.cummKoers = cummKoers;
    }

    /**
     * @return the cummRel
     */
    public float getCummRel() {
        return cummRel;
    }

    /**
     * @param cummRel the cummRel to set
     */
    public void setCummRel(float cummRel) {
        this.cummRel = cummRel;
    }
    
}