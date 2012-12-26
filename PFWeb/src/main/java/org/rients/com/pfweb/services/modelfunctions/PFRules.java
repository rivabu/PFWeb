package org.rients.com.pfweb.services.modelfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.constants.Constants;
import org.rients.com.pfweb.utils.FileUtils;

import rients.trading.download.model.DagkoersStatus;
import rients.trading.download.model.Modelregel;
import rients.trading.download.model.Transaction;

public class PFRules {

    
    public void setDoubleTopAndBottom(ArrayList<Modelregel> pfData) {
        ArrayList<Modelregel> tops = new ArrayList<Modelregel>();
        ArrayList<Modelregel> bottoms = new ArrayList<Modelregel>();
        int i = 1;
        Modelregel modelregel = null;
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            int k = modelregel1.getKolomnr();
            String s = modelregel1.getSign();
            if (s.equals("o") && k > i)
                tops.add(modelregel);
            if (s.equals("x") && k > i)
                bottoms.add(modelregel);
            if (!s.equals("-") && !s.equals("+")) {
                i = k;
                modelregel = modelregel1;
            }
        }
        Modelregel previousTop = null;
        for (Modelregel mr : tops) {
            if (previousTop != null) {
                if (previousTop.getRijnr() == mr.getRijnr()) {
                    previousTop.setStatus(DagkoersStatus.DOUBLE_TOP);
                    mr.setStatus(DagkoersStatus.DOUBLE_TOP);
                }

            }
            previousTop = mr;
        }
        Modelregel previousBottom = null;
        for (Modelregel mr : bottoms) {
            if (previousBottom != null) {
                if (previousBottom.getRijnr() == mr.getRijnr()) {
                    previousBottom.setStatus(DagkoersStatus.DOUBLE_BOTTOM);
                    mr.setStatus(DagkoersStatus.DOUBLE_BOTTOM);
                }

            }
            previousBottom = mr;
        }
    }
    /**
     * @param pfData
     * @param fileName
     */
    public List<Transaction> getOptimalDecisions(ArrayList<Modelregel> pfData, String fileName, boolean saveToFile) {
        Transaction trans = null;
        float totalScore = 0;
        List<Transaction> transactions = new ArrayList<Transaction>();
        Modelregel lastModelregel = null;
        String switchDatum = null;
        float swithKoers = 0f;
        for(Modelregel modelregel: pfData) {
            // kolomnr starts with 1
            lastModelregel = modelregel;
            
            
            if (modelregel.getKolomnr() >=  2) {
                if (modelregel.isStijger() ) {
                     // sluit verkooptrans
                     if (trans != null && trans.getType() == Constants.SHORT) {
                         trans.setEndDate(switchDatum);
                         trans.setEndRate(swithKoers);
                         transactions.add(trans);
                         totalScore = totalScore + trans.getScore();
                     }
                     if (trans == null || trans.getType() == Constants.SHORT) {
                         trans = new Transaction();
                         trans.setStartDate(switchDatum);
                         trans.setStartRate(swithKoers);
                         trans.setType(Constants.LONG);
                     }
                } 
                else 
                {
                    // sluit kooptrans
                    if (trans != null && trans.getType() == Constants.LONG) {
                         trans.setEndDate(switchDatum);
                         trans.setEndRate(swithKoers);
                         transactions.add(trans);
                         totalScore = totalScore + trans.getScore();
                     }
                    if (trans == null || trans.getType() == Constants.LONG) {
                        // nu verkopen
                        trans = new Transaction();
                        trans.setStartDate(switchDatum);
                        trans.setStartRate(swithKoers);
                        trans.setType(Constants.SHORT);
                    }
                }
            }
            if (modelregel.isStijger()) {
                switchDatum = modelregel.getHoogsteDatum();
                swithKoers= modelregel.getHoogsteKoers();
            } else {
                switchDatum = modelregel.getLaagsteDatum();
                swithKoers = modelregel.getLaagsteKoers();
            }
        }
        if (trans != null) {
            if (lastModelregel.isStijger()) {
                trans.setEndDate(lastModelregel.getHoogsteDatum());
                trans.setEndRate(lastModelregel.getHoogsteKoers());
            } else {
                trans.setEndDate(lastModelregel.getLaagsteDatum());
                trans.setEndRate(lastModelregel.getLaagsteKoers());
            }
            transactions.add(trans);
            totalScore = totalScore + trans.getScore();
        }
        
        if (saveToFile) {
           
            Iterator<Transaction> i = transactions.iterator();
            List<String> transactionsString  = new ArrayList<String>();
            while (i.hasNext()) {
                transactionsString.add(i.next().toString());
            }
            FileUtils.writeToFile(fileName, transactionsString);
         }
        System.out.println(fileName + ": " + totalScore);
        return transactions;
    }

}
