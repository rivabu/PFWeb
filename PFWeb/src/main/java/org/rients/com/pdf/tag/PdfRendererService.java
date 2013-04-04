package org.rients.com.pdf.tag;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.rients.com.constants.Constants;
import org.rients.com.utils.DateParser;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class PdfRendererService {
    
    public void process() throws IOException, DocumentException, ParserConfigurationException, SAXException {
        // Create a buffer to hold the cleaned up HTML
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Clean up the HTML to be well formed
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        
        InputStream input = getConnection();
        
        TagNode node = cleaner.clean(input);
        // Instead of writing to System.out we now write to the ByteArray buffer
        new PrettyXmlSerializer(props).writeToStream(node, out);

        // Create the PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(new String(out.toByteArray()));
        renderer.layout();
        String outputFilename = Constants.PDF_GENERATION_OUTPUTFILE;
        String today = DateParser.printDate(Calendar.getInstance());
        outputFilename = StringUtils.replace(outputFilename, "_date_", today);
        //outputFilename = outputFilename.replaceAll("_date_", today);
        OutputStream outputStream = new FileOutputStream(outputFilename);
        renderer.createPDF(outputStream);

        // Finishing up
        renderer.finishPDF();
        out.flush();
        out.close();
        
    }
    private InputStream getConnection() throws MalformedURLException, IOException {
        String urlString = Constants.PDF_GENERATION_URL_1;
        URL url = new URL(urlString);
        InputStream input = null;
        URLConnection urlconnection = url.openConnection();
        try {
            input = urlconnection.getInputStream();
        } catch (ConnectException e) {
            urlString = Constants.PDF_GENERATION_URL_2;
            url = new URL(urlString);
            urlconnection = url.openConnection();
            input = urlconnection.getInputStream();
        }
        return input;
    }
    
    public static void main(String[] args) throws IOException, DocumentException, ParserConfigurationException, SAXException {
        PdfRendererService service  = new PdfRendererService();
        
        service.process();
    }
}
