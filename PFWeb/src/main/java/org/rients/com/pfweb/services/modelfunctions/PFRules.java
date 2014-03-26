package org.rients.com.pfweb.services.modelfunctions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.model.Modelregel;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.RSI;


public class PFRules {

    public void setTopsAndBottoms(ArrayList<Modelregel> pfData) {
        int maxTop = -1;
        int maxBottom = Integer.MAX_VALUE;;
        
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            int r = modelregel1.getRijnr();
            if (r < maxBottom) {
                maxBottom = r;
            }
            if (r > maxTop) {
                maxTop = r;
            }
        }
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            int r = modelregel1.getRijnr();
            if (r == maxBottom) {
                modelregel1.setStatus(DagkoersStatus.BOTTOM);
            }
            if (r == maxTop) {
                modelregel1.setStatus(DagkoersStatus.TOP);
            }
        }
    }
    
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
    
    public void setRSI(List<Dagkoers> rates, ArrayList<Modelregel> pfData) {
        int DAGENTERUG = 25;
        Formula rsiCalculator = new RSI(DAGENTERUG);
        int days = rates.size();
        for (int j = 0; j < days; j++) {
            BigDecimal rsi = rsiCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            if (j >= DAGENTERUG) {
                setModelRegelRSI(pfData, rates.get(j).datum, rsi.intValue());
            }
        }
        boolean red = false;
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            if (modelregel1.getStatus() != DagkoersStatus.BIGMOVER_UP && modelregel1.getStatus() != DagkoersStatus.BIGMOVER_DOWN) {
                if (modelregel1.getRSI() >= 60) {
                    modelregel1.setStatus(DagkoersStatus.POS_RSI_LARGE);
            		red = false;
                } else if ((modelregel1.getRSI() < 60) && (modelregel1.getRSI() >= 50))  {
                    modelregel1.setStatus(DagkoersStatus.POS_RSI);
            		red = false;
                } else if ((modelregel1.getRSI() < 50) && (modelregel1.getRSI() >= 40))  {
                	if (red) {
                		modelregel1.setStatus(DagkoersStatus.BUY);
                		red = false;
                	} else {
                        modelregel1.setStatus(DagkoersStatus.NEG_RSI);
                		red = false;
                	}
                } else if (modelregel1.getRSI() > 0){
                    modelregel1.setStatus(DagkoersStatus.NEG_RSI_LARGE);
                    red = true;
                }
            }
            
        }
    }
    
    private void setModelRegelRSI(ArrayList<Modelregel> pfData, String date, int RSI) {
        float koers = 0;
        for (int i = 0; i < pfData.size(); i++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(i);
            if (modelregel1.getDatumInt() == Integer.parseInt(date)) {
                modelregel1.setRSI(RSI);
                if ((koers > 0) && ((modelregel1.getKoers() / koers ) < 0.95)) {
                    //modelregel1.setStatus(DagkoersStatus.BIGMOVER_DOWN);
                }
                if ((koers > 0) && ((koers /  modelregel1.getKoers()) < 0.95)) {
                    //modelregel1.setStatus(DagkoersStatus.BIGMOVER_UP);
                }
            } else {
                koers = modelregel1.getKoers();
            }
            if (modelregel1.getDatumInt() > Integer.parseInt(date)) {
                break;
            }
            
        }
    }

        public void setTransaction(ArrayList<Modelregel> pfData, Transaction trans) {
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel = (Modelregel) pfData.get(j);
            if (modelregel.getDatumInt() >= trans.getStartDate() && modelregel.getDatumInt() <= trans.getEndDate()) {
                if (trans.getScoreAbs() > 0) {
                    modelregel.setStatus(DagkoersStatus.BIGMOVER_UP);
                } else {
                    modelregel.setStatus(DagkoersStatus.BIGMOVER_DOWN);
                    
                }
            }
        }
        
    }
    /**
     * @param pfData
     * @param fileName
     */
    public List<Transaction> getOptimalDecisions(List<Modelregel> pfData, String fileName, boolean saveToFile) {
        Transaction trans = null;
        float totalScore = 0;
        List<Transaction> transactions = new ArrayList<Transaction>();
        Modelregel lastModelregel = null;
        int switchDatum = 0;
        float swithKoers = 0f;
        for(Modelregel modelregel: pfData) {
            // kolomnr starts with 1
            lastModelregel = modelregel;
            
            
            if (modelregel.getKolomnr() >=  2) {
                if (modelregel.isStijger() ) {
                     // sluit verkooptrans
                     if (trans != null && trans.getType() == Type.SHORT) {
                         trans.setEndDate(switchDatum);
                         trans.setEndRate(swithKoers);
                         transactions.add(trans);
                         totalScore = totalScore + trans.getScorePerc();
                     }
                     if (trans == null || trans.getType() == Type.SHORT) {
                         trans = new Transaction();
                         trans.setStartDate(switchDatum);
                         trans.setStartRate(swithKoers);
                         trans.setType(Type.LONG);
                     }
                } 
                else 
                {
                    // sluit kooptrans
                    if (trans != null && trans.getType() == Type.LONG) {
                         trans.setEndDate(switchDatum);
                         trans.setEndRate(swithKoers);
                         transactions.add(trans);
                         totalScore = totalScore + trans.getScorePerc();
                     }
                    if (trans == null || trans.getType() == Type.LONG) {
                        // nu verkopen
                        trans = new Transaction();
                        trans.setStartDate(switchDatum);
                        trans.setStartRate(swithKoers);
                        trans.setType(Type.SHORT);
                    }
                }
            }
            if (modelregel.isStijger()) {
                switchDatum = Integer.parseInt(modelregel.getHoogsteDatum());
                swithKoers= modelregel.getHoogsteKoers();
            } else {
                switchDatum = Integer.parseInt(modelregel.getLaagsteDatum());
                swithKoers = modelregel.getLaagsteKoers();
            }
        }
        if (trans != null) {
            if (lastModelregel.isStijger()) {
                trans.setEndDate(Integer.parseInt(lastModelregel.getHoogsteDatum()));
                trans.setEndRate(lastModelregel.getHoogsteKoers());
            } else {
                trans.setEndDate(Integer.parseInt(lastModelregel.getLaagsteDatum()));
                trans.setEndRate(lastModelregel.getLaagsteKoers());
            }
            transactions.add(trans);
            totalScore = totalScore + trans.getScorePerc();
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
