package rients.trading.download.model;

public class DoubleTopBottom {
    
    public DoubleTopBottom(String fundName, int turningPoint, float stepSize, Modelregel modelRegel) {
        super();
        this.fundName = fundName;
        this.turningPoint = turningPoint;
        this.stepSize = stepSize;
        this.modelRegel = modelRegel;
    }
    private String fundName;
    
    private int turningPoint;
    
    private float stepSize;
    
    private Modelregel modelRegel;

    public String getFundName() {
        return fundName;
    }

    public int getTurningPoint() {
        return turningPoint;
    }

    public float getStepSize() {
        return stepSize;
    }

    public Modelregel getModelRegel() {
        return modelRegel;
    }
    
    public String toString() {
        return "fund: " + fundName + "(" + turningPoint + ", " + stepSize + ") datum: " + modelRegel.getDatum() + " status: " + modelRegel.getStatus() + " kolom: " + modelRegel.getKolomnr();
    }


}
