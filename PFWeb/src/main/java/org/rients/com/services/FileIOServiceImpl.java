/**
 *
 */
package org.rients.com.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.Transaction;
import org.rients.com.utils.TimeUtils;


/**
 * @author Rients
 * 
 */
public class FileIOServiceImpl implements FileIOService {
    private static String sep;

    public FileIOServiceImpl(String rootDir, String tempDir) {
        super();
        this.rootDir = rootDir;
        this.tempDir = tempDir;
    }
    
    static {
        sep = System.getProperty("file.separator");
    }

    private String rootDir;
    private String tempDir;

    /**
     * @param filename
     * @param directory
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<String> readFromFile(String directory, String filename) throws IOException {
        File file = new File(getRootDir() + sep + directory + sep + filename);
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        return lines;
    }

    /**
     * @param directory
     * @param filename
     * @param list
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void saveToFile(String directory, String filename, Collection list) throws IOException {
        File dir = new File(directory);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullFilename = dir.getAbsolutePath() + sep + filename;
        FileUtils.writeLines(new File(fullFilename), "UTF-8", list);
    }

    

    
    

    /**
     * @param filename
     * @param directory
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public List<String> readFromFileInTemp(String filename) throws IOException {
        File file = new File(getTempDir() + sep + filename);
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        return lines;
    }

    /**
     * @param directory
     * @param filename
     * @param content
     * @throws IOException
     */
    public void addFileToDirectory(final String directory, final String filename, final String content) throws IOException {
        File dir = new File(getRootDir() + sep + directory);

        if (!dir.exists()) {
            boolean status = dir.mkdir();
        }

        String fullFilename = dir.getAbsolutePath() + sep + filename;
        FileUtils.writeStringToFile(new File(fullFilename), content, "UTF-8");
    }

    

    
    /**
     * @param filename
     * @param content
     * @throws IOException
     */
    public void addFileToTempDirectory(final String filename, final String content) throws IOException {
        File dir = new File(getTempDir());

        if (!dir.exists()) {
            dir.mkdir();
        }

        String fullFilename = dir.getAbsolutePath() + sep + filename;
        FileUtils.writeStringToFile(new File(fullFilename), content, "UTF-8");
    }

    /**
     * @param filename
     * @throws IOException
     */
    public void removeFileFromTempDirectory(final String filename) throws IOException {
        File dir = new File(getTempDir());
        String fullFilename = dir.getAbsolutePath() + sep + filename;
        FileUtils.forceDelete(new File(fullFilename));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * rients.trading.download.services.FileIOService#readFromSlotKoersenFile
     * (java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public ArrayList<Dagkoers> readFromSlotKoersenFile(String rootDir, String directory, String filename) throws IOException {
        File file = new File(rootDir + sep + directory + sep + filename);
        ArrayList<Dagkoers> slotKoersen = new ArrayList<Dagkoers>();

        if (file.exists()) {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            Iterator<String> i = lines.iterator();
            StringTokenizer stringtokenizer = null;

            while (i.hasNext()) {
                String line = i.next();
                stringtokenizer = new StringTokenizer(line.trim(), ",");

                if (stringtokenizer.countTokens() == 2) {
                    Dagkoers sk = new Dagkoers(stringtokenizer.nextToken().trim(), Float.parseFloat(stringtokenizer.nextToken()
                            .trim()));
                    slotKoersen.add(sk);
                }
            }
        }
        return slotKoersen;
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> readFromTransactiesFile(String directory, String filename, String fundName) {
        File file = new File(directory + sep + filename);
        List<Transaction> transactions = new ArrayList<Transaction>();
        try {
            if (file.exists()) {
                List<String> lines = FileUtils.readLines(file, "UTF-8");
                Iterator<String> i = lines.iterator();
                StringTokenizer stringtokenizer = null;
    
                while (i.hasNext()) {
                    String line = i.next();
                    stringtokenizer = new StringTokenizer(line.trim(), ",");
    
                    if (stringtokenizer.countTokens() >= 7) {
                        String readFundName = stringtokenizer.nextToken().trim();
                        int startDate = Integer.parseInt(stringtokenizer.nextToken().trim());
                        int endDate = Integer.parseInt(stringtokenizer.nextToken().trim());
                        float startRate = Float.parseFloat(stringtokenizer.nextToken().trim());
                        float endRate = Float.parseFloat(stringtokenizer.nextToken().trim());
                        int pieces = Integer.parseInt(stringtokenizer.nextToken().trim());
                        String type = stringtokenizer.nextToken().trim();
                        Transaction ct = new Transaction(readFundName, startDate, endDate, startRate, endRate, pieces, type);
                        if (ct.getEndDate() == -1) {
                            ct.setEndDate(TimeUtils.today());
                        }
                        if (ct.getFundName().equals(fundName)) {
                            transactions.add(ct);
                        }
                    }
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        return new Transaction().sort(transactions);
    }
    /**
     * @param dirName
     * @return
     */
    public String[] getFilenamesFromDir(final String dirName) {
        File dir = new File(dirName);

        if (dir.exists()) {
            return dir.list();
        }

        return new String[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * rients.trading.download.services.FileIOService#findFolderName(java.lang
     * .String)
     */
    public String findFolderName(final String filename) {
        File root = new File(getRootDir());
        String[] subDirs = root.list();

        for (int i = 0; i < subDirs.length; i++) {
            File dir = new File(getRootDir() + sep + subDirs[i]);

            if (dir.isDirectory()) {
                String[] files = dir.list();

                for (int j = 0; j < files.length; j++) {
                    if (files[j].equals(filename)) {
                        return subDirs[i];
                    }
                }
            }
        }

        return null;
    }

    /**
     * @return the rootDir
     */
    public String getRootDir() {
        return this.rootDir;
    }

    /**
     * @param rootDir
     *            the rootDir to set
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    /**
     * @return the tempDir
     */
    public String getTempDir() {
        return this.tempDir;
    }

    /**
     * @param tempDir
     *            the tempDir to set
     */
    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }
}
