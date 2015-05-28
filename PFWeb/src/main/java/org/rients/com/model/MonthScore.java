package org.rients.com.model;

public class MonthScore {

    public MonthScore(int month, int number, float totalScore) {
        super();
        this.month = month;
        this.number = number;
        this.totalScore = totalScore;
    }
    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }
    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }
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
     * @return the totalScore
     */
    public float getTotalScore() {
        return totalScore;
    }
    /**
     * @param totalScore the totalScore to set
     */
    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }
    int month;
    int number;
    float totalScore;
}
