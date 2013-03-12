package org.rients.com.pfweb.controllers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.rients.com.constants.Constants;
import org.rients.com.model.Transaction;
import org.rients.com.services.FileIOService;
import org.rients.com.utils.PropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping()

public class TransactionDetailsController {

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
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/TransactionDetails", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int transId = Integer.parseInt(request.getParameter("id"));
        HttpSession httpSession = request.getSession();  
        List<Transaction> transactions = null;
        if (httpSession.getAttribute("transactions") != null) {
            transactions = (List<Transaction>) httpSession.getAttribute("transactions");
        } else {
            transactions = fileIOService.readFromTransactiesFile(Constants.TRANSACTIONDIR, Constants.ALL_TRANSACTIONS, null);
        }
        Transaction myTrans = findTransaction(transId, transactions);
        String realFundName = findRealFundName(myTrans.getFundName());
        myTrans.setRealFundName(realFundName);
        httpSession.setAttribute("trans", myTrans);
            return "transactionDetails";
    }

    private Transaction findTransaction(int transId, List<Transaction> transactions) {
        Transaction myTrans = null;
        Iterator<Transaction> iter = transactions.iterator();
        while (iter.hasNext()) {
            myTrans = iter.next();
            if (myTrans.getBuyId() == transId) {
                break;
            }
        }
        return myTrans;
    }

    private String findRealFundName(String turboName) {
        Properties prop = PropertiesUtils.getPropertiesFromClasspath(Constants.TURBO_MAPPINGS);
        Enumeration<Object> keys = prop.keys();
        String value = null;
        while(keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (turboName.toLowerCase().indexOf(key.toLowerCase()) >= 0) {
                value = prop.getProperty(key);
            }
        }
        if (value == null) {
            System.out.println("no mapping found for turbo: " + turboName);
        }
        if (value.endsWith(Constants.CSV)) {
            value = value.replaceAll(Constants.CSV, "");
        }
        return value;
    }

    /**
     * @param fileIOService the fileIOService to set
     */
    public void setFileIOService(FileIOService fileIOService) {
        this.fileIOService = fileIOService;
    }



}
