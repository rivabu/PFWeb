package org.rients.com.pfweb.services.modelfunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rients.com.constants.Constants;

import rients.trading.download.model.ClosedTransaction;
import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.DagkoersStatus;
import rients.trading.download.model.Modelregel;
import rients.trading.services.FileIOServiceImpl;

public class ModelFunctions {
    
    private String fundName;
    private ArrayList<Modelregel> PFData;
    PFRules pfRules = new PFRules();

    public ModelFunctions(String fundName) {
        this.fundName = fundName;
    }
    
    public void handlePFRules(int turningPoint, float stepSize) {
        pfRules.setDoubleTopAndBottom(PFData);
    }
    
    public void handleFindTopsAndBottoms(int turningPoint, float stepSize) {
        pfRules.setTopsAndBottoms(PFData);
    }
    
    
    public void setBestOrWorstDates(List<Dagkoers> rates, int number, boolean worst) {
        
        @SuppressWarnings("unchecked")
        ArrayList<Dagkoers> ratesTemp = (ArrayList<Dagkoers>) ((ArrayList<Dagkoers>)rates).clone();
        new Dagkoers().sort(ratesTemp, false);
        Set<String> selectedDatesUp = new HashSet<String>();
        Set<String> selectedDatesDown = new HashSet<String>();
        boolean doorgaan  = true;
        int count = 0;
        if (ratesTemp.size() > 0) {
            for (int i = 0; doorgaan; i++) {
                Dagkoers koers = ratesTemp.get(i);
                if (count < number) {
                    selectedDatesUp.add(koers.datum);
                    count ++;
                } else {
                    doorgaan = false;
                }
            }
            count = 0;
            doorgaan = true;
            for (int i = ratesTemp.size() - 1; doorgaan; i--) {
                Dagkoers koers = ratesTemp.get(i);
                if (count < number) {
                    selectedDatesDown.add(koers.datum);
                    count ++;
                } else {
                    doorgaan = false;
                }
            }
            for (int j = 0; j < rates.size(); j++) {
                Dagkoers koers = rates.get(j);
                if (selectedDatesUp.contains(koers.datum) && koers.getStatus() == DagkoersStatus.DEFAULT) {
                    koers.setStatus(DagkoersStatus.BIGMOVER_UP);
                }
                if (selectedDatesDown.contains(koers.datum) && koers.getStatus() == DagkoersStatus.DEFAULT) {
                    koers.setStatus(DagkoersStatus.BIGMOVER_DOWN);
                }
            }
        }
        updatePFWithFunctionResults(rates);
    }

    
    
    public void fillExecutedTransactions(List<Dagkoers> rates) {
        FileIOServiceImpl fileService = new FileIOServiceImpl(null, null);
        List<ClosedTransaction> transactions =  fileService.readFromTransactiesFile(Constants.REAL_TRANSACTIONDIR, "transacties" + Constants.CSV, fundName);
        ClosedTransaction trans = null;
        boolean found = false;
        int transCount = 0;
        if (transactions.size() > 0) {
            trans = getTrans(transactions, transCount);
            for (int j = 0; j < rates.size(); j++) {
                Dagkoers koers = rates.get(j);
                if (koers.getDatumInt() > trans.getStartDateInt() && koers.getDatumInt() <= trans.getEndDateInt()) {
                    koers.setStatus(DagkoersStatus.valueOf(trans.getLongShort().name().toUpperCase()));
                    found = true;
                }
                if (koers.getDatumInt() == trans.getStartDateInt() && koers.getDatumInt() == trans.getEndDateInt()) {
                    koers.setStatus(DagkoersStatus.valueOf(trans.getLongShort().name().toUpperCase()));
                    found = true;
                }
                if (koers.getDatumInt() > trans.getEndDateInt() && found) {
                    found = false;
                    transCount++;
                    trans = getTrans(transactions, transCount);
                    if (trans == null) {
                        break;
                    }
                }
            }
        }
        updatePFWithFunctionResults(rates);
    }

    private ClosedTransaction getTrans(List<ClosedTransaction> transactions, int index) {
        if (transactions.size() <= index) {
            return null;
        }
        return transactions.get(index);
    }

    
    public void updatePFWithFunctionResults(List<Dagkoers> rates) {
        for (Dagkoers koers : rates) {
            if (koers.getStatus() != DagkoersStatus.DEFAULT ) {
                fillModelRegel(koers.datum, koers.getStatus());
            }
        }
    }
    
    private void fillModelRegel(String datum, DagkoersStatus status) {
        boolean found = false;
        for (Modelregel mr : PFData) {
            if (mr.getDatum().equals(datum)) {
                mr.setStatus(status);
                found = true;
            } else {
                if (found) {
                    break;
                }
            }
        }
    }
    /**
     * @return the fundName
     */
    public String getFundName() {
        return fundName;
    }

    /**
     * @param fundName the fundName to set
     */
    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    /**
     * @return the pFData
     */
    public ArrayList<Modelregel> getPFData() {
        return PFData;
    }

    /**
     * @param pFData the pFData to set
     */
    public void setPFData(ArrayList<Modelregel> pFData) {
        PFData = pFData;
    }}
