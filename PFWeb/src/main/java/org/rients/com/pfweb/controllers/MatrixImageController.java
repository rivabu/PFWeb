package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.HighLowImageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
/**
 * Servlet implementation class HitCount.
 */
public class MatrixImageController  {
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	HighLowImageGenerator highLowImageGenerator;
	
	@RequestMapping("/MatrixImage")
    public void getMatrixImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	String dir = request.getParameter("dir");
    	String type = request.getParameter("type");
    	 String graphType = request.getParameter("graphtype");
         if (graphType == null) {
             graphType = "EXP";
         }
    	ImageResponse imageResponse = highLowImageGenerator.getHighLowImage(type, dir, graphType);
        response.setContentType("image/png");
        
        OutputStream os = response.getOutputStream();
        try {
            ImageIO.write(imageResponse.getBuffer(), "png", os);
        } catch (IOException io) {
            io.printStackTrace();
        }
        os.close();
	}

    /**
     * @param highLowImageGenerator the highLowImageGenerator to set
     */
    public void setHighLowImageGenerator(HighLowImageGenerator highLowImageGenerator) {
        this.highLowImageGenerator = highLowImageGenerator;
    }
}
