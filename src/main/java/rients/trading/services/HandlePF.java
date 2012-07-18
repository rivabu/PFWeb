/*
 * Created on Aug 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package rients.trading.services;

import java.util.ArrayList;
import java.util.List;

import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.Levels;
import rients.trading.download.model.Modelregel;
import rients.trading.utils.MathFunctions;

/**
 * @author Rients van Buren
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HandlePF {
	


    
    public ArrayList<Modelregel> createPFData(List<Dagkoers> rates, String fundName, String directory, int turningPoint, float stepSize) {


        Levels levels = new Levels();
        if (!directory.contains("intraday")) {
            levels.createExpLevelArray(stepSize, 0.01F);
        } else {
            levels.createExpLevelArray(stepSize, 20F);
        }
        
        Modelregel modelregel = null;
        ArrayList model = new ArrayList();
        int numberOfDays = rates.size() - 1;
        boolean flag6 = true;
        int i1 = 0, j1 = 0, columnNumber = 0, rowNumber = 0;
        String signSimple = " ", vorigeSign = "", sign = "";
        if (numberOfDays > 0) {
            Dagkoers huidigeDagKoers = null;
            int aantalDagen = 0;
            for (int dagteller = 3; dagteller <= numberOfDays; dagteller++) {
                huidigeDagKoers = (Dagkoers) rates.get(dagteller);

                float avrKoers = MathFunctions.calculateAverageRate((ArrayList) rates, dagteller);
                String dagkoersdatum = huidigeDagKoers.datum;
                aantalDagen++;
                if (avrKoers > 0.0F) {
                    int aantalstappen = 0;
                    boolean flag3 = false;
                    if (flag6) {
                        flag6 = false;
                        i1 = levels.LookupOccurenceNumber(huidigeDagKoers.closekoers);
                    } else {
                        j1 = levels.LookupOccurenceNumber(huidigeDagKoers.closekoers);
                        if (j1 > i1)
                            signSimple = "+";
                        else if (j1 < i1)
                            signSimple = "-";
                        else
                            signSimple = " ";
                    }
                    if (!signSimple.equals(" ")) {
                        if (!vorigeSign.equals(signSimple))
                            flag3 = true;
                        aantalstappen = Math.abs(j1 - i1);
                        if (aantalstappen >= turningPoint && flag3)
                            columnNumber++;
                        if (aantalstappen < turningPoint && flag3) {
                            aantalstappen = 0;
                        } else {
                            vorigeSign = signSimple;
                            rowNumber = i1;
                            i1 = j1;
                        }
                    }
                    boolean flag5 = false;
                    if (aantalstappen == 0 && modelregel != null) {
                        if (modelregel.isStijger() && new Float(modelregel.getHoogsteKoers()).floatValue() < huidigeDagKoers.closekoers) {
                            modelregel.setHoogsteKoers(huidigeDagKoers.closekoers);
                            modelregel.setHoogsteDatum(huidigeDagKoers.getDatum());
                        }
                        if (!modelregel.isStijger() && new Float(modelregel.getLaagsteKoers()).floatValue() > huidigeDagKoers.closekoers) {
                            modelregel.setLaagsteKoers(huidigeDagKoers.closekoers);
                            modelregel.setLaagsteDatum(huidigeDagKoers.getDatum());
                        }
                    }
                    for (int stapTeller = 1; stapTeller <= aantalstappen; stapTeller++) {
                        if (signSimple.equals("+"))
                            rowNumber++;
                        else
                            rowNumber--;
                        sign = signSimple;
                        if (flag3 && !flag5)
                            flag5 = true;
                        if (stapTeller == aantalstappen && sign.equals("+"))
                            sign = "x";
                        if (stapTeller == aantalstappen && sign.equals("-"))
                            sign = "o";
                        
                        modelregel = new Modelregel();
                        modelregel.setDatum(dagkoersdatum);
                        modelregel.setSign(sign);
                        modelregel.setKolomnr(columnNumber);
                        modelregel.setRijnr(rowNumber);
                        modelregel.setKoers(huidigeDagKoers.closekoers);
                        modelregel.setStatus(huidigeDagKoers.getStatus());
                        if (stapTeller == aantalstappen) {
                            modelregel.setAantalDagen(aantalDagen);
                            aantalDagen = 0;
                        } else
                            modelregel.setAantalDagen(0);
                        if (modelregel.isStijger()) {
                            modelregel.setHoogsteKoers(huidigeDagKoers.closekoers);
                            modelregel.setHoogsteDatum(huidigeDagKoers.getDatum());
                        } else {
                            modelregel.setLaagsteKoers(huidigeDagKoers.closekoers);
                            modelregel.setLaagsteDatum(huidigeDagKoers.getDatum());
                        }
                        // add the line to the model
                        model.add(modelregel);
                    }
                }

            }
        }
        return model;
    }


}
