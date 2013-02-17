package org.rients.com.executables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.rients.com.constants.Constants;
import org.rients.com.pfweb.utils.FileUtils;
import org.rients.com.pfweb.utils.PropertiesUtils;
import org.rients.com.services.FileDownloadService;
import org.rients.com.services.FileDownloadServiceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class IntradayDownloadExecutor {

    String fondsNaam = "aex-index";
    FileDownloadService downloader = new FileDownloadServiceImpl();
    
    

    public static void main(String[] args)  {
        IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
        try {
            demo.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void process() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DateTime now = new DateTime();
        int counter = 0;
        Properties props = PropertiesUtils.getProperties(Constants.INTRADAY_PROPERTIES);
        String lastDownloaded = props.getProperty("lastdownloaded");
        int year = Integer.parseInt("20"+ lastDownloaded.substring(0,2));
        int month = Integer.parseInt(lastDownloaded.substring(3,4));
        int day = Integer.parseInt(lastDownloaded.substring(4));
        
        String closeKoers = props.getProperty("closekoers");
        now = new DateTime().withDate(year, month, day).withHourOfDay(20).withMinuteOfHour(0);
        while (true) {
            now = now.plusDays(1);
            
            if (now.isAfterNow()) {
                break;
            }
            int dayofweek = now.getDayOfWeek();
            
            if (dayofweek == 7 || dayofweek == 6) {
                continue;
            }
            String nowString = now.toString("yyMMdd");
            
            String intradayFile = Constants.INTRADAY_KOERSENDIR + "20"+ nowString + ".csv";
            
            if (FileUtils.fileExists(intradayFile)) {
                break;
            }
            String firstmatch = "<table class=\"shares \" cellpadding=\"0\" cellspacing=\"0\">";
            String lastmatch = "</table>";

            System.out.println("downloading: http://www.behr.nl/koersen/intradays/" + fondsNaam + "?dag=" + nowString);

            String content = downloader.downloadFile("http://www.behr.nl/koersen/intradays/" + fondsNaam + "?dag=" + nowString);
            int indexFirst = content.indexOf(firstmatch);
            if (indexFirst == -1) {
                continue;
            }
            String table = content.substring(indexFirst, content.indexOf(lastmatch, indexFirst) + lastmatch.length());
            table = table.replaceAll("&nbsp;", "");
            FileUtils.writeToFile(Constants.INTRADAY_TEMPFILE, table);
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(Constants.INTRADAY_TEMPFILE);
            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression expr = xpath.compile("//td/text()");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            TreeMap tm = new TreeMap();
            String time = "";
            String koers = "";
            for (int i = 0; i < nodes.getLength(); i++) {
                String value = nodes.item(i).getNodeValue();
                if (value.indexOf(":") > 0) {
                    time = value;
                    koers = "";
                } else {
                    koers = value;
                    tm.put(time, koers);
                }
            }
            Set set = tm.entrySet();
            // Get an iterator
            Iterator i = set.iterator();
            // Display elements
            ArrayList<String> lines = new ArrayList<String>();
            String closeKoersNew = "";
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                closeKoersNew = (String) me.getValue();
                lines.add(me.getKey() + "," + closeKoersNew);

            }
            if (!closeKoers.equals(closeKoersNew)) {
                FileUtils.writeToFile(intradayFile, lines);
                props.setProperty("lastdownloaded", nowString);
                props.setProperty("closekoers", closeKoersNew);
                PropertiesUtils.saveProperties(Constants.INTRADAY_PROPERTIES, props);
                closeKoers = closeKoersNew;
            } else {
                break;
            }
            counter++;
        }
        FileUtils.removeFile(Constants.INTRADAY_TEMPFILE);
    }
}
