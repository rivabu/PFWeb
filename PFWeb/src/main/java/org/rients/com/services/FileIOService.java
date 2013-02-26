/**
 *
 */
package org.rients.com.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rients.com.model.Dagkoers;
import org.rients.com.model.Transaction;



/**
 * @author Rients
 *
 */
public interface FileIOService {
    /**
     * @param path
     * @param filename
     * @param content
     * @throws IOException
     */
    public void addFileToDirectory(final String path, final String filename, final String content)
        throws IOException;

    /**
     * @param filename
     * @param content
     * @throws IOException
     */
    public void addFileToTempDirectory(final String filename, final String content)
        throws IOException;

    /**
     * @param directory
     * @param filename
     * @return
     * @throws IOException
     */
    public List<String> readFromFile(String directory, String filename)
        throws IOException;

    /**
     * @param filename
     * @return
     * @throws IOException
     */
    public List<String> readFromFileInTemp(String filename)
        throws IOException;

    /**
     * @param directory
     * @param filename
     * @param list
     * @throws IOException
     */
    public void saveToFile(String directory, String filename, Collection<?> list)
        throws IOException;

    
    /**
     * @param filename
     * @throws IOException
     */
    public void removeFileFromTempDirectory(final String filename)
        throws IOException;

    /**
     * @param directory
     * @param filename
     * @return
     * @throws IOException
     */
    public ArrayList<Dagkoers> readFromSlotKoersenFile(String rootDir, String directory, String filename)
        throws IOException;

    public List<Transaction> readFromTransactiesFile(String directory, String filename, String fundName);
    /**
     * @param dirName
     * @return
     */
    public String[] getFilenamesFromDir(final String dirName);

    /**
     * Zoek de foldernaam waarin een file zit,zoekende vanaf de root dir.
     *
     * @param filename
     * @return
     */
    public String findFolderName(String filename);

}
