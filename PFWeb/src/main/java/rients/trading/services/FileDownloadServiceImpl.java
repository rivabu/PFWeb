package rients.trading.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.rients.com.pfweb.utils.PropertiesUtils;

import rients.trading.utils.StackTraceUtils;
import rients.trading.utils.URLUtilities;
import sun.misc.BASE64Encoder;

/**
 * @author Rients
 * 
 */
public class FileDownloadServiceImpl implements FileDownloadService {
    private static final Logger LOGGER = Logger.getLogger(FileDownloadServiceImpl.class);

    public String downloadFileOld(final String urlString) throws IOException {
        final StringBuffer out = new StringBuffer();

        try {
            final URL url = new URL(urlString);
            final URLConnection urlconnection = url.openConnection();
            final InputStream inputStream = urlconnection.getInputStream();

            final byte[] bytes = new byte[4096];

            for (int number; (number = inputStream.read(bytes)) != -1;) {
                out.append(new String(bytes, 0, number));
            }
        } catch (IOException io) {
            LOGGER.error("Fatal read error: " + StackTraceUtils.getStackTrace(io));
        }

        return out.toString();
    }
    
    
    public String downloadFile(String urlString) throws IOException {
        StringBuffer output = new StringBuffer();
            BufferedReader bufferedreader = makeInternetConnection(urlString);
            boolean doorgaan = true;
            while (doorgaan) {
                String regel = bufferedreader.readLine();
                output.append(regel+"\n");
                if (regel == null){
                    break;
                }
            }

        return output.toString();
    }
    
    public BufferedReader makeInternetConnection(String URL) throws IOException {
        String decodedURL = URLUtilities.urlDecode(URL);
        BufferedReader bufferedreader = null;
        Properties props = PropertiesUtils.getPropertiesFromClasspath("application.properties");
        boolean useProxy = Boolean.parseBoolean((String)props.getProperty("useproxy"));
            Object obj = null;
            if (useProxy) {
                String host = (String)props.getProperty("host");
                String port = (String)props.getProperty("port");
                String userid = (String)props.getProperty("user");
                String password = (String)props.getProperty("password");
                System.getProperties().put("proxySet", "true");
                System.getProperties().put("proxyHost", host);
                System.getProperties().put("proxyPort", port);
                String auth = userid + ":" + password;
                String proxyURL = "Basic " + (new BASE64Encoder()).encode(auth.getBytes());
                URL url = new URL(decodedURL);
                URLConnection urlconnection = url.openConnection();
                urlconnection.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
                urlconnection.setRequestProperty("Proxy-Authorization", proxyURL);
                StreamTokenizer streamtokenizer = new StreamTokenizer(urlconnection.getInputStream());
                streamtokenizer.ordinaryChar(32);
                streamtokenizer.wordChars(32, 32);
                streamtokenizer.eolIsSignificant(true);
                streamtokenizer.commentChar(35);
                streamtokenizer.quoteChar(34);

                bufferedreader = new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
            } else {
                URL urlObject = new URL(decodedURL);
                URLConnection con = urlObject.openConnection();
                con.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
                 
                bufferedreader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
           
        return bufferedreader;
    }

    public String downloadFile(final String urlString, final String categorie) throws IOException {

        String result = null;
        //final StringBuffer out = new StringBuffer();

        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "Test Client");

        BufferedReader br = null;

        PostMethod method = new PostMethod(urlString);
        method.addParameter("fondsgroep", categorie.toUpperCase());

        try {
            int returnCode = client.executeMethod(method);

            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                // still consume the response body
                result = method.getResponseBodyAsString();
            } else {
                result = method.getResponseBodyAsString();
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            method.releaseConnection();
            if (br != null)
                try {
                    br.close();
                } catch (Exception fe) {
                }
        }

        return result;
    }

    /**
     * @param url
     * @return
     */
    public String downloadFile2(String url) {
        String endResult = null;
        HttpClient client = new HttpClient();
        GetMethod method = null;

        try {
            // Create a method instance.
            method = new GetMethod(url);

            // Provide custom retry handler is necessary
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

            byte[] responseBody = null;

            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Method failed: " + method.getStatusLine());
            }

            // Read the response body.
            responseBody = method.getResponseBody();
            endResult = new String(responseBody);
        } catch (HttpException e) {
            LOGGER.error("Fatal protocol violation: " + StackTraceUtils.getStackTrace(e));
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + StackTraceUtils.getStackTrace(e));
        } catch (Exception e) {
            LOGGER.error(StackTraceUtils.getStackTrace(e));
        } finally {
            // Release the connection.
            if (method != null) {
                method.releaseConnection();
            }
        }

        return endResult;
    }
}
