package org.rients.com.pfweb.controllers;


import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.constants.Constants;
import org.rients.com.model.FundInfo;
import org.rients.com.model.ImageResponse;
import org.rients.com.model.Transaction;
import org.rients.com.pfweb.services.PFGenerator;
import org.rients.com.services.FileIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping()
public class PFImageController {
	
    @Autowired
    PFGenerator pFGenerator;
    
    @Autowired
    FileIOService fileIOService;

    @RequestMapping("/PFImage")
    public  void getPFImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
        String fundName = null;
        String dir = null;
        Transaction trans = null;
        String graphType = request.getParameter("graphtype");
        if (graphType == null) {
            graphType = "EXP";
        }
        String type = request.getParameter("type");
    	if (type.equals("default")) {
            fundName = request.getParameter("fund");
            dir = request.getParameter("dir");
    	}
    	if (type.equals("trans")) {
            HttpSession httpSession = request.getSession();  
            if (httpSession.getAttribute("trans") != null) {
                trans =  (Transaction) httpSession.getAttribute("trans");
                fundName = trans.getRealFundName();
                dir = fileIOService.findFolderName(fundName + Constants.CSV);
            }
    	}
    	int turningPoint = Integer.parseInt(request.getParameter("turningPoint"));
    	float stepSize = Float.parseFloat(request.getParameter("stepSize"));
        String row = request.getParameter("row");
        int maxcolumns = -1;
        if (request.getParameter("maxcolumns") != null) {
            maxcolumns = Integer.parseInt(request.getParameter("maxcolumns"));
        }
        int days = -1;
        if (request.getParameter("days") != null) {
        	days = Integer.parseInt(request.getParameter("days"));
        }
    	ImageResponse imageResponse = pFGenerator.getImage(type, trans, dir, fundName, graphType, turningPoint, stepSize, maxcolumns, days);
        HttpSession session = request.getSession();
        FundInfo fundInfo = new FundInfo();
        fundInfo.setFundName(fundName);
        if (imageResponse.getFirstDate() != null) {
            fundInfo.setFirstDate(imageResponse.getFirstDate());
        }
        fundInfo.setDivName(fundName + "_" + row);
        
        putFundInfoInSession(session, fundInfo);
        response.setContentType("image/png");

        OutputStream os = response.getOutputStream();
        try {
            ImageIO.write(imageResponse.getBuffer(), "png", os);
        } catch (IndexOutOfBoundsException iob) {
            System.out.println("error (IndexOutOfBoundsException) met " + fundName + " in dir " + dir);
        } catch (SocketException s) {
            System.out.println("error (SocketException)");
        }
        os.close();
    }

    /**
     * Put fund info in session.
     *
     * @param session the session
     * @param fundInfo the fund info
     */
    private synchronized void putFundInfoInSession(HttpSession session, FundInfo fundInfo) {
        List<FundInfo> firstDates = (List<FundInfo>) session.getAttribute("firstDates");
        if (firstDates == null) {
            firstDates = new ArrayList<FundInfo>();
        }
        firstDates.add(fundInfo);
        session.setAttribute("firstDates", firstDates);
    }

    /**
     * @param pFGenerator the pFGenerator to set
     */
    public void setPFGenerator(PFGenerator thispFGenerator) {
        pFGenerator = thispFGenerator;
    }
    
    /**
     * @param fileIOService the fileIOService to set
     */
    public void setFileIOService(FileIOService fileIOService) {
        this.fileIOService = fileIOService;
    }
}
