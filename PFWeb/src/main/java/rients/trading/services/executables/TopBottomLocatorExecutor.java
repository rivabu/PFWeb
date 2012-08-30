/*
 * Copyright Alliander 2009
 */
package rients.trading.services.executables;

import org.rients.com.constants.Constants;

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
    	
    	controller.locate();
    }
    

    
    public static void main(String[] args) throws Exception {
        TopBottomLocatorExecutor executor = new TopBottomLocatorExecutor();
        executor.testDownloadFavourites();
    }
}
