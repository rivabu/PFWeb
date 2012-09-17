/**
 *
 */
package rients.trading.services;

import java.util.List;


import rients.trading.download.model.Categorie;
import rients.trading.download.model.Dagkoers;


/**
 * @author Rients
 *
 */
public interface BehrOverzichtSplitter {
    /**
     * @param content
     * @return
     */
    Categorie extractURLs(String content, String category);

    /**
     * @param filename
     * @param lines
     * @return
     */
    List<Dagkoers> extractSlotkoersen(final String filename, List<String> lines);
}
