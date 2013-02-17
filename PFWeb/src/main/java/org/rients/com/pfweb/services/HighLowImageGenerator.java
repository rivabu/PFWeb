package org.rients.com.pfweb.services;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.rients.com.constants.Constants;
import org.rients.com.indexpredictor.FundDataHolder;
import org.rients.com.indexpredictor.Matrix;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.model.ImageResponse;
import org.rients.com.model.Modelregel;
import org.rients.com.model.Transaction;
import org.rients.com.pfweb.services.modelfunctions.PFRules;
import org.rients.com.pfweb.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HighLowImageGenerator {

    private boolean saveImage = true;
    private int DAYS = 200;
    private int lookbackPeriod = 5;
    
    @Autowired
    HandleFundData fundData;
    
    @Autowired
    HandlePF handlePF;

    public ImageResponse getHighLowImage(String type, String dir) {

        List<String> files = FileUtils.getFiles(Constants.KOERSENDIR + dir, "csv", false);

        // een matrix is een array van funddataholders.
        Matrix matrix = new Matrix(files.size() + 1, getDAYS());

        for (int i = 0; i < files.size() + 1; i++) {
            FundDataHolder dataHolder;
            if (i == 0) {
                dataHolder = new FundDataHolder(Constants.AEX_INDEX, getDAYS());
                String directory = Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP;
                fundData.setNumberOfDays(DAYS);
                List<Dagkoers> aexRates = fundData.getFundRates(Constants.AEX_INDEX, directory);
                matrix.fillDates(aexRates);

            } else {
                dataHolder = new FundDataHolder(files.get(i - 1), getDAYS());
            }
            matrix.setFundData(dataHolder, i);
        }
        if (type.equals("higherlower")) {
            fillMatrixHigherLowerThanLookback(matrix, dir, files);
        }
        if (type.equals("updown")) {
            fillMatrixIsUpOrDown(matrix, dir, files);
        }
        ImageGenerator ig = new ImageGenerator();
        ImageResponse imageResponse = ig.getImage(matrix, DAYS);
        if (saveImage) {
            String imageFile = Constants.IMAGESDIR + "matrix_" + dir + Constants.PNG;
            saveImage(imageFile, imageResponse.getBuffer());
        }
        return imageResponse;
    }

    private void fillMatrixIsUpOrDown(Matrix matrix, String dir, List<String> files) {
        String directory = null;
        List<Dagkoers> rates = null;

        fundData.setNumberOfDays(DAYS + 100);
        ArrayList<Modelregel> pfData = null;
        for (int i = 0; i < files.size() + 1; i++) {
            if (i == 0) {
                directory = Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP;
                rates = fundData.getFundRates(Constants.AEX_INDEX, directory);
                pfData = handlePF
                        .createPFData(rates, Constants.AEX_INDEX, Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP, 1, 1);
            } else {
                directory = Constants.KOERSENDIR + dir + Constants.SEP;
                rates = fundData.getFundRates(files.get(i - 1), directory);
                // pfData = handlePF.createPFData(files.get(i - 1), directory,
                // 1, 2);
                // turning point = 2
                // stepsize = 1
                pfData = handlePF.createPFData(rates, files.get(i - 1), directory, 2, 1);
            }
            PFRules optimum = new PFRules();
            List<Transaction> transactions = optimum.getOptimalDecisions(pfData, null, false);

            int fillFactor = DAYS - rates.size(); // 400 - 360
            int days = Math.min(DAYS, rates.size());
            for (int j = 0; j < days; j++) {
                fillFactor = 0;
                if (transactionIsPlus(transactions, rates.get(j + fillFactor).datum) == 1) {
                    matrix.getFundData(i).addValue(rates.get(j + fillFactor).datum, 1);
                } else if (transactionIsPlus(transactions, rates.get(j + fillFactor).datum) == -1) {
                    matrix.getFundData(i).addValue(rates.get(j + fillFactor).datum, -1);
                } else {
                    if (j > 0) {
                        matrix.getFundData(i).addValue(rates.get(j).datum, matrix.getFundData(i).getValue(rates.get(j - 1).datum));
                    }
                }
            }
        }
    }

    private void fillMatrixHigherLowerThanLookback(Matrix matrix, String dir, List<String> files) {
        String directory = null;
        List<Dagkoers> rates = null;
        fundData.setNumberOfDays(DAYS + lookbackPeriod);
        for (int file = 0; file < files.size() + 1; file++) {
            if (file == 0) {
                highLowAex(matrix, file);
                continue;
            } else {
                directory = Constants.KOERSENDIR + dir + Constants.SEP;
                rates = fundData.getFundRates(files.get(file - 1), directory);
            }
            // float[] koersen = getFloatArray(rates);
            if (file == 0) {
                System.out.println(Constants.AEX_INDEX + " first date: " + rates.get(0).datum + " aantal records: " + rates.size());
            } else {
                System.out.println(files.get(file - 1) + " first date: " + rates.get(0).datum + " aantal records: " + rates.size());
            }
            int startValue = 0;
            if (rates.size() < DAYS + lookbackPeriod && rates.size() > lookbackPeriod) {
                int difference = DAYS + lookbackPeriod - rates.size();
                for (int j = 0; j < difference; j++) {
                    matrix.getFundData(file).addValue("1999" + j, -2);
                }
                startValue = difference;
            }
            if (rates.size() + startValue == DAYS + lookbackPeriod) {
                int koersenCounter = 0;
                for (int j = startValue; j < DAYS; j++) {
                    if (isHighest(rates, koersenCounter, koersenCounter + lookbackPeriod, rates
                            .get(koersenCounter + lookbackPeriod).getClosekoers())) {
                        matrix.getFundData(file).addValue(rates.get(koersenCounter + lookbackPeriod).getDatum(), 1);
                    } else if (isLowest(rates, koersenCounter, koersenCounter + lookbackPeriod,
                            rates.get(koersenCounter + lookbackPeriod).getClosekoers())) {
                        matrix.getFundData(file).addValue(rates.get(koersenCounter + lookbackPeriod).getDatum(), -1);
                    } else {
                        matrix.getFundData(file).addValue(rates.get(koersenCounter + lookbackPeriod).getDatum(), 0);
                    }
                    koersenCounter++;
                }
            }
        }
    }

    private void highLowAex(Matrix matrix, int file) {
        String directory;

        directory = Constants.KOERSENDIR + Constants.INDEXDIR + Constants.SEP;
        fundData.setNumberOfDays(DAYS);
        List<Dagkoers> rates = fundData.getFundRates(Constants.AEX_INDEX, directory);
        @SuppressWarnings("unchecked")
        ArrayList<Dagkoers> ratesTemp = (ArrayList<Dagkoers>) ((ArrayList<Dagkoers>) rates).clone();
        new Dagkoers().sort(ratesTemp, false);
        Set<String> selectedDatesUp = new HashSet<String>();
        Set<String> selectedDatesDown = new HashSet<String>();
        boolean doorgaan = true;
        int count = 0;
        int aantal = 150;
        if (ratesTemp.size() > 0) {
            for (int i = 0; doorgaan; i++) {
                Dagkoers koers = ratesTemp.get(i);
                if (count < aantal) {
                    selectedDatesUp.add(koers.datum);
                    count++;
                } else {
                    doorgaan = false;
                }
            }
            count = 0;
            doorgaan = true;
            for (int i = ratesTemp.size() - 1; doorgaan; i--) {
                Dagkoers koers = ratesTemp.get(i);
                if (count < aantal) {
                    selectedDatesDown.add(koers.datum);
                    count++;
                } else {
                    doorgaan = false;
                }
            }
            for (int j = 0; j < rates.size(); j++) {
                Dagkoers koers = rates.get(j);
                if (selectedDatesUp.contains(koers.datum) && koers.getStatus() == DagkoersStatus.DEFAULT) {
                    matrix.getFundData(file).addValue(koers.getDatum(), 1);
                } else if (selectedDatesDown.contains(koers.datum) && koers.getStatus() == DagkoersStatus.DEFAULT) {
                    matrix.getFundData(file).addValue(koers.getDatum(), -1);
                } else
                    matrix.getFundData(file).addValue(koers.getDatum(), 0);

            }
        }
    }

    private int transactionIsPlus(List<Transaction> transactions, String date) {
        for (Transaction trans : transactions) {
            if ((Integer.valueOf(trans.getStartDate()) <= Integer.valueOf(date))
                    && (Integer.valueOf(trans.getEndDate()) >= Integer.valueOf(date)) && trans.getType() == Constants.SHORT) {
                return -1;
            }
            if ((Integer.valueOf(trans.getStartDate()) <= Integer.valueOf(date))
                    && (Integer.valueOf(trans.getEndDate()) >= Integer.valueOf(date)) && trans.getType() == Constants.LONG) {
                return 1;
            }
        }
        return 0;
    }

    private boolean isHighest(List<Dagkoers> rates, int from, int to, float value) {
        for (int i = from; i < to; i++) {
            if (value < rates.get(i).getClosekoers()) {
                return false;
            }
        }
        return true;
    }

    private boolean isLowest(List<Dagkoers> rates, int from, int to, float value) {
        for (int i = from; i < to; i++) {
            if (value > rates.get(i).getClosekoers()) {
                return false;
            }
        }
        return true;
    }

    private void saveImage(String imageFile, BufferedImage buffer) {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile));
            ImageIO.write(buffer, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the saveImage
     */
    public boolean isSaveImage() {
        return saveImage;
    }

    /**
     * @param saveImage
     *            the saveImage to set
     */
    public void setSaveImage(boolean saveImage) {
        this.saveImage = saveImage;
    }

    /**
     * @return the dAYS
     */
    public int getDAYS() {
        return DAYS;
    }

    /**
     * @param dAYS
     *            the dAYS to set
     */
    public void setDAYS(int dAYS) {
        DAYS = dAYS;
    }

    /**
     * @return the lookbackPeriod
     */
    public int getLookbackPeriod() {
        return lookbackPeriod;
    }

    /**
     * @param lookbackPeriod
     *            the lookbackPeriod to set
     */
    public void setLookbackPeriod(int lookbackPeriod) {
        this.lookbackPeriod = lookbackPeriod;
    }

    /**
     * @param fundData the fundData to set
     */
    public void setFundData(HandleFundData fundData) {
        this.fundData = fundData;
    }

    /**
     * @param handlePF the handlePF to set
     */
    public void setHandlePF(HandlePF handlePF) {
        this.handlePF = handlePF;
    }
}
