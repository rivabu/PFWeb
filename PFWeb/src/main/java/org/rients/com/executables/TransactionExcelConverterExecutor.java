package org.rients.com.executables;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.rients.com.constants.Constants;
import org.rients.com.model.BuySell;
import org.rients.com.model.Transaction;
import org.rients.com.model.TransactionExcel;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;
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
        Collections.reverse(transExcel);
        System.out.println(transExcel.size());
        ArrayList<Transaction> transactions = convert(transExcel);
        System.out.println(transactions.size());
        String filename = Constants.ALL_TRANSACTIONS;
        FileUtils.writeToFile(filename, transactions);
    }
    
    private ArrayList<Transaction> convert(ArrayList<TransactionExcel> transExcel) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        Iterator<TransactionExcel> iterExcel = transExcel.iterator();

        while (iterExcel.hasNext()) {
            TransactionExcel transOld = iterExcel.next();
            if (transOld.getBuySell() == BuySell.BUY) {
                Transaction transaction = new Transaction(transOld.getFundName(), transOld.getDatum(), transOld.getNumber(), transOld.getKoers(), transOld.getPieces(), transOld.getType());
                transactions.add(transaction);
                iterExcel.remove();
            }
        }
        // find the related selling transaction
        Iterator<Transaction> iterNew = transactions.iterator();
        while (iterNew.hasNext()) {
            Transaction trans = iterNew.next();
            float totalPrice = 0;
            if (transExcel.size() == 0) {
                iterNew.remove();
            }
            iterExcel = transExcel.iterator();
            int numbersToSell = trans.getPieces();
            while (iterExcel.hasNext() && numbersToSell > 0) {
                TransactionExcel transSell = iterExcel.next();
                if (trans.getBuyId() > transSell.getNumber()) {
                    iterExcel.remove();
                    continue;
                }
                if (trans.getBuyId() < transSell.getNumber() && trans.getFundName().equals(transSell.getFundName())) {
                    int numberBought = trans.getPieces();
                    int numberSold = transSell.getPieces();
                    float endRate = transSell.getKoers();
                    numbersToSell = numberBought - numberSold;
                    int endDate = transSell.getDatum();
                    int lastSellId = transSell.getNumber();
                    // situatie 1
                    if (numbersToSell == 0) {
                        iterExcel.remove();
                        totalPrice = totalPrice + (numberSold * endRate);
                    }
                    // situatie 2
                    if (numbersToSell < 0) {
                        transSell.setPieces(numberSold - numberBought);
                        totalPrice = totalPrice + (numberBought * endRate);
                    }
                    // situatie 3 + 4
                    if (numbersToSell > 0) {
                        totalPrice = totalPrice + (numberSold * endRate);
                        iterExcel.remove();
                        while (numbersToSell > 0 && iterExcel.hasNext()) {
                            
                            transSell = iterExcel.next();
                            if (!trans.getFundName().equals(transSell.getFundName())) {
                                continue;
                            }
                            numberSold = transSell.getPieces();
                            endRate = transSell.getKoers();
                            endDate = transSell.getDatum();
                            lastSellId = transSell.getNumber();
                            if (numbersToSell >= numberSold) {
                                totalPrice = totalPrice + (numberSold * endRate);
                                iterExcel.remove();
                            }
                            if (numbersToSell < numberSold) {
                                transSell.setPieces(numberSold - numbersToSell);
                                totalPrice = totalPrice + numbersToSell * endRate;
                            }
                            numbersToSell = numbersToSell - numberSold;
                        }     
                    }
                    endRate = MathFunctions.divide(totalPrice, trans.getPieces());
                    trans.addSellInfo(endDate, lastSellId, endRate);
                } 
            }
        }
        return transactions;
    }
    
    /*
     * koop 100 a 10
     * verkoop 100 a 11
     * 
     * koop 100 a 10
     * koop 100 a 11
     * verkoop 200 a 12
     * 
     * koop 200 a 11
     * verkoop 100  a 11
     * verkoop 100  a 11
     * 
     * koop 100 a 10
     * koop 200 a 11
     * verkoop 25  a 11
     * verkoop 25  a 11
     * verkoop 250 a 12
     */
    
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
                
                BuySell buySell = null;
                if (soort.equalsIgnoreCase("koop")) {
                    buySell = BuySell.BUY;
                }
                if (soort.equalsIgnoreCase("verkoop")) {
                    buySell = BuySell.SELL;
                }
                Type type = Type.LONG;
                if (omschrijving.toLowerCase().indexOf("short") != -1) {
                    type = Type.SHORT;
                }
                if (buySell != null) {
                    
                    TransactionExcel trans = new TransactionExcel();
                    trans.setType(type);
                    trans.setBuySell(buySell);
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
