package org.rients.com.pfweb.controllers;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.graph.TransactionGraph;
import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.RSIGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping()
 
public class TransactionImageController  {
    
    @Autowired
    RSIGenerator rSIGenerator;

    @RequestMapping("/TransactionImage")
    public @ResponseBody byte[] getRSIImage(HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
    	
        TransactionGraph graph = new TransactionGraph();

        ImageResponse imageResponse = graph.generate();
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
