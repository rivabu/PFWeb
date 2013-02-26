/*
 * 
 */
package org.rients.com.pfweb.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping()
/**
 * Servlet implementation class Overview.
 */
public class FileUploadController {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    protected String doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return "fileupload";
    }

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
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    protected String doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Create a factory for disk-based file items
        FileItemFactory factory = new DiskFileItemFactory();

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            // Parse the request
            @SuppressWarnings("unchecked")
            List<FileItem> items = upload.parseRequest(request);

            FileItem item = items.get(0);

            showExcelContent(item.getInputStream());

        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fileupload";
    }

    public void showExcelContent(InputStream fileInputStream) {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
            HSSFSheet worksheet = workbook.getSheetAt(0);
            int lastRowNum = worksheet.getLastRowNum();
            
            for (int i = 3; i <= lastRowNum; i++) {
            
                HSSFRow row1 = worksheet.getRow(i);
                
                HSSFCell cellA1 = row1.getCell(0);
                double orderNumber = cellA1.getNumericCellValue();
                
                HSSFCell cellB1 = row1.getCell(1);
                Date datum = cellB1.getDateCellValue();

                HSSFCell cellC1 = row1.getCell(2);
                String soort = cellC1.getStringCellValue();
                
                HSSFCell cellD1 = row1.getCell(3);
                String omschrijving = cellD1.getStringCellValue();
                
                HSSFCell cellE1 = row1.getCell(4);
                double mutatie = cellE1.getNumericCellValue();

                HSSFCell cellF1 = row1.getCell(5);
                double nieuwSaldo = cellF1.getNumericCellValue();

    
                System.out.println("A: " + orderNumber);
                System.out.println("B: " + datum);
                System.out.println("C: " + soort);
                System.out.println("D: " + omschrijving);
                System.out.println("E: " + mutatie);
                System.out.println("F: " + nieuwSaldo);
                
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
