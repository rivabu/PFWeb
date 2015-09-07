package org.rients.com.pfweb.controllers;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.RSIGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping()
 
public class RSIImageController  {
    
    @Autowired
    RSIGenerator rSIGenerator;

    @RequestMapping("/RSIImage")
    public @ResponseBody byte[] getRSIImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
        String dir = request.getParameter("dir");
        String fundName = request.getParameter("fund") ;
        String size = request.getParameter("size") ;

        ImageResponse imageResponse = null;
        if (fundName == null) {
            imageResponse = rSIGenerator.getImage(dir);
        } else {
            imageResponse = rSIGenerator.getImage(dir, fundName, size);
            
        }
        response.setContentType("image/png");
        return imageResponse.getContent();
    }

    /**
     * @param rSIGenerator the rSIGenerator to set
     */
    public void setRSIGenerator(RSIGenerator rSIGenerator) {
        this.rSIGenerator = rSIGenerator;
    }

}
