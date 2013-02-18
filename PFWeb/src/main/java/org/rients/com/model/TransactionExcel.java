package org.rients.com.model;

public class TransactionExcel {
    private int number;
    private int pieces;
    private BuySell buySell;
    private String fundName;
    private int datum;
    private float koers;
    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }
    /**
     * @return the pieces
     */
    public int getPieces() {
        return pieces;
    }
    /**
     * @param pieces the pieces to set
     */
    public void setPieces(int pieces) {
        this.pieces = pieces;
    }
    /**
     * @return the buySell
     */
    public BuySell getBuySell() {
        return buySell;
    }
    /**
     * @param buySell the buySell to set
     */
    public void setBuySell(BuySell buySell) {
        this.buySell = buySell;
    }
    /**
     * @return the fundName
     */
    public String getFundName() {
        return fundName;
    }
    /**
     * @param fundName the fundName to set
     */
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }
    /**
     * @return the datum
     */
    public int getDatum() {
        return datum;
    }
    /**
     * @param datum the datum to set
     */
    public void setDatum(int datum) {
        this.datum = datum;
    }
    /**
     * @return the koers
     */
    public float getKoers() {
        return koers;
    }
    /**
     * @param koers the koers to set
     */
    public void setKoers(float koers) {
        this.koers = koers;
    }
    
}
