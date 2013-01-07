package org.rients.com.pdf.tag;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

public class PdfRenderer {
    public static void main(String[] args) throws IOException, DocumentException, ParserConfigurationException, SAXException {

        // Create a buffer to hold the cleaned up HTML
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Clean up the HTML to be well formed
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        
        URL url = new URL("http://127.0.0.1:8060/PFWeb/PDFOverview");
        URLConnection urlconnection = url.openConnection();
        InputStream input = urlconnection.getInputStream();
        
        TagNode node = cleaner.clean(input);
        // Instead of writing to System.out we now write to the ByteArray buffer
        new PrettyXmlSerializer(props).writeToStream(node, out);

        // Create the PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(new String(out.toByteArray()));
        renderer.layout();
        OutputStream outputStream = new FileOutputStream("HTMLasPDF.pdf");
        renderer.createPDF(outputStream);

        // Finishing up
        renderer.finishPDF();
        out.flush();
        out.close();
    }
}
