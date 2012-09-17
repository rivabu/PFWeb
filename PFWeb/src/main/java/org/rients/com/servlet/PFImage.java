package org.rients.com.servlet;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.model.ImageResponse;

import rients.trading.download.model.FundInfo;
import rients.trading.services.PFGenerator;


// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class HitCount.
 */
public class PFImage extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public PFImage() {
        
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {

		getPFImage(request, response);
    }  	
	
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
    	getPFImage(request, response);
    }
    
    /**
     * Gets the pF image.
     *
     * @param request the request
     * @param response the response
     * @return the pF image
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void getPFImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
    	PFGenerator PFGenerator = new PFGenerator();
    	String fundName = request.getParameter("fund");
    	int turningPoint = Integer.parseInt(request.getParameter("turningPoint"));
    	float stepSize = Float.parseFloat(request.getParameter("stepSize"));
    	String dir = request.getParameter("dir");
        String row = request.getParameter("row");
    	ImageResponse imageResponse = PFGenerator.getImage(dir, fundName, turningPoint, stepSize);
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




}
