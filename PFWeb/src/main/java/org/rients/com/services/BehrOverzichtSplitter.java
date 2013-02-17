/**
 *
 */
package org.rients.com.services;

import java.util.List;

import org.rients.com.model.Categorie;
import org.rients.com.model.Dagkoers;




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
