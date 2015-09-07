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

    public static void main(String[] args) {
        IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
        demo.process(true);

    }

    private String getLaatsteKoersenDag() {
        // woensdag = 3
        int weekday = dateTime.getDayOfWeek();

        int back = 0;
        if (weekday == 6) {
            back = 1;
        }
        if (weekday == 7) {
            back = 2;
        }
        dateTime = dateTime.minusDays(back);
        weekday = dateTime.getDayOfWeek();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("YYMMdd");
        String date = fmt.print(dateTime);
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

    public void process(boolean download) {
        boolean first = true;
        // Properties koersen = new Properties();
        Properties data = new Properties();
        String intradayDownloadProps = Constants.KOERSENDIR + Categories.INTRADAY + "/download.properties";
        data = PropertiesUtils.getProperties(intradayDownloadProps);
        String lastImportDate = data.getProperty("downloadDate");
        String lastImportResult = data.getProperty("downloadResult");
        data.put("downloadResult", "finished");
        String laatsteKoersenDag = getLaatsteKoersenDag();
        while (true) {
            String nowString = "";
            if (first && lastImportResult.equals("unfinished")) {
                nowString = lastImportDate;
            } else {
                nowString = getNextDay();
                if ((Integer.parseInt(nowString) > Integer.parseInt(laatsteKoersenDag) || lastImportResult.equals("unfinished"))) {
                    System.out.println("lastImportResult: " + lastImportResult + " laatsteKoersenDag: " + laatsteKoersenDag + " nowString: " + nowString);
                    break;
                }
            }

            if (first) {
                data.put("downloadDate", nowString);
            }
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
            }
            PropertiesUtils.saveProperties(intradayDownloadProps, data);

            first = false;

        }
        ;
    }

    private ReturnData handleOneFile(String fondsNaam, String nowString, String intradayFile) {
        String firstMatch = "<data>";
        String lastMatch = "</data>";
        ReturnData returnData = new ReturnData();
        returnData.result = "finished";

        System.out.println("downloading: http://www.behr.nl/charts/dagkoers/" + fondsNaam + "?dag=" + nowString);

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
                        outputLines.add(outputLine);
                    } else {
                        returnData.result = "unfinished";
                    }
                }
            }
        }
        FileUtils.writeToFile(intradayFile, outputLines);
        returnData.koers = koers;
        return returnData;
    }

    public class ReturnData {
        String koers;
        String result;
    }
}
