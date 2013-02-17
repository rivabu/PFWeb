/**
 *
 */
package org.rients.com.services;

import java.util.List;

import org.rients.com.model.Dagkoers;



/**
 * @author Rients
 *
 */
public interface BehrDownloadService {
    /**
     * @param category
     */
    void downloadCategory(String category);

    /**
     * @param oldData
     * @param newData
     * @return
     */
    List<Dagkoers> mergeOldWithNew(final List<Dagkoers> oldData, final List<Dagkoers> newData);
    
    void downloadFavorites();
}
