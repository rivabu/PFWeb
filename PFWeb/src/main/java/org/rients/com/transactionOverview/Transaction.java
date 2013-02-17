/**
 *
 */
package org.rients.com.transactionOverview;

import org.rients.com.pfweb.utils.MathFunctions;


/**
 * @author Rients
 *
 */
public class Transaction {
    private String fundName;
    private int number;
    private SlotKoers koopKoers;
    private SlotKoers verkoopKoers;
    private Type type;

    /**
     * @param koopKoers
     * @param verkoopKoers
     */
    private Transaction(SlotKoers koopKoers, SlotKoers verkoopKoers) {
        super();
        this.koopKoers = koopKoers;
        this.verkoopKoers = verkoopKoers;
    }

    public static Transaction createInstance(final SlotKoers koopKoers, final SlotKoers verkoopKoers) {
    	return new Transaction(koopKoers, verkoopKoers);
    }
    /**
     * @return the koopKoers
     */
    public SlotKoers getKoopKoers() {
        return this.koopKoers;
    }

    /**
     * @return the verkoopKoers
     */
    public SlotKoers getVerkoopKoers() {
        return this.verkoopKoers;
    }

    public double getProcVerschil() {
        double koop = new Double(koopKoers.getKoers()).doubleValue();
        double verkoop = new Double(verkoopKoers.getKoers()).doubleValue();
        return ((verkoop - koop) / koop) * 100;
    }

    public String getScore() {
        return MathFunctions.round(getProcVerschil() + "");
    }

    @Override
    public String toString() {

        return fundName + " " + number + " " + koopKoers.getDatum() + " " + koopKoers.getKoers() + " " + verkoopKoers.getDatum() +
        " " + verkoopKoers.getKoers() + " " + type.name();
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
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

}
