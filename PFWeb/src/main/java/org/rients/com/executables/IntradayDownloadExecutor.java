package org.rients.com.executables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.rients.com.constants.Constants;
import org.rients.com.model.Categories;
import org.rients.com.services.FileDownloadService;
import org.rients.com.services.FileDownloadServiceImpl;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.PropertiesUtils;
import org.rients.com.utils.TimeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class IntradayDownloadExecutor {

	FileDownloadService downloader = new FileDownloadServiceImpl();

	public static void main(String[] args) {
		IntradayDownloadExecutor demo = new IntradayDownloadExecutor();
					demo.process();
	}

	public Properties process()  {
		Properties koersen = new Properties();
		String nowString = TimeUtils.getNowString();

		String directory = Constants.KOERSENDIR + Categories.HOOFDFONDEN;
		List<String> files = FileUtils.getFiles(directory, "csv", false);
		for (String fondsNaam : files) {
		
			String intradayFile = Constants.INTRADAY_KOERSENDIR + "20" + nowString + "_" + fondsNaam + ".csv";

			if (FileUtils.fileExists(intradayFile)) {
				FileUtils.removeFile(intradayFile);
			}
			String koers = handleOneFile(fondsNaam, nowString, intradayFile);
			if (koers != null) {
				koersen.put(fondsNaam, koers);
			}
			FileUtils.removeFile(Constants.INTRADAY_TEMPFILE);
		}
		String filenaam = Constants.KOERSENDIR + Categories.INTRADAY + "/20" + nowString + ".properties";
		PropertiesUtils.saveProperties(filenaam, koersen);
		return koersen;
	}

	private String handleOneFile(String fondsNaam, String nowString, String intradayFile)  {
		String firstmatch = "<table class=\"shares \" cellpadding=\"0\" cellspacing=\"0\">";
		String lastmatch = "</table>";

		System.out.println("downloading: http://www.behr.nl/koersen/intradays/" + fondsNaam + "?dag=" + nowString);

		String content = null;
		try {
			content = downloader.downloadFile("http://www.behr.nl/koersen/intradays/" + fondsNaam + "?dag=" + nowString);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
		int indexFirst = content.indexOf(firstmatch);
		if (indexFirst == -1) {
			return null;
		}
		String table = content.substring(indexFirst, content.indexOf(lastmatch, indexFirst) + lastmatch.length());
		table = table.replaceAll("&nbsp;", "");
		FileUtils.writeToFile(Constants.INTRADAY_TEMPFILE, table);
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		Object result = null;
		TreeMap<String, String> tm = new TreeMap<String, String>();
		try {
			builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(Constants.INTRADAY_TEMPFILE);
			XPath xpath = XPathFactory.newInstance().newXPath();
			// XPath Query for showing all nodes value
			XPathExpression expr = xpath.compile("//td/text()");

			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		NodeList nodes = (NodeList) result;
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
		Set<Entry<String, String>> set = tm.entrySet();
		// Get an iterator
		Iterator<Entry<String, String>> i = set.iterator();
		// Display elements
		ArrayList<String> lines = new ArrayList<String>();
		String closeKoersNew = "";
		while (i.hasNext()) {
			Map.Entry<String, String> me = i.next();
			closeKoersNew = (String) me.getValue();
			lines.add(me.getKey() + "," + closeKoersNew);

		}
		FileUtils.writeToFile(intradayFile, lines);
		return koers;
	}
}
