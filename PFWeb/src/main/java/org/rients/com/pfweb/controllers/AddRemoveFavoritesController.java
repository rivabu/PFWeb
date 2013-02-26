/*
 * 
 */
package org.rients.com.pfweb.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rients.com.constants.Constants;
import org.rients.com.utils.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping()
/**
 * Servlet implementation class Overview.
 */
public class AddRemoveFavoritesController  {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    

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
    @RequestMapping(value = "/AddRemove", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the attribute and Forward to hello.jsp
        String action = request.getParameter("action");
        String currentDir = request.getParameter("dir");
        String fundName = request.getParameter("fund");
        String from = request.getParameter("from");
        
        if ("add".equals(action) && !currentDir.equals("aaa")) {
              FileUtils.copyFile(Constants.KOERSENDIR + currentDir + Constants.SEP + fundName + Constants.CSV, Constants.FAVORITESDIR + fundName + Constants.CSV);
        }
        if ("delete".equals(action) ) {
            FileUtils.removeFile(Constants.KOERSENDIR + currentDir + Constants.SEP + fundName + Constants.CSV);
        }  
        return "redirect:/ " + from + "?dir=" + currentDir;
    }



}
