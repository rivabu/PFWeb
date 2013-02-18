package org.rients.com.executables;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.rients.com.constants.Constants;
import org.rients.com.model.BuySell;
import org.rients.com.model.TransactionExcel;
import org.rients.com.utils.MathFunctions;
import org.rients.com.utils.TimeUtils;

public class TransactionExcelConverterExecutor {

    /**
     * @param args
     */
    public static void main(String[] args) {
        TransactionExcelConverterExecutor tece = new TransactionExcelConverterExecutor();
        tece.process();

    }
    
    private void process() {
        ArrayList<TransactionExcel> transExcel = readExcel();
        System.out.println(transExcel.size());
    }
    
    private ArrayList<TransactionExcel> readExcel() {
        ArrayList<TransactionExcel> transExcel = new ArrayList<TransactionExcel>();
        
        String filename = Constants.TRANSACTIONS_EXCEL;
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
 
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            HSSFSheet worksheet = workbook.getSheetAt(0);
            
            int lastRowNum = worksheet.getLastRowNum();
            
            for (int i = 3; i <= lastRowNum; i++) {
            
                HSSFRow row1 = worksheet.getRow(i);
                
                HSSFCell cellA1 = row1.getCell(0);
                int orderNumber = new Double(cellA1.getNumericCellValue()).intValue();
                
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
                
                BuySell type = null;
                if (soort.equalsIgnoreCase("koop")) {
                    type = BuySell.BUY;
                }
                if (soort.equalsIgnoreCase("verkoop")) {
                    type = BuySell.SELL;
                }
                if (type != null) {
                    TransactionExcel trans = new TransactionExcel();
                    trans.setBuySell(type);
                    trans.setNumber(orderNumber);
                    trans.setDatum(TimeUtils.dateToInt(datum));
                    int firstSpace = omschrijving.indexOf(" ");
                    String aantal = omschrijving.substring(0, firstSpace).trim();
                    aantal = aantal.replaceAll("\\.", "");
                    int pieces = Integer.parseInt(aantal);
                    trans.setPieces(pieces);
                    trans.setFundName(omschrijving.substring(firstSpace).trim());
                    float koers = Math.abs(MathFunctions.divide(mutatie, pieces));
                    trans.setKoers(koers);
                    
                    transExcel.add(trans);
                }
                
            }

            
        } catch (IOException io) {
            
        }
        
        
        return transExcel;
    }

    
   
    
}
