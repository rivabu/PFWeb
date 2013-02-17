package org.rients.com.model;

public class DoubleTopBottom {
    
    public DoubleTopBottom(Categorie categorie, String fundName, int turningPoint, float stepSize, Modelregel modelRegel) {
        super();
        this.fundName = fundName;
        this.turningPoint = turningPoint;
        this.stepSize = stepSize;
        this.modelRegel = modelRegel;
        this.categorie = categorie;
    }
    private String fundName;
    private int turningPoint;
    private float stepSize;
    private Modelregel modelRegel;
    private Categorie categorie;

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
    
    public Categorie getCategorie() {
        return categorie;
    }

    public String toString() {
        return "fund: " + fundName + "(" + turningPoint + ", " + stepSize + ") datum: " + modelRegel.getDatum() + " status: " + modelRegel.getStatus() + " kolom: " + modelRegel.getKolomnr();
    }

}
