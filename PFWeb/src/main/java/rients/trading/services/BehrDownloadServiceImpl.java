/**
 *
 */
package rients.trading.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.rients.com.constants.Constants;

import rients.trading.download.exception.ValidationException;
import rients.trading.download.model.Categorie;
import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.FondsURL;
import rients.trading.utils.FileUtils;
import rients.trading.utils.PropertiesUtils;
import rients.trading.utils.StackTraceUtils;


/**
 * @author Rients
 *
 */
public class BehrDownloadServiceImpl implements BehrDownloadService {
    private static final Logger LOGGER = Logger.getLogger(BehrDownloadServiceImpl.class);
    private static String sep = "/";
    private FileDownloadService fileDownloadService;
    private BehrOverzichtSplitter behrOverzichtSplitter;
    private FileIOService fileIOService;
    private FundPropertiesService fundPropertiesService;
    private String behrOverviewURL;
    private String fileUrlPrefix;
    private String rootDir;
    private String favouritesDir;

    

    /*
     * (non-Javadoc)
     *
     * @see rients.trading.download.services.BehrDownloadService#downloadCategory(java.lang.String)
     */
    public void downloadCategory(final String categoryNaam) {
        try {
            String behrOverviewFileContent = fileDownloadService.downloadFile(behrOverviewURL, categoryNaam);
            Categorie categorie = behrOverzichtSplitter.extractURLs(behrOverviewFileContent, categoryNaam);

            if (categorie.getNaam().equals(categoryNaam)) {
                List<FondsURL> fondsURLs = categorie.getItems();
                Iterator<FondsURL> fondsen = fondsURLs.iterator();

                while (fondsen.hasNext()) {
                    FondsURL fondsURL = fondsen.next();
                    LOGGER.debug(categorie.getNaam() + sep + fondsURL.getURL());

                    handleOneFile(getRootDir(), categorie, fondsURL);
                }

                return;
            }
        } catch (IOException e) {
            LOGGER.error(StackTraceUtils.getStackTrace(e));
        }
    }

    
    /*
     *  // list favorite directory
     * 
     */
    public void downloadFavorites() {
    	String dir = getFavouritesDir();
    	List<String> subdirs = FileUtils.getSubdirs(dir);
    	for (String subdir : subdirs) {
    	    if (subdir.equals("Random")) {
    	        continue;
    	    }
    		List<String> files = FileUtils.getFiles(getFavouritesDir() + sep + subdir, "csv", false);
    		Categorie categorie = new Categorie();
    		categorie.setNaam(subdir);
    		for (String file : files) {
    			
    			FondsURL fondsURL = new FondsURL();
    			fondsURL.setNaam(file);
    			fondsURL.setURL(file);
    			System.out.println(categorie.getNaam() + sep + fondsURL.getURL());
    			handleOneFile(getFavouritesDir(), categorie, fondsURL);
    		}
    	}
    }
   
    private void handleOneFile(final String theRootDir, final Categorie categorie, final FondsURL fondsURL) {
        try {
            String fileContent = fileDownloadService.downloadFile(getFileDownloadURL(fondsURL.getURL()));

            if (fileContent.length() < 100) {
                LOGGER.error(categorie.getNaam() + sep + fondsURL.getURL() + " filecontent < 100");

                return;
            }

            fileIOService.addFileToTempDirectory(fondsURL.getURL(), fileContent);

            List<String> lines = fileIOService.readFromFileInTemp(fondsURL.getURL());
            List<Dagkoers> slotKoersenNew = behrOverzichtSplitter.extractSlotkoersen(categorie.getNaam() + sep +
                    fondsURL.getURL(), lines);
            String filename = changeExtension(fondsURL.getURL(), Constants.CSV);
            List<Dagkoers> slotKoersenOld = fileIOService.readFromSlotKoersenFile(theRootDir, categorie.getNaam(), filename);
            slotKoersenNew = mergeOldWithNew(slotKoersenOld, slotKoersenNew);

            String dir = theRootDir + sep + categorie.getNaam();
            fileIOService.saveToFile(dir, filename, slotKoersenNew);
            fileIOService.removeFileFromTempDirectory(fondsURL.getURL());
            String fundName = filename.substring(0, filename.lastIndexOf('.'));
            if (theRootDir.equals(getFavouritesDir())) {
                Map<String, String> fundProperties = fundPropertiesService.extractFundProperties(fundName, slotKoersenNew);
                filename = changeExtension(filename, Constants.PROPERTIES);
                PropertiesUtils.saveProperties(Constants.FUND_PROPERTIESDIR + filename, fundProperties);
                
            }
        } catch (IOException ioe) {
            LOGGER.error(StackTraceUtils.getStackTrace(ioe));
        } catch (ValidationException e) {
            LOGGER.error(StackTraceUtils.getStackTrace(e));
        }
    }

    /**
     * @param oldData
     * @param newData
     * @return
     */
    public List<Dagkoers> mergeOldWithNew(final List<Dagkoers> oldData, final List<Dagkoers> newData) {
        List<Dagkoers> returnData = null;
        int countNew = newData.size();

        if (oldData.isEmpty()) {
        	returnData = newData;
        } else {
            returnData = oldData;
            int countOld = oldData.size();

            Dagkoers lastOldDagKoers = oldData.get(countOld - 1);

            // hij telt terug!!!
            for (int counter = countNew - 1; counter >= 0; counter--) {
                Dagkoers slotkoers = newData.get(counter);
                int datum = Integer.parseInt(slotkoers.getDatum());

                if (datum == Integer.parseInt(lastOldDagKoers.getDatum())) {
                    break;
                }

                returnData.add(countOld, slotkoers);
            }
        }

        return returnData;
    }

    private String getFileDownloadURL(final String filename) {
        return getFileUrlPrefix() + filename.substring(0, 1) + sep + filename;
    }

    private String changeExtension(final String filename, final String newExtension) {
        String newfilename = filename  + newExtension;
        if (filename.endsWith(".csv")) {
            newfilename = filename.substring(0, filename.lastIndexOf('.')) + newExtension;
        }
        return newfilename;
    }

    /**
     * @return the fileDownloadService
     */
    public FileDownloadService getFileDownloadService() {
        return this.fileDownloadService;
    }

    /**
     * @param fileDownloadService
     *            the fileDownloadService to set
     */
    public void setFileDownloadService(final FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    /**
     * @return the behrOverviewURL
     */
    public String getBehrOverviewURL() {
        return this.behrOverviewURL;
    }

    /**
     * @param behrOverviewURL
     *            the behrOverviewURL to set
     */
    public void setBehrOverviewURL(final String behrOverviewURL) {
        this.behrOverviewURL = behrOverviewURL;
    }

    /**
     * @return the behrOverzichtSplitter
     */
    public BehrOverzichtSplitter getBehrOverzichtSplitter() {
        return this.behrOverzichtSplitter;
    }

    /**
     * @param behrOverzichtSplitter
     *            the behrOverzichtSplitter to set
     */
    public void setBehrOverzichtSplitter(final BehrOverzichtSplitter behrOverzichtSplitter) {
        this.behrOverzichtSplitter = behrOverzichtSplitter;
    }

    /**
     * @return the fileIOService
     */
    public FileIOService getFileIOService() {
        return this.fileIOService;
    }

    /**
     * @param fileIOService
     *            the fileIOService to set
     */
    public void setFileIOService(final FileIOService fileIOService) {
        this.fileIOService = fileIOService;
    }

    /**
     * @return the fileUrlPrefix
     */
    public String getFileUrlPrefix() {
        return this.fileUrlPrefix;
    }

    /**
     * @param fileUrlPrefix
     *            the fileUrlPrefix to set
     */
    public void setFileUrlPrefix(final String fileUrlPrefix) {
        this.fileUrlPrefix = fileUrlPrefix;
    }

    /**
     * @return the rootDir
     */
    public String getRootDir() {
        return this.rootDir;
    }

    /**
     * @param rootDir the rootDir to set
     */
    public void setRootDir(final String rootDir) {
        this.rootDir = rootDir;
    }

	public String getFavouritesDir() {
		return favouritesDir;
	}

	public void setFavouritesDir(String favouritesDir) {
		this.favouritesDir = favouritesDir;
	}

	public FundPropertiesService getFundPropertiesService() {
		return fundPropertiesService;
	}

	public void setFundPropertiesService(FundPropertiesService fundPropertiesService) {
		this.fundPropertiesService = fundPropertiesService;
	}
}
