package org.rients.com.pfweb.controllers;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.VotalityGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping()
 
public class VotalityImageController  {
    
    @Autowired
    VotalityGenerator votalityGenerator;

    @RequestMapping("/VotalityImage")
    public @ResponseBody byte[] getRSIImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
    	
        String dir = request.getParameter("dir");
        String fundName = request.getParameter("fund");
        
        ImageResponse imageResponse = null;
        if (fundName == null) {
            imageResponse = votalityGenerator.getImage(dir);
        } else {
            imageResponse = votalityGenerator.getImage(dir, fundName);
            
        }
        response.setContentType("image/png");
        return imageResponse.getContent();
    }

    /**
     * @param VotalityGenerator the VotalityGenerator to set
     */
    public void setVotalityGenerator(VotalityGenerator votalityGenerator) {
        this.votalityGenerator = votalityGenerator;
    }

}
