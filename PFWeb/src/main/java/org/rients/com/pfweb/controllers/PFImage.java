package org.rients.com.pfweb.controllers;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.PFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import rients.trading.download.model.FundInfo;

@Controller
@RequestMapping()
public class PFImage {
	
    @Autowired
    PFGenerator pFGenerator;

    @RequestMapping("/PFImage")
    public  void getPFImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
    	String fundName = request.getParameter("fund");
    	int turningPoint = Integer.parseInt(request.getParameter("turningPoint"));
    	float stepSize = Float.parseFloat(request.getParameter("stepSize"));
    	String dir = request.getParameter("dir");
        String row = request.getParameter("row");
        int maxcolumns = -1;
        if (request.getParameter("maxcolumns") != null) {
            maxcolumns = Integer.parseInt(request.getParameter("maxcolumns"));
        }
    	ImageResponse imageResponse = pFGenerator.getImage(dir, fundName, turningPoint, stepSize, maxcolumns);
        HttpSession session = request.getSession();
        FundInfo fundInfo = new FundInfo();
        fundInfo.setFundName(fundName);
        fundInfo.setFirstDate(imageResponse.getFirstDate());
        fundInfo.setDivName(fundName + "_" + row);
        
        putFundInfoInSession(session, fundInfo);
        response.setContentType("image/png");

        OutputStream os = response.getOutputStream();
        try {
            ImageIO.write(imageResponse.getBuffer(), "png", os);
        } catch (IndexOutOfBoundsException iob) {
            System.out.println("error (IndexOutOfBoundsException) met " + fundName + " in dir " + dir);
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
}
