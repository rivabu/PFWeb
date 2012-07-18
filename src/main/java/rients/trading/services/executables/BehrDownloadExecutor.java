/*
 * Copyright Alliander 2009
 */
package rients.trading.services.executables;

import java.io.IOException;

import org.rients.com.constants.Constants;
import org.rients.randomkoersgenerator.RandomKoersGenerator;

import rients.trading.services.BehrDownloadServiceImpl;
import rients.trading.services.BehrOverzichtSplitterImpl;
import rients.trading.services.FileDownloadServiceImpl;
import rients.trading.services.FileIOServiceImpl;
import rients.trading.services.FundPropertiesServiceImpl;


/**
 * Testcase for class {@link VersionController}
 */
public class BehrDownloadExecutor  {
    private transient BehrDownloadServiceImpl controller;

    BehrDownloadExecutor() {
        super();
        controller = new BehrDownloadServiceImpl();
        controller.setBehrOverviewURL(Constants.BEHR_OVERVIEW_URL);
        controller.setFileUrlPrefix(Constants.URL_PREFIX);
        controller.setRootDir(Constants.ALLKOERSENDIR);
        controller.setFavouritesDir(Constants.KOERSENDIR);
        controller.setFileDownloadService(new FileDownloadServiceImpl());
        controller.setBehrOverzichtSplitter(new BehrOverzichtSplitterImpl());
        controller.setFileIOService(new FileIOServiceImpl(Constants.ALLKOERSENDIR, Constants.TEMPDIR));
        controller.setFundPropertiesService(new FundPropertiesServiceImpl());
        /*
        <property name="behrOverviewURL" value="${behroverviewurl}" />
        <property name="fileUrlPrefix" value="${fileurlprefix}" />
        <property name="rootDir" value="${rootdir}" />
        <property name="favouritesDir" value="${favouritesdir}" />
        
         */
    }

    /**
     * @throws Exception
     */
    public void testDownloadCategory() throws Exception {
        //controller.downloadCategory(Categories.HOOFDFONDEN);
        //controller.downloadCategory(Categories.DIVERSE);
        //controller.downloadCategory(Categories.VALUTA);
        //controller.downloadCategory(Categories.LOKALEFONDSEN);
        //controller.downloadCategory(Categories.INDEXEN);
        //controller.downloadCategory(Categories.GRONDSTOFFEN);
        //controller.downloadCategory(Categories.BELEGGINGSFUNDS);

    }
    
    public void testDownloadFavourites() throws Exception {
    	
    	controller.downloadFavorites();
    }
    
    public void testGenerateRandom() throws IOException {
        RandomKoersGenerator.main(null);
    }

    /**
     * @throws Exception
     */
    public void downloadAll() throws Exception {
        // controller.downloadAll();
    }
    
    public static void main(String[] args) throws Exception {
        BehrDownloadExecutor executor = new BehrDownloadExecutor();
        executor.testDownloadFavourites();

        //executor.downloadAll();
        executor.testGenerateRandom();
        //executor.testDownloadCategory();
        
        
    }
}
