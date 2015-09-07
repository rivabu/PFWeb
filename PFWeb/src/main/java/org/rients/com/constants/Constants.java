package org.rients.com.constants;

import java.util.Properties;

import org.rients.com.utils.PropertiesUtils;


public class Constants {
    
    private static Properties props = null;
    
    private static Properties props() {
        if (props == null) {
            props =  PropertiesUtils.getPropertiesFromClasspath("application.properties");   
        }
        return props;
    }

    public static int NUMBEROFDAYSTOPRINT = Integer.parseInt(props().getProperty("lookbackperiod")); // -1 = all
    public static int NUMBEROFDAYSTOPRINT_PF = Integer.parseInt(props().getProperty("lookbackperiod_pf")); // -1 = all

    public static String CSV = ".csv";
	public static String PNG = ".png";
	public static String PROPERTIES = ".properties";
	public static String SEP = "\\";
	public static int NOTHING = 0;

    public static String ALLKOERSENDIR = props().getProperty("ALLKOERSENDIR");;
	public static String KOERSENDIR = props().getProperty("KOERSENDIR");;
	public static String IMAGESDIR = props().getProperty("IMAGESDIR");
	public static String TRANSACTIONDIR = props().getProperty("TRANSACTIONDIR");
    public static String FAVORITESDIR = props().getProperty("FAVORITESDIR");
	
    public static String REAL_TRANSACTIONDIR = props().getProperty("REAL_TRANSACTIONDIR");;
    public static String INTRADAY_KOERSENDIR = props().getProperty("INTRADAY_KOERSENDIR");
    public static String TEMPDIR = props().getProperty("TEMPDIR");
    
    public static String FUND_PROPERTIESDIR = props().getProperty("FUND_PROPERTIESDIR");
    

	//behr
    public static String URL_PREFIX = props().getProperty("URL_PREFIX");;
    public static String BEHR_OVERVIEW_URL = props().getProperty("BEHR_OVERVIEW_URL");;
    
    //bloomberg
    public static String BLOOMBERG_COMMODITIES_URL = props().getProperty("BLOOMBERG_COMMODITIES_URL");;
    public static String BLOOMBERG_COMMODITIES_TEMPFILE = props().getProperty("BLOOMBERG_COMMODITIES_TEMPFILE");;
        
    //random dir
    public static String WRITEDIR = props().getProperty("WRITEDIR");;
    public static String READDIR = props().getProperty("READDIR");;
    
    // intraday
    public static String INTRADAY_TEMPFILE = props().getProperty("INTRADAY_TEMPFILE");;
    public static String INTRADAY_PROPERTIES =  props().getProperty("INTRADAY_PROPERTIES");

    
	public static String LASTDATE = "LASTDATE";
	public static String PROC_VERSCHIL = "PROC_VERSCHIL";
    public static String LASTRATE = "LASTRATE";
    public static String FUNDNAME = "FUNDNAME";
    public static String INDEXDIR = "Indexen";
    public static String HOOFDFONDSEN = "Hoofdfondsen";
    public static String AEX_INDEX = "aex-index";
    public static String RANDOMDIR = "Random";
    public static String OPTIMALTRANSDIR = "optimalTransactions";
    public static String LAST_TEN_DAYS = "lastTenDays";
    public static String ISCURRENCY = "isCurrency";
    
    public static String TRANSACTIONS_EXCEL = props().getProperty("TRANSACTIONS_EXCEL");
    public static String ALL_TRANSACTIONS = props().getProperty("ALL_TRANSACTIONS");
    
    public static String TURBO_MAPPINGS = "turbo-mapping.properties";
    
    public static String PDF_GENERATION_URL_1 = props().getProperty("PDF_GENERATION_URL_1");
    public static String PDF_GENERATION_URL_2 = props().getProperty("PDF_GENERATION_URL_2");
    public static String PDF_GENERATION_OUTPUTFILE = props().getProperty("PDF_GENERATION_OUTPUTFILE");

	
}
