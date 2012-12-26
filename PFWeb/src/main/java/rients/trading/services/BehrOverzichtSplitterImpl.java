/**
 *
 */
package rients.trading.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.rients.com.pfweb.utils.MathFunctions;

import rients.trading.download.exception.ValidationException;
import rients.trading.download.model.Categorie;
import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.FondsURL;
import rients.trading.utils.TimeUtils;

/**
 * @author Rients
 * 
 */
public class BehrOverzichtSplitterImpl implements BehrOverzichtSplitter {
    private static final Logger LOG = Logger.getLogger(BehrOverzichtSplitterImpl.class);

    /**
     * @param content
     * @return
     */
    public Categorie extractURLs(String content, String category) {
        Categorie categorie = new Categorie();

        String match = "fondsdetail/detail/";
        String qoute = "\"";
        String fundName = null;
        int fromIndex = 0;

        LOG.debug(category);
        categorie.setNaam(category);
        while (true) {
            fromIndex = content.indexOf(match, fromIndex + 1);
            if (fromIndex == -1) {
                break;
            }
            fundName = content.substring(fromIndex  + match.length(), content.indexOf(qoute, fromIndex  + match.length()));

            FondsURL fondsURL = new FondsURL();
            fondsURL.setNaam(fundName);
            fondsURL.setURL(fundName);
            categorie.getItems().add(fondsURL);
        }
        return categorie;
    }

    

    public ArrayList<Dagkoers> extractSlotkoersen(final String filename, List<String> lines) {
        ArrayList<Dagkoers> slotKoersen = new ArrayList<Dagkoers>();
        Iterator<String> i = lines.iterator();
        int date = 0;
        int previousDate = 0;
        String line = "";

        while (i.hasNext()) {
            line = i.next().trim();

            line = line.replaceAll("<font color=blue>", "");
            line = line.replaceAll("<</font>", "");
            line = line.replaceAll("<font color=red>", "");
            line = line.replaceAll("<font color=red>", "");
            line = line.replaceAll("<b>", "");
            line = line.replaceAll("<</b>", "");
            line = line.replaceAll("</b>", "");
            line = line.replaceAll("<<</font >", "");

            if (!StringUtils.isBlank(line)) {
                StringTokenizer stringtokenizer = new StringTokenizer(line.trim(), ":");
                int tokens = stringtokenizer.countTokens();

                if (tokens == 2) {
                    try {
                        Dagkoers slotkoers = extractSlotkoers(filename, line);
                        date = Integer.valueOf(slotkoers.getDatum());

                        if (previousDate < date) {
                            previousDate = date;
                            slotKoersen.add(slotkoers);
                        } else {
                            LOG.error(filename + " :error with date order: " + line);
                        }
                    } catch (ParseException pe) {
                        // do nothing
                    }
                }
            }
        }

        return slotKoersen;
    }

    private Dagkoers extractSlotkoers(final String filename, String line) throws ParseException {
        int sepPos = line.indexOf(":");

        if (sepPos > -1) {
            String datum = line.substring(0, sepPos).trim();
            TimeUtils.yymmddToDate(datum);
            datum = "20" + datum;

            String koers = line.substring(sepPos + 1).trim();
            koers = koers.replace(',', '.');
            koers = koers.replace("-", "");
            koers = koers.replace('*', ' ').trim();
            // koers = koers.substring("&nbsp;".length());
            koers = koers.replaceAll("[A-Za-z]", "");

            return new Dagkoers(datum, Float.parseFloat(MathFunctions.round(koers)));
        }

        LOG.error(filename + ": error in line: " + line);
        throw new ValidationException(filename + ": error in line: " + line);
    }

}
