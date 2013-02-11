/*
 * Copyright Alliander 2009
 */
package rients.trading.services.executables;

import java.util.ArrayList;

import org.rients.com.constants.Constants;

import rients.trading.download.model.Categorie;
import rients.trading.download.model.FondsURL;
import rients.trading.services.DoubleTopAndBottomsLocator;


/**
 * Testcase for class {@link VersionController}
 */
public class TopBottomLocatorExecutor  {
    private transient DoubleTopAndBottomsLocator controller;

    TopBottomLocatorExecutor() {
        super();
        controller = new DoubleTopAndBottomsLocator();
        controller.setFavouritesDir(Constants.KOERSENDIR);
    }

    
    public void testDownloadFavourites() throws Exception {
    	
        ArrayList<Categorie> matchedCategoriesList = controller.locate("top_bottoms");
    	
        for (Categorie cat : matchedCategoriesList) {
            System.out.println(cat.getNaam() + ": ");
            for (FondsURL fund : cat.getItems()) {
                System.out.println(" " + fund.getNaam());
            }
        }

        
    }
    

    
    public static void main(String[] args) throws Exception {
        TopBottomLocatorExecutor executor = new TopBottomLocatorExecutor();
        executor.testDownloadFavourites();
    }
}
