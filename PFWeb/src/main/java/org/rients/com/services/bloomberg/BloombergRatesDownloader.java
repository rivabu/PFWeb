package org.rients.com.services.bloomberg;

import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.rients.com.constants.Constants;
import org.rients.com.services.FileDownloadService;
import org.rients.com.services.FileDownloadServiceImpl;
import org.rients.com.utils.DateParser;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.PropertiesUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class BloombergRatesDownloader {

    private FileDownloadService fileDownloadService = new FileDownloadServiceImpl();
    
    public void downloadNow() {
        try {
            
            downloadContent();
            
            Properties props = PropertiesUtils.getPropertiesFromClasspath("commodities.properties");
            
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(Constants.BLOOMBERG_COMMODITIES_TEMPFILE);
            XPath xpath = XPathFactory.newInstance().newXPath();
            // XPath Query for showing all nodes value
            XPathExpression fondsen = xpath.compile("/div/table/tbody/tr/td[@class='name']/text()");
            XPathExpression koersen = xpath.compile("/div/table/tbody/tr/td[@class='value']/text()");

            Object fondsenList = fondsen.evaluate(doc, XPathConstants.NODESET);
            NodeList fondsenNodes = (NodeList) fondsenList;
            Object koersenList = koersen.evaluate(doc, XPathConstants.NODESET);
            NodeList koersenNodes = (NodeList) koersenList;
            String line = "";
            boolean first = true;
            for (int i = 0; i < fondsenNodes.getLength(); i++) {
                String fondsnaam = fondsenNodes.item(i).getNodeValue();

                if (PropertiesUtils.hasKey(props, fondsnaam)) {
                    String fondsnaamNew = PropertiesUtils.getValue(props, fondsnaam);
                    String koers = koersenNodes.item(i).getNodeValue();
                    if (!first) {
                        line = line + "|";
                    } else {
                        first = false;
                    }
                    line  = line + fondsnaamNew + ":" + koers;
                }
            }
            String time = DateParser.printTime(Calendar.getInstance());
            System.out.println(time + " " + line);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void downloadContent() throws Exception {
        String content = fileDownloadService.downloadFile(Constants.BLOOMBERG_COMMODITIES_URL);
     //   System.out.println(content);
        String firstmatch = "<table class=\'std_table_module dual_border_data_table\'>"; 
        String lastmatch = "</table>";
        String table = "<div>";
        int indexFirst = 0;
        boolean firstFound = false;
        while (true) {
           
            indexFirst = content.indexOf(firstmatch, indexFirst+ 1);
            if (indexFirst == -1) {
                break;
            }
            if (!firstFound) {
                firstFound = true;
            } else {
                table = table + content.substring(indexFirst, content.indexOf(lastmatch, indexFirst) + lastmatch.length());
            }
        }
        table = table + "</div>";
        table = table.replaceAll("&nbsp;", "");
        FileUtils.writeToFile(Constants.BLOOMBERG_COMMODITIES_TEMPFILE, table);
    }
}
