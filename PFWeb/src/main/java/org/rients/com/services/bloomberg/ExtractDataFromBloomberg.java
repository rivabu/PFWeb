package org.rients.com.services.bloomberg;

import java.io.IOException;

import org.rients.com.services.FileDownloadService;
import org.rients.com.services.FileDownloadServiceImpl;
import org.rients.com.utils.FileUtils;


public class ExtractDataFromBloomberg {

    FileDownloadService fileDownloader = new FileDownloadServiceImpl();

    public Article extractURLs(String bloombergQuery) {
        String query = "<!-- " + bloombergQuery + "-->";
        StringBuffer output = new StringBuffer();

        String out = "";
        try {
            out = fileDownloader.downloadFile(bloombergQuery);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int startpos = out.indexOf("<span class=\"news_story_title\">", 0);
        int endpos = output.indexOf("EDT</I>", startpos);
        if (endpos == -1)
            return null;
        else
            endpos = endpos + 7;
        out = out.substring(startpos, endpos);
        out = out.replaceAll("\n", " ");
        out = remove(out, "<a href", "\">");
        out = remove(out, "<div", "</div>");
        out = out.replaceAll("</a>", "");
        out = out.replaceAll("</div>", "");
        out = out.replaceAll("</p>", "<br />");
        out = out.replaceAll("<p>", "");
        out = out.replaceAll("<I>", "<br /><I>");
        String var = "<span class=\"news_story_title\">";
        String title = out.substring(out.indexOf(var) + var.length(), out.indexOf("</span>")).trim();
        String head = "<html><head>" + "<title>" + title + "</title>"
                + "<link media=\"screen\" type=\"text/css\" rev=\"stylesheet\" rel=\"stylesheet\" href=\"css\\news.css\">"
                + "<link media=\"screen\" type=\"text/css\" rev=\"stylesheet\" rel=\"stylesheet\" href=\"css\\main2.css\">" + query
                + "</head>" + "<body>" + "<div class=\"articlepage\">" + "<div class=\"contentbox article\">";

        String footer = "</div></div></body></html>";
        String lastUpdated = out.substring(out.indexOf("<I>") + 3, out.indexOf("EDT</I>") + 3).trim();

        out = head + out + footer;
        out = makeHeadersBold(out);
        String filename = "C:\\trading simulator\\bloombergfiles\\" + title + ".html";
        FileUtils.writeToFile(filename, out);
        Article article = new Article();
        article.setAuthor(findAuthor(out));
        article.setTitle(title);
        article.setLength(out.length());
        article.setLastUpdated(lastUpdated);
        return article;
    }

    public String findAuthor(String text) {
        String author = "unknown";
        String br = "<br />";
        int startpos = text.indexOf("<br>", 0);
        int endpos = text.indexOf(br, 0);
        String temp = text.substring(startpos, endpos).trim();
        if (temp.indexOf("By") != -1) {
            author = temp.substring(8);
        }
        return author;
    }

    public String makeHeadersBold(String text) {
        String br = "<br />";
        int startpos = 0;
        while (true) {
            startpos = text.indexOf(br, startpos);
            int endpos = text.indexOf(br, startpos + 1);
            if (startpos == -1 || endpos == -1)
                break;
            if ((endpos - startpos) < 50) {
                String middle = text.substring((startpos), endpos).trim();
                if (middle.length() > 3 && !middle.equals(br)) {
                    String before = text.substring(0, startpos);
                    String after = text.substring(endpos);
                    text = before + br + "<b>" + middle + "</b>" + after;
                }
            }
            startpos = endpos;
        }
        return text;
    }

    public String remove(String input, String start, String end) {
        while (true) {
            int startpos = input.indexOf(start, 0);
            int endpos = input.indexOf(end, startpos);
            if (startpos == -1 || endpos == -1)
                break;
            input = input.substring(0, startpos) + " " + input.substring(endpos + end.length());
        }
        return input;
    }

    public String removeSpaces(String text) {
        String[] words = text.split(" ");
        String newSentence = "";
        for (int i = 0; i < words.length; i++) {
            String currentWord = words[i];
            if (!currentWord.equalsIgnoreCase("")) {
                newSentence += currentWord + " ";
            }
        }
        return newSentence;
    }

}