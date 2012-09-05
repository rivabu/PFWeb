package rients.trading.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.rients.com.constants.Constants;

import rients.trading.download.model.Categorie;
import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.DagkoersStatus;
import rients.trading.download.model.FondsURL;
import rients.trading.download.model.Modelregel;
import rients.trading.services.modelfunctions.ModelFunctions;
import rients.trading.utils.FileUtils;
import rients.trading.utils.PropertiesUtils;

public class DoubleTopAndBottomsLocator {
    private static String sep = "\\";
    private String favouritesDir;

    
    public String getFavouritesDir() {
        return favouritesDir;
    }

    public void setFavouritesDir(String favouritesDir) {
        this.favouritesDir = favouritesDir;
    }
    
    public ArrayList<Categorie> locate() {
        String dir = getFavouritesDir();
        @SuppressWarnings("unchecked")
        List<String> subdirs = FileUtils.getSubdirs(dir);
        ArrayList<Categorie> matchedCategoriesList = new ArrayList<Categorie>();

        for (String subdir : subdirs) {
            if (subdir.equals("Random") || subdir.equals("Beleggingsfunds")) {
                continue;
            }
            String fullSubDir = getFavouritesDir() + subdir + sep;
            List<String> files = FileUtils.getFiles(fullSubDir, "csv", false);
            Categorie categorie = new Categorie();
            categorie.setNaam(subdir);
            for (String file : files) {
                
                FondsURL fondsURL = new FondsURL();
                fondsURL.setNaam(file);
                fondsURL.setURL(file);
                System.out.println(categorie.getNaam() + sep + fondsURL.getURL());
                
                Properties prop = null;
                String fileName = Constants.FUND_PROPERTIESDIR + file + Constants.PROPERTIES;
                prop = PropertiesUtils.getProperties(fileName);

                String graphParametersString = prop.getProperty("graphParameters");
                
                String[][] graphParameters = convertGraphParameters(graphParametersString);
                
                for (int i=0; i< 5; i++) {
                    boolean topBottomFound = handleOne(fullSubDir, file, Integer.parseInt(graphParameters[i][0]), Float.parseFloat(graphParameters[i][1]));
                    if (topBottomFound) {
                        categorie.getItems().add(fondsURL);
                        break;
                    }
                }
            }
            matchedCategoriesList.add(categorie);
        }
        return matchedCategoriesList;
    }

    private boolean handleOne(String dir, String fundName, int turningPoint, float stepSize) {
        
        boolean topBottomFound = false;
        HandleFundData fundData = new HandleFundData();
        HandlePF handlePF = new HandlePF();

        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, dir);

        ArrayList<Modelregel> PFData = handlePF.createPFData(rates, fundName, dir, turningPoint, stepSize);
        
        ModelFunctions mf = new ModelFunctions(fundName);
        mf.setPFData(PFData);
        mf.handlePFRules(turningPoint, stepSize);
        int lastColumnTopMatch = -10;
        int lastColumnBottomMatch = -10;
        int lastRowTopMatch = -10;
        int lastRowBottomMatch = -10;
        int oneWeekAgo = getOneWeekAgo();
        for (Modelregel modelRegel : PFData) {
            if (modelRegel.getStatus() == DagkoersStatus.DOUBLE_TOP) {
                if (lastColumnTopMatch + 2 == modelRegel.getKolomnr() && lastRowTopMatch == modelRegel.getRijnr()) {
                    if (Integer.parseInt(modelRegel.getDatum()) > oneWeekAgo) {
                        topBottomFound = true;
                        break;
                    }
                }
                lastColumnTopMatch = modelRegel.getKolomnr();
                lastRowTopMatch = modelRegel.getRijnr();
            }
            if (modelRegel.getStatus() == DagkoersStatus.DOUBLE_BOTTOM) {
                if (lastColumnBottomMatch + 2 == modelRegel.getKolomnr() && lastRowBottomMatch == modelRegel.getRijnr()) {
                    
                    if (Integer.parseInt(modelRegel.getDatum()) > oneWeekAgo) {
                        topBottomFound = true;
                        break;
                    }
                }
                lastColumnBottomMatch = modelRegel.getKolomnr();
                lastRowBottomMatch = modelRegel.getRijnr();
            }
        }
        return topBottomFound;
    }

    private int getOneWeekAgo() {
        DateTime now = new DateTime();
        now = now.minusWeeks(1); // 1 week terug
        String nowString = now.toString("yyyyMMdd");
        return Integer.parseInt(nowString);
    }

    private String[][] convertGraphParameters(String line) {
        String[][]  graphParameters = { { "1", "0.75" }, { "2", "1" }, { "1", "1" }, { "1", "1.5" }, { "1", "2" } };
        if (line != null) {
            graphParameters = new String[5][2];
            StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), ":");
            if (stringtokenizer.countTokens() == 5) {
                for (int i = 0; i < 5; i++) {
                    String token = stringtokenizer.nextToken().trim();
                    int indexDubbelePunt = token.indexOf(",");
                    String turningPoint = token.substring(0, indexDubbelePunt);
                    String stepSize = token.substring(indexDubbelePunt + 1);
                    graphParameters[i][0] = turningPoint;
                    graphParameters[i][1] = stepSize;
                }
            }
            
        }
        return graphParameters;
    }

}
