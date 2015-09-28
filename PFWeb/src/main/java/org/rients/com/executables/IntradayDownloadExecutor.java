package org.rients.com.executables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.rients.com.constants.Constants;
import org.rients.com.model.Categories;
import org.rients.com.services.FileDownloadService;
import org.rients.com.services.FileDownloadServiceImpl;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.PropertiesUtils;

public class IntradayDownloadExecutor {

    FileDownloadService downloader = new FileDownloadServiceImpl();
    private DateTime dateTime = new DateTime();

    public static void main(String[] args) throws Exception {
        IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
        demo.process(true);
        WinnersAndLosersExecutor winnersAndLosers = new WinnersAndLosersExecutor();
        winnersAndLosers.process();

    }

    private String getLaatsteKoersenDag(int minus) {
        // woensdag = 3
        int weekday = dateTime.getDayOfWeek();

        int back = minus;
        if (weekday == 6) {
            back = back + 1;
        }
        if (weekday == 7) {
            back = back + 2;
        }
        dateTime = dateTime.minusDays(back);
        weekday = dateTime.getDayOfWeek();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYMMdd");
        String date = fmt.print(dateTime);
        return date;
    }
    
    private String getToday() {
        // woensdag = 3
        DateTime now = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYMMdd");
        String date = fmt.print(now);
        return date;
    }

    private String getNextDay() {
        // woensdag = 3
        // zondag = 7
        int weekday = dateTime.getDayOfWeek();

        int plus = 1;
        if (weekday == 6) {
            plus = 2;
        }
        if (weekday == 5) {
            plus = 3;
        }
        dateTime = dateTime.plusDays(plus);
        weekday = dateTime.getDayOfWeek();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYMMdd");
        String date = fmt.print(dateTime);
        return date;
    }

    // http://www.behr.nl/charts/dagkoers/sbm.offshor?dag=150828
    // http://www.behr.nl/koersen/intradays/vopak?dag=150828
    /*
     * start met de datum uit de download properties, als deze op unfinised
     * staat als deze op finished staat
     */
    
    private boolean nuLaterDan(int tijd) {
        DateTime nu = new DateTime();
        boolean returnValue = false;
        if (nu.getHourOfDay() > tijd) {
            returnValue = true;
        }
        return returnValue;
    }

    public void process(boolean download) throws Exception {
        String today = getToday();
        boolean first = true;
        Properties data = new Properties();
        String intradayDownloadProps = Constants.KOERSENDIR + Categories.INTRADAY + "/download.properties";
        data = PropertiesUtils.getProperties(intradayDownloadProps);
        String lastImportDate = data.getProperty("downloadDate");
        String lastImportResult = data.getProperty("downloadResult");
        String slotKoersenDownloaded = data.getProperty("slotkoersenDownloaded");
        
        String laatsteKoersenDag = getLaatsteKoersenDag(0);
        //String voorLaatsteKoersenDag = getLaatsteKoersenDag(1);
        // laatste nog niet gedownload
        if (Integer.parseInt(slotKoersenDownloaded) < Integer.parseInt(laatsteKoersenDag) || nuLaterDan(19)) {
            data.put("slotkoersenDownloaded", laatsteKoersenDag);
            BehrDownloadExecutor executor = new BehrDownloadExecutor();
            executor.testDownloadFavourites();
            PropertiesUtils.saveProperties(intradayDownloadProps, data);
        }

        
        int year = Integer.parseInt(lastImportDate.substring(0, 2)) + 2000;
        int month = Integer.parseInt(lastImportDate.substring(2, 4));
        int day = Integer.parseInt(lastImportDate.substring(4));
        
        dateTime = new DateTime(year, month, day, 12, 0, 0, 0);

        data.put("downloadResult", "finished");
        Properties koersen = new Properties();
        while (true) {
            String nowString = "";
            if (first && lastImportResult.equals("unfinished")) {
                nowString = lastImportDate;
            } else {
                nowString = getNextDay();
                // uitstappen als nowString in de toekomst ligt, of status unfinished is
                if ((Integer.parseInt(nowString) > Integer.parseInt(today) || lastImportResult.equals("unfinished"))) {
                    System.out.println("lastImportResult: " + lastImportResult + " laatsteKoersenDag: " + laatsteKoersenDag + " nowString: " + nowString);
                    break;
                }
            }
            String filenaam = Constants.KOERSENDIR + Categories.INTRADAY + "/_properties/20" + nowString + ".properties";

            data.put("downloadDate", nowString);
            String FondsenDirectory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
            List<String> files = FileUtils.getFiles(FondsenDirectory, "csv", false);
            for (String fondsNaam : files) {
                String directory = Constants.INTRADAY_KOERSENDIR + Constants.SEP + fondsNaam;
                String intradayFile = directory + Constants.SEP + "20" + nowString + ".csv";
                FileUtils.createIfNonExisting(directory);
                if (FileUtils.fileExists(intradayFile)) {
                    FileUtils.removeFile(intradayFile);
                }

                ReturnData returnData = handleOneFile(fondsNaam, nowString, intradayFile);
                if (first) {
                    data.put("downloadResult", returnData.result);
                    lastImportResult = returnData.result;
                }
                if (returnData.koers != null) {
                    koersen.put(fondsNaam, returnData.koers);
                }
            }
            PropertiesUtils.saveProperties(intradayDownloadProps, data);

            first = false;

            PropertiesUtils.saveProperties(filenaam, koersen);
        }
    }

    private ReturnData handleOneFile(String fondsNaam, String nowString, String intradayFile) {
        String firstMatch = "<data>";
        String lastMatch = "</data>";
        ReturnData returnData = new ReturnData();
        returnData.result = "finished";


        String content = null;
        try {
            content = downloader.downloadFile("http://www.behr.nl/charts/dagkoers/" + fondsNaam + "?dag=" + nowString);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        int indexFirst = content.indexOf(firstMatch);
        if (indexFirst == -1) {
            return null;
        }
        String data = content.substring(indexFirst + firstMatch.length(), content.indexOf(lastMatch, indexFirst));
        String[] lines = data.split("\n");
        ArrayList<String> outputLines = new ArrayList<String>();
        boolean topFound = false;
        String koers = "";
        String lastKoers = "";
        for (String line : lines) {
            if (line.startsWith("09")) {
                topFound = true;
            }
            if (topFound) {
                String result[] = line.split(",");
                if (result.length == 3) {
                    String time = result[0];
                    koers = result[1].trim();
                    if (!StringUtils.isBlank(koers)) {
                        String outputLine = time + "," + koers;
                        lastKoers = koers;
                        outputLines.add(outputLine);
                        returnData.koers = koers;
                    } else {
                        returnData.result = "unfinished";
                    }
                }
            }
        }
        System.out.println(StringUtils.rightPad(fondsNaam, 20)  + " : " + lastKoers);
        FileUtils.writeToFile(intradayFile, outputLines);
        return returnData;
    }

    public class ReturnData {
        String koers;
        String result;
    }
}
