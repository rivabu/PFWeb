package org.rients.com.servlet;


import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.indexpredictor.HighLowImageGenerator;
import org.rients.com.model.ImageResponse;


// TODO: Auto-generated Javadoc
/**
 * Servlet implementation class HitCount.
 */
public class MatrixImage extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public MatrixImage() {
        
    }

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {

		getMatrixImage(request, response);
    }  	
	
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
    	getMatrixImage(request, response);
    }
    
    /**
     * Gets the pF image.
     *
     * @param request the request
     * @param response the response
     * @return the pF image
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void getMatrixImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
        HighLowImageGenerator highLowImageGenerator = new HighLowImageGenerator();
    	String dir = request.getParameter("dir");
    	String type = request.getParameter("type");
    	ImageResponse imageResponse = highLowImageGenerator.getHighLowImage(type, dir);
        response.setContentType("image/png");
        OutputStream os = response.getOutputStream();
        ImageIO.write(imageResponse.getBuffer(), "png", os);
        os.close();
    }


}
