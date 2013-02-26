package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.constants.Constants;
import org.rients.com.services.FileIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping()

public class ShowTransactionsController {

    /** The Constant serialVersionUID. */
    private static long serialVersionUID = 1L;
    
    @Autowired
    FileIOService fileIOService;
    

    /**
     * Do get.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @RequestMapping(value = "/Transactions", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List list = fileIOService.readFromTransactiesFile(Constants.TRANSACTIONDIR, Constants.ALL_TRANSACTIONS, null);
        HttpSession httpSession = request.getSession();  
        httpSession.setAttribute("transactions", list); 
        return "transactions";
    }


    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }


    /**
     * @param serialversionuid the serialversionuid to set
     */
    public static void setSerialversionuid(long serialversionuid) {
        serialVersionUID = serialversionuid;
    }


    /**
     * @param fileIOService the fileIOService to set
     */
    public void setFileIOService(FileIOService fileIOService) {
        this.fileIOService = fileIOService;
    }
}
