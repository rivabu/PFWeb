/*
 * Copyright Alliander 2009
 */
package org.rients.com.executables;

import java.util.ArrayList;

import org.rients.com.constants.Constants;
import org.rients.com.model.Categorie;
import org.rients.com.model.FondsURL;
import org.rients.com.services.DoubleTopAndBottomsLocator;



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
