package org.rients.com.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.joda.time.DateTime;
import org.rients.com.constants.Constants;
import org.rients.com.model.Categorie;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.model.FondsURL;
import org.rients.com.model.Modelregel;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.pfweb.services.HandlePF;
import org.rients.com.pfweb.services.modelfunctions.ModelFunctions;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.PropertiesUtils;


public class DoubleTopAndBottomsLocator {
    private static String sep = "\\";
    private String favouritesDir;

    
    public String getFavouritesDir() {
        return favouritesDir;
    }

    public void setFavouritesDir(String favouritesDir) {
        this.favouritesDir = favouritesDir;
    }
    
    public ArrayList<Categorie> locate(final String type, String graphType) {
        String dir = getFavouritesDir();
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
                prop = PropertiesUtils.getPropertiesFromClasspath(file + Constants.PROPERTIES);

                String graphParametersString = prop.getProperty("graphParameters");
                
                String[][] graphParameters = convertGraphParameters(graphParametersString);
                
                if (type.equals("top_bottoms")) {
                    for (int i=0; i< 5; i++) {
                        boolean doubleTopBottomFound = handleOneDoubleTopsAndBottoms(fullSubDir, file, graphType, Integer.parseInt(graphParameters[i][0]), Float.parseFloat(graphParameters[i][1]));
                        if (doubleTopBottomFound) {
                            categorie.getItems().add(fondsURL);
                            break;
                        }
                    }
                }
                if (type.equals("tops") || type.equals("bottoms")) {
                    boolean topOrBottomFound = handleOneTopsAndBottoms(fullSubDir, file, graphType, Integer.parseInt(graphParameters[0][0]), Float.parseFloat(graphParameters[0][1]), type);
                    if (topOrBottomFound) {
                        categorie.getItems().add(fondsURL);
                    }
                }
            }
            matchedCategoriesList.add(categorie);
        }
        return matchedCategoriesList;
    }

    private boolean handleOneDoubleTopsAndBottoms(String dir, String fundName, String graphType, int turningPoint, float stepSize) {
        
        boolean topBottomFound = false;
        HandleFundData fundData = new HandleFundData();
        HandlePF handlePF = new HandlePF();

        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, dir);

        ArrayList<Modelregel> PFData = handlePF.createPFData(rates, fundName, graphType, dir, turningPoint, stepSize);
        
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

    private boolean handleOneTopsAndBottoms(String dir, String fundName, String graphType, int turningPoint, float stepSize, String type) {
        
        HandleFundData fundData = new HandleFundData();
        HandlePF handlePF = new HandlePF();

        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, dir);

        ArrayList<Modelregel> PFData = handlePF.createPFData(rates, fundName, graphType, dir, turningPoint, stepSize);
        
        ModelFunctions mf = new ModelFunctions(fundName);
        mf.setPFData(PFData);
        mf.handleFindTopsAndBottoms(turningPoint, stepSize);
        int size = PFData.size() - 1;
        Modelregel modelRegel = PFData.get(PFData.size() - 1);
        boolean match = false;
        int lastColumn = modelRegel.getKolomnr();
        if ((modelRegel.getStatus() == DagkoersStatus.TOP &&  type.equals("tops")) || 
                (modelRegel.getStatus() == DagkoersStatus.BOTTOM && type.equals("bottoms"))) {
            match = true;
        }
        if (!match) {
            for (int i = size; i > 0; i--) {
                modelRegel = PFData.get(i);
                if ((modelRegel.getKolomnr() == (lastColumn - 1)) && 
                        (modelRegel.getStatus() == DagkoersStatus.TOP &&  type.equals("tops")) || 
                        (modelRegel.getStatus() == DagkoersStatus.BOTTOM && type.equals("bottoms"))) {
                    match = true;
                    break;
                }
                if (modelRegel.getKolomnr() < (lastColumn - 1)) {
                    break;
                }
                
            }
        }
        return match;
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
