package org.rients.com.pfweb.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.indexpredictor.HighLowImageGenerator;
import org.rients.com.model.ImageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping()
/**
 * Servlet implementation class HitCount.
 */
public class MatrixImage  {
	
	private static final long serialVersionUID = 1L;
	
	@RequestMapping("/MatrixImage")
    public @ResponseBody byte[] getMatrixImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
        HighLowImageGenerator highLowImageGenerator = new HighLowImageGenerator();
    	String dir = request.getParameter("dir");
    	String type = request.getParameter("type");
    	ImageResponse imageResponse = highLowImageGenerator.getHighLowImage(type, dir);
        response.setContentType("image/png");
        
        return imageResponse.getContent();
    }
}
