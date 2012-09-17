package rients.trading.services.bloomberg;

import java.io.IOException;
import java.util.ArrayList;

import rients.trading.services.FileDownloadService;
import rients.trading.services.FileDownloadServiceImpl;

public class ExtractMetaDataFromBloomberg {

    FileDownloadService fileDownloader = new FileDownloadServiceImpl();

    public ArrayList extractURLs(String bloombergQuery) {
        ArrayList list = new ArrayList();
        String output = "";
        String out = "";
        try {
            out = fileDownloader.downloadFile(bloombergQuery);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int endpos = 0;
        while (true) {
            String match1 = "<a class=\"summheadline\" href=\"/apps/news?pid=";
            int startpos = output.indexOf(match1, endpos);
            endpos = output.indexOf("\"", startpos + match1.length());
            if (startpos == -1 || endpos == -1)
                break;
            String headString = "<a class=\"summheadline\" href=\"";
            String match2 = output.substring(startpos, endpos);
            String temp = "http://www.bloomberg.com" + match2.substring(headString.length());

            list.add(temp);
        }
        return list;
    }
}