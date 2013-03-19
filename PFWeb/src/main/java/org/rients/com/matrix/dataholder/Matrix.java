package org.rients.com.matrix.dataholder;

import java.util.List;

import org.rients.com.model.Dagkoers;


public class Matrix {

    String[] dates = null;
    FundDataHolder[] fundData = null;
    int aantalFunds = 0;
    
    public Matrix(int aantalFunds, int aantalDays) {
        super();
        fundData = new FundDataHolder[aantalFunds];
        dates = new String[aantalDays];
        this.aantalFunds = aantalFunds;
    }
    
    
    public int getMaxFundnameLength() {
        int maxLength = 0;
        for (int i = 0; i < this.getAantalFunds(); i++) {
            if (this.getFundData(i).getFundName().length() > maxLength) {
                maxLength = this.getFundData(i).getFundName().length();
            }
        }
        return maxLength;
    }
    
    public void fillDates(List<Dagkoers> aexRates) {
        int counter = 0;
        for (Dagkoers dagkoers : aexRates) {
            dates[counter] = dagkoers.getDatum();
            counter++;
        }
    }
    
    public FundDataHolder getFundData(int number) {
        return fundData[number];
    }
    
    public void setFundData(FundDataHolder data, int number) {
        fundData[number] = data;
    }
    
    public int getAantalFunds() {
        return this.aantalFunds;
    }
}
