package org.rients.com.pfweb.performancepermonth;

import java.util.List;

public class InputParameters {

    private List<Integer> longMonths;
    private float stepSize;
    private int turningPoint;
    private int stopLoss;
    

    /**
     * @return the longMonths
     */
    public List<Integer> getLongMonths() {
        return longMonths;
    }
    /**
     * @param longMonths the longMonths to set
     */
    public void setLongMonths(List<Integer> longMonths) {
        this.longMonths = longMonths;
    }
    /**
     * @return the stepSize
     */
    public float getStepSize() {
        return stepSize;
    }
    /**
     * @param stepSize the stepSize to set
     */
    public void setStepSize(float stepSize) {
        this.stepSize = stepSize;
    }
    /**
     * @return the turningPoint
     */
    public int getTurningPoint() {
        return turningPoint;
    }
    /**
     * @param turningPoint the turningPoint to set
     */
    public void setTurningPoint(int turningPoint) {
        this.turningPoint = turningPoint;
    }
    /**
     * @return the stopLoss
     */
    public int getStopLoss() {
        return stopLoss;
    }
    /**
     * @param stopLoss the stopLoss to set
     */
    public void setStopLoss(int stopLoss) {
        this.stopLoss = stopLoss;
    }
}
