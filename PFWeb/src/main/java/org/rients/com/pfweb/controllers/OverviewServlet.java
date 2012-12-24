/*
 * 
 */
package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.rients.com.constants.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import rients.trading.services.FundPropertiesService;
import rients.trading.services.FundPropertiesServiceImpl;
import rients.trading.utils.FileUtils;

@Controller
@RequestMapping()
/**
 * Servlet implementation class Overview.
 */
public class OverviewServlet  {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    FundPropertiesService fundPropertiesService = new FundPropertiesServiceImpl();


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
    @RequestMapping(value = "/Overview", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Set the attribute and Forward to hello.jsp
            String dir = request.getParameter("dir");
            List<String> subdirs = FileUtils.getSubdirs(Constants.KOERSENDIR);
            if (StringUtils.isBlank(dir)) {
                dir = subdirs.get(0);
            }
            List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);

            List<Properties> fileProperties = fundPropertiesService.getFileProperties(files);
            request.setAttribute("files", fileProperties);
            request.setAttribute("dirs", subdirs);
            request.setAttribute("dir", dir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "overview";
    }

}
