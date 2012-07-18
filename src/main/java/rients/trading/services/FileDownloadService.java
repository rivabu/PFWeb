/**
 *
 */
package rients.trading.services;

import java.io.IOException;


/**
 * @author Rients
 *
 */
public interface FileDownloadService {
    /**
     * @param url
     * @return
     * @throws IOException
     */
    public String downloadFile(String url) throws IOException;
    public String downloadFile(String url, String categorieName) throws IOException;
}
