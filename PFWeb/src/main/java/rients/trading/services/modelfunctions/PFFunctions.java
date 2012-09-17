package rients.trading.services.modelfunctions;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.Modelregel;
import rients.trading.download.model.Transaction;
import rients.trading.services.HandleFundData;
import rients.trading.services.HandlePF;

public class PFFunctions {

    HandlePF pfHandler = new HandlePF();
    List<Dagkoers> rates;
    
    public PFFunctions (String fundName, String directory) {
        HandleFundData fundData = new HandleFundData();
        rates = fundData.getFundRates(fundName, directory);

    }
    
    public ArrayList getHigherTopActions(String fundName, String directory, int turningPoint, float stepSize) {
        ArrayList transactions = new ArrayList();
        ArrayList PFData = pfHandler.createPFData(rates, fundName, directory, turningPoint, stepSize);
        Hashtable tops = new Hashtable();
        Hashtable bottoms = new Hashtable();
        getTopsAndBottoms(PFData, tops, bottoms);
        //System.out.println("tops size = "+tops.size());
        Transaction trans = null;
        boolean bought = false;
        boolean last = false;
        int boughtColumn = 0;
        for (int counter = 0; counter < PFData.size(); counter++) {
            Modelregel modelregel = (Modelregel) PFData.get(counter);
            Modelregel vorigeTop_1 = null;
            Modelregel vorigeBodem_1 = null;
            if(counter == PFData.size() - 1)
                last = true;
            if (modelregel.getKolomnr() < 3)
                continue;
            if (!isPlus(modelregel)) {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 1));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 2));
            } else {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 2));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 1));
            }
            if(!bought && isPlus(modelregel) /*&& modelregel.getRijnr() > vorigeTop_1.getRijnr() */&& boughtColumn != modelregel.getKolomnr())
            {
                trans = new Transaction();
                trans.setStartDate(modelregel.getDatum());
                trans.setStartRate(modelregel.getKoers());
                boughtColumn = modelregel.getKolomnr();
                bought = true;
                continue;
            }
            if(bought && (!isPlus(modelregel) || last)) {
                trans.setEndDate(modelregel.getDatum());
                trans.setEndRate(modelregel.getKoers());
                transactions.add(trans);
                bought = false;
                continue;
            }
            /*if(bought && fundName.startsWith("aex-index")){
                float profit = ((trans.getStartRate() - modelregel.getKoers())/modelregel.getKoers()) * 100;
                if(profit < -2) {
                    trans.setEndDate(modelregel.getDatum());
                    trans.setEndRate(modelregel.getKoers());
                    transactions.add(trans);
                    bought = false;
                    continue;
                }
            }*/
        }
        return transactions; 
    }

    public ArrayList getHigherTopsActions(String fundName, String directory, int turningPoint, float stepSize) {
        ArrayList transactions = new ArrayList();
        ArrayList PFData = pfHandler.createPFData(rates, fundName, directory, turningPoint, stepSize);
        Hashtable tops = new Hashtable();
        Hashtable bottoms = new Hashtable();
        getTopsAndBottoms(PFData, tops, bottoms);
        //System.out.println("tops size = "+tops.size());
        Transaction trans = null;
        boolean bought = false;
        boolean last = false;
        for (int counter = 0; counter < PFData.size(); counter++) {
            Modelregel modelregel = (Modelregel) PFData.get(counter);
            Modelregel vorigeTop_1 = null;
            Modelregel vorigeBodem_1 = null;
            Modelregel vorigeTop_2 = null;
            Modelregel vorigeBodem_2 = null;
            if(counter == PFData.size() - 1)
                last = true;
            if (modelregel.getKolomnr() < 5)
                continue;
            if (!isPlus(modelregel)) {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 1));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 2));
                vorigeTop_2 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 3));
                vorigeBodem_2 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 4));
            } else {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 2));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 1));
                vorigeTop_2 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 4));
                vorigeBodem_2 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 3));
            }
            if(!bought && isPlus(modelregel) && modelregel.getRijnr() > vorigeTop_1.getRijnr() && vorigeTop_1.getRijnr() >= vorigeTop_2.getRijnr())
            {
                trans = new Transaction();
                trans.setStartDate(modelregel.getDatum());
                trans.setStartRate(modelregel.getKoers());
                bought = true;
                continue;
            }
            if(bought && (!isPlus(modelregel) || last)) {
                trans.setEndDate(modelregel.getDatum());
                trans.setEndRate(modelregel.getKoers());
                transactions.add(trans);
                bought = false;
                continue;
            }
        }
        return transactions;
    }

    public ArrayList getLowerBottomsActions(String fundName, String directory, int turningPoint, float stepSize) {
        ArrayList transactions = new ArrayList();
        ArrayList PFData = pfHandler.createPFData(rates, fundName, directory, turningPoint, stepSize);
        Hashtable tops = new Hashtable();
        Hashtable bottoms = new Hashtable();
        getTopsAndBottoms(PFData, tops, bottoms);
        //System.out.println("tops size = "+tops.size());
        Transaction trans = null;
        boolean bought = false;
        boolean last = false;
        for (int counter = 0; counter < PFData.size(); counter++) {
            Modelregel modelregel = (Modelregel) PFData.get(counter);
            Modelregel vorigeTop_1 = null;
            Modelregel vorigeBodem_1 = null;
            if(counter == PFData.size() - 1)
                last = true;
            if (modelregel.getKolomnr() < 3)
                continue;
            if (!isPlus(modelregel)) {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 1));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 2));
            } else {
                vorigeTop_1 = (Modelregel) tops.get("" + (modelregel.getKolomnr() - 2));
                vorigeBodem_1 = (Modelregel) bottoms.get("" + (modelregel.getKolomnr() - 1));
            }
            if(!bought && !isPlus(modelregel) /*&& modelregel.getRijnr() < vorigeBodem_1.getRijnr()*/)
            {
                trans = new Transaction();
                trans.setStartDate(modelregel.getDatum());
                trans.setStartRate(modelregel.getKoers());
                bought = true;
                continue;
            }
            if(bought && (isPlus(modelregel) || last)) {
                trans.setEndDate(modelregel.getDatum());
                trans.setEndRate(modelregel.getKoers());
                transactions.add(trans);
                bought = false;
                continue;
            }
        }
        return transactions;
    }

    public void getTopsAndBottoms(ArrayList pfData, Hashtable tops, Hashtable bottoms) {
        int i = 1;
        Modelregel modelregel = null;
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            int k = modelregel1.getKolomnr();
            int _tmp = modelregel1.getRijnr();
            String s = modelregel1.getSign();
            if (s.equals("o") && k > i)
                tops.put("" + i, modelregel);
            if (s.equals("x") && k > i)
                bottoms.put("" + i, modelregel);
            if (!s.equals("-") && !s.equals("+")) {
                i = k;
                modelregel = modelregel1;
            }
        }
        //System.out.println("tops size = "+tops.size());
    }

    public boolean isPlus(Modelregel modelregel) {
        if (modelregel.getSign().equals("+") || modelregel.getSign().equals("x"))
            return true;
        else
            return false;
    }

}
