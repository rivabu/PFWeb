package rients.trading.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import rients.trading.utils.StackTraceUtils;

/**
 * @author Rients
 * 
 */
public class FileDownloadServiceImpl implements FileDownloadService {
    private static final Logger LOGGER = Logger.getLogger(FileDownloadServiceImpl.class);

    public String downloadFile(final String urlString) throws IOException {
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
    public String downloadFileOld(String url) {
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
