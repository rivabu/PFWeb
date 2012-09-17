package org.rients.com.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.model.ImageResponse;

import rients.trading.services.RSIGenerator;


// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class HitCount.
 */
public class RSIImage extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public RSIImage() {
        
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {

	    getRSIImage(request, response);
    }  	
	
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
        getRSIImage(request, response);
    }
    
    /**
     * Gets the pF image.
     *
     * @param request the request
     * @param response the response
     * @return the pF image
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void getRSIImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
        RSIGenerator RSIGenerator = new RSIGenerator();
        String dir = request.getParameter("dir");
        String fundName = request.getParameter("fund");
        ImageResponse imageResponse = null;
        if (fundName == null) {
            imageResponse = RSIGenerator.getImage(dir);
        } else {
            imageResponse = RSIGenerator.getImage(dir, fundName);
            
        }
        response.setContentType("image/png");
        
        response.getOutputStream().write(imageResponse.getContent());
        
    }

}
