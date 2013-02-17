/**
 *
 */
package org.rients.com.transactionOverview;


/**
 * @author Rients
 *
 */
public class SlotKoers {
    private int datum;
    private String koers;

    public SlotKoers() {
    }

    /**
     * @param datum
     * @param koers
     */
    public SlotKoers(int datum, String koers) {
        this.datum = datum;
        this.koers = koers;
    }

    /**
     * @return the datum
     */
    public int getDatum() {
        return this.datum;
    }

    /**
     * @return the koers
     */
    public String getKoers() {
        return this.koers;
    }

    @Override
    public String toString() {
        return datum + "," + koers;
    }
}
