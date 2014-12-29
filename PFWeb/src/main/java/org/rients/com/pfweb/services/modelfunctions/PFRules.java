package org.rients.com.pfweb.services.modelfunctions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.rients.com.model.AllTransactions;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.model.Modelregel;
import org.rients.com.model.PFModel;
import org.rients.com.model.Transaction;
import org.rients.com.model.Type;
import org.rients.com.utils.FileUtils;
import org.rients.com.utils.Formula;
import org.rients.com.utils.RSI;


public class PFRules {

	/*
	 * gekocht, tenzij dalende trend
	 * dalende trend: isPlus & hw < vorige_top && vorigeBodem_1 < vorigeBodem_2
	 * dalende trend: !isPlus && hw < vorige_bottom && vorigeTop_1 < vorigeTop_2
	 */
	public void basicPF(ArrayList<Modelregel> modelRegels, AllTransactions transactions) {
        Hashtable<String, Modelregel> tops = new Hashtable<String, Modelregel>();
        Hashtable<String, Modelregel> bottoms = new Hashtable<String, Modelregel>();
        getTopsAndBottoms(modelRegels, tops, bottoms);
        //System.out.println("tops size = "+tops.size());
        Transaction trans = null;
        boolean bought = false;
        boolean last = false;
        boolean dalendeTrend = false;
        for (int counter = 0; counter < modelRegels.size(); counter++) {
            Modelregel modelregel = modelRegels.get(counter);
            Modelregel vorigeTop_1 = null;
            Modelregel vorigeBodem_1 = null;
            Modelregel vorigeTop_2 = null;
            Modelregel vorigeBodem_2 = null;
            if(counter == modelRegels.size() - 1)
                last = true;
            if (modelregel.getKolomnr() < 5)
                continue;
            if (modelregel.getSign().equals("+") || modelregel.getSign().equals("-")) {
            	continue;
            }
            if (!isPlus(modelregel)) {
                vorigeTop_1 = tops.get("" + (modelregel.getKolomnr() - 1));
                vorigeBodem_1 = bottoms.get("" + (modelregel.getKolomnr() - 2));
                vorigeTop_2 = tops.get("" + (modelregel.getKolomnr() - 3));
                vorigeBodem_2 = bottoms.get("" + (modelregel.getKolomnr() - 4));
            } else {
                vorigeTop_1 = tops.get("" + (modelregel.getKolomnr() - 2));
                vorigeBodem_1 =  bottoms.get("" + (modelregel.getKolomnr() - 1));
                vorigeTop_2 =  tops.get("" + (modelregel.getKolomnr() - 4));
                vorigeBodem_2 = bottoms.get("" + (modelregel.getKolomnr() - 3));
            }
            if ( isPlus(modelregel) && modelregel.getRijnr() < vorigeTop_1.getRijnr() && vorigeBodem_1.getRijnr() <=  vorigeBodem_2.getRijnr()) {
            	dalendeTrend = true;
            } else if ( !isPlus(modelregel) && modelregel.getRijnr() < vorigeBodem_1.getRijnr() && vorigeTop_1.getRijnr() <=  vorigeTop_2.getRijnr()) {
            	dalendeTrend = true;
            } else {
            	dalendeTrend = false;
            }
//            if(!bought && !dalendeTrend && isPlus(modelregel))
//            {
//                // kopen
//            	trans = new Transaction();
//                trans.setStartDate(Integer.parseInt(modelregel.getDatum()));
//                trans.setStartRate(modelregel.getKoers());
//                bought = true;
//                continue;
//            }
//            if(bought && (dalendeTrend || last)) {
//            	// verkopen
//                trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
//                trans.setEndRate(modelregel.getKoers());
//                transactions.add(trans);
//                bought = false;
//                continue;
//            }
            if(!bought && (dalendeTrend || last)) {
            	// kopen
            	trans = new Transaction();
                trans.setStartDate(Integer.parseInt(modelregel.getDatum()));
                trans.setStartRate(modelregel.getKoers());
                bought = true;
                continue;
            }
            if(bought && !dalendeTrend && isPlus(modelregel))
            {
                // verkopen
                trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
                trans.setEndRate(modelregel.getKoers());
                transactions.add(trans);
                bought = false;
                continue;


            }
        }
        
    }
	
	
    public boolean isPlus(Modelregel modelregel) {
        if (modelregel.getSign().equals("+") || modelregel.getSign().equals("x"))
            return true;
        else
            return false;
    }
    public void getTopsAndBottoms(ArrayList<Modelregel> pfData, Hashtable tops, Hashtable bottoms) {
        int i = 1;
        Modelregel modelregel = null;
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = pfData.get(j);
            int k = modelregel1.getKolomnr();
            //int _tmp = modelregel1.getRijnr();
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
    
    public void setRSI(List<Dagkoers> rates, ArrayList<Modelregel> pfData, AllTransactions transactions) {
        int DAGENTERUG = 40;
        Formula rsiCalculator = new RSI(DAGENTERUG);
        int days = rates.size();
        for (int j = 0; j < days; j++) {
            BigDecimal rsi = rsiCalculator.compute(new BigDecimal(rates.get(j).closekoers));
            if (j >= DAGENTERUG) {
                setModelRegelRSI(pfData, rates.get(j).datum, rsi.intValue());
            }
        }
        DagkoersStatus vorigeWaarde = null;
        for (int j = 0; j < pfData.size(); j++) {
            Modelregel modelregel1 = (Modelregel) pfData.get(j);
            if (modelregel1.getStatus() != DagkoersStatus.BIGMOVER_UP && modelregel1.getStatus() != DagkoersStatus.BIGMOVER_DOWN) {
            	if (vorigeWaarde == DagkoersStatus.NEG_RSI_LARGE && modelregel1.getRSI() >= 45) {
            		modelregel1.setStatus(DagkoersStatus.BUY);
            		if (modelregel1.getSign().equals("+")) {
            			continue;
            		}
            	}
            	else if (modelregel1.getRSI() >= 75) {
                    modelregel1.setStatus(DagkoersStatus.POS_RSI_VERY_LARGE);
                } else if (modelregel1.getRSI() >= 55) {
                    modelregel1.setStatus(DagkoersStatus.POS_RSI_LARGE);
                } else if ((modelregel1.getRSI() < 55) && (modelregel1.getRSI() >= 45))  {
                        modelregel1.setStatus(DagkoersStatus.POS_RSI);
               // } else if ((modelregel1.getRSI() < 45) && (modelregel1.getRSI() >= 40))  {
               //         modelregel1.setStatus(DagkoersStatus.NEG_RSI);
                } else if (modelregel1.getRSI() > 0){
                    modelregel1.setStatus(DagkoersStatus.NEG_RSI_LARGE); //red
                }
                vorigeWaarde = modelregel1.getStatus();
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
                    if (trans != null && trans.getType() == Type.SHORT) {
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


	public void overrideWithPF(ArrayList<Modelregel> modelRegels, AllTransactions transactions) {
		Hashtable<String, Modelregel> tops = new Hashtable<String, Modelregel>();
        Hashtable<String, Modelregel> bottoms = new Hashtable<String, Modelregel>();
        getTopsAndBottoms(modelRegels, tops, bottoms);
       
        //Transaction trans = null;
        //boolean bought = false;
        boolean last = false;
        boolean dalendeTrend = false;
        Stack<Transaction> portefeuille = new Stack<Transaction>();
        for (int counter = 0; counter < modelRegels.size(); counter++) {
            Modelregel modelregel = modelRegels.get(counter);
            Modelregel vorigeTop_1 = null;
            Modelregel vorigeBodem_1 = null;
            Modelregel vorigeTop_2 = null;
            Modelregel vorigeBodem_2 = null;
            if(counter == modelRegels.size() - 1)
                last = true;
            if (modelregel.getKolomnr() < 5)
                continue;
            if (modelregel.getSign().equals("+") || modelregel.getSign().equals("-")) {
            	// maak deze achteraf dezelfde kleur!
            	continue;
            }
            if (!isPlus(modelregel)) {
                vorigeTop_1 = tops.get("" + (modelregel.getKolomnr() - 1));
                vorigeBodem_1 = bottoms.get("" + (modelregel.getKolomnr() - 2));
                vorigeTop_2 = tops.get("" + (modelregel.getKolomnr() - 3));
                vorigeBodem_2 = bottoms.get("" + (modelregel.getKolomnr() - 4));
            } else {
                vorigeTop_1 = tops.get("" + (modelregel.getKolomnr() - 2));
                vorigeBodem_1 =  bottoms.get("" + (modelregel.getKolomnr() - 1));
                vorigeTop_2 =  tops.get("" + (modelregel.getKolomnr() - 4));
                vorigeBodem_2 = bottoms.get("" + (modelregel.getKolomnr() - 3));
            }
//            if ( isPlus(modelregel) && modelregel.getRijnr() >= vorigeTop_1.getRijnr() && vorigeBodem_1.getRijnr() >=  vorigeBodem_2.getRijnr()) {
//            	modelregel.setStatus(DagkoersStatus.BUY);
//            }	
//            if ( isPlus(modelregel) &&  vorigeTop_1.getRijnr() >= vorigeTop_2.getRijnr() && vorigeBodem_1.getRijnr() >=  vorigeBodem_2.getRijnr()) {
//            	modelregel.setStatus(DagkoersStatus.BUY);
//            }	
//            if ( !isPlus(modelregel) && modelregel.getRijnr() < vorigeTop_1.getRijnr() && vorigeBodem_1.getRijnr() <  vorigeBodem_2.getRijnr()) {
//            	modelregel.setStatus(DagkoersStatus.SELL);
//            }	
//            if ( !isPlus(modelregel) &&  vorigeTop_1.getRijnr() < vorigeTop_2.getRijnr() && vorigeBodem_1.getRijnr() <  vorigeBodem_2.getRijnr()) {
//            	modelregel.setStatus(DagkoersStatus.SELL);
//            }	
//            if ( !isPlus(modelregel) && modelregel.getRijnr() >= vorigeBodem_1.getRijnr() && vorigeTop_1.getRijnr() >=  vorigeTop_2.getRijnr()) {
//            	modelregel.setStatus(DagkoersStatus.BUY);
//            }

            if( (modelregel.getStatus() == DagkoersStatus.BUY && portefeuille.size() < 3)) {
            	// kopen
            	Transaction trans = new Transaction();
                trans.setStartDate(Integer.parseInt(modelregel.getDatum()));
                trans.setStartRate(modelregel.getKoers());
                trans.setPieces(1d);
                portefeuille.push(trans);
                continue;
            }
//            if(modelregel.getStatus() == DagkoersStatus.BUY && portefeuille.size() < 3 ) {
//            	// bijkopen
//            	Transaction trans = new Transaction();
//                trans.setStartDate(Integer.parseInt(modelregel.getDatum()));
//                trans.setStartRate(modelregel.getKoers());
//                trans.setPieces(1d);
//                portefeuille.add(trans);
//                continue;
//            }
            if (!portefeuille.isEmpty() && (modelregel.getStatus() == DagkoersStatus.POS_RSI_LARGE && !isPlus(modelregel)) || last)
            {
                // verkopen op groen rondje
            	if (!last) {
            		Transaction trans = portefeuille.pop();
                	trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
                    trans.setEndRate(modelregel.getKoers());
                    transactions.add(trans);
            	} else {
            		while (!portefeuille.isEmpty()) {
                		Transaction trans = portefeuille.pop();
                    	trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
                        trans.setEndRate(modelregel.getKoers());
                        transactions.add(trans);
            		}
            	}
                
//                if (!portefeuille.isEmpty()) {
//                	trans = portefeuille.pop();
//                	trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
//                	trans.setEndRate(modelregel.getKoers());
//                  transactions.add(trans);
//              }
                continue;
            }
//            if(bought && modelregel.getStatus() == DagkoersStatus.POS_RSI_LARGE && !isPlus(modelregel) && (trans2 != null))
//            {
//                // verkopen op groen rondje
//                trans.setEndDate(Integer.parseInt(modelregel.getDatum()));
//                trans.setEndRate(modelregel.getKoers());
//                transactions.add(trans);
//                if (trans2 != null) {
//                	trans2.setEndDate(Integer.parseInt(modelregel.getDatum()));
//                	trans2.setEndRate(modelregel.getKoers());
//                    transactions.add(trans2);
//                    trans2 = null;
//                }
//                bought = false;
//                continue;
//
//
//            }
            
        }
		
	}

}
