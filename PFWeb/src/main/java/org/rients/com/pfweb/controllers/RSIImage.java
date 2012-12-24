package org.rients.com.pfweb.controllers;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.model.ImageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import rients.trading.services.RSIGenerator;


@Controller
@RequestMapping()
 
public class RSIImage  {
    
    @RequestMapping("/RSIImage")
    public @ResponseBody byte[] getRSIImage(HttpServletRequest request, 
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
        return imageResponse.getContent();
    }

}
