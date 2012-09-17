/**
 *
 */
package rients.trading.download.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Rients
 *
 */
public class Categorie {
    private String naam;
    private List<FondsURL> items = new ArrayList<FondsURL>();

    /**
     * @return the naam
     */
    public String getNaam() {
        return this.naam;
    }

    /**
     * @param naam
     *            the naam to set
     */
    public void setNaam(String naam) {
        this.naam = naam;
    }

    /**
     * @return the items
     */
    public List<FondsURL> getItems() {
        return this.items;
    }

    /**
     * @param items
     *            the items to set
     */
    public void setItems(List<FondsURL> items) {
        this.items = items;
    }
}
