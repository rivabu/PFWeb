/*
 * 
 */
package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.constants.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import rients.trading.download.model.Categorie;
import rients.trading.download.model.FondsURL;
import rients.trading.services.DoubleTopAndBottomsLocator;
import rients.trading.services.FundPropertiesService;
import rients.trading.services.FundPropertiesServiceImpl;

@Controller
@RequestMapping()
/**
 * Servlet implementation class Overview.
 */
public class TopBottomsServlet {

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
    @RequestMapping(value = "/TopBottoms", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            if (session.getAttribute("categories") == null) {
                DoubleTopAndBottomsLocator doubleTopAndBottomsLocator = new DoubleTopAndBottomsLocator();
                doubleTopAndBottomsLocator.setFavouritesDir(Constants.KOERSENDIR);
                ArrayList<Categorie> matchedCategoriesList = doubleTopAndBottomsLocator.locate();
                session.setAttribute("categories", matchedCategoriesList);
            }
            if (request.getParameter("topbottomscategorie") != null) {
                String topbottomscategorie = request.getParameter("topbottomscategorie");
                if (session.getAttribute("categories") != null) {
                    ArrayList<Categorie> matchedCategoriesList = (ArrayList<Categorie>) session.getAttribute("categories");
                    for (Categorie cat : matchedCategoriesList) {
                        if (cat.getNaam().equals(topbottomscategorie)) {
                            List<String> files = new ArrayList<String>();
                            for (FondsURL fondsURL : cat.getItems()) {
                                files.add(fondsURL.getNaam());
                            }
                            List<Properties> fileProperties = fundPropertiesService.getFileProperties(files);
                            request.setAttribute("files", fileProperties);
                            request.setAttribute("dir", topbottomscategorie);

                            break;
                        }
                    }
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "topbottoms";
    }
}