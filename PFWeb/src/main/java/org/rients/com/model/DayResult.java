package org.rients.com.model;

public class DayResult {
    private String date;
    private float benchMark;
    private float koers;
    /**
     * @return the koers
     */
    public float getKoers() {
        return koers;
    }

    public static String separator = ",";
    
    public DayResult(String date, float benchMark, float koers) {
        super();
        this.date = date;
        this.benchMark = benchMark;
        this.koers = koers;
    }
    
    public String toString() {
        return this.date + separator + this.benchMark + separator + this.koers; 
    }
    
}
