package org.rients.com.pfweb.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.rients.com.constants.Constants;
import org.rients.com.model.Dagkoers;
import org.rients.com.model.DagkoersStatus;
import org.rients.com.model.ImageResponse;
import org.rients.com.model.Levels;
import org.rients.com.model.ModelInfo;
import org.rients.com.model.Modelregel;
import org.rients.com.model.Transaction;
import org.rients.com.pfweb.services.modelfunctions.ModelFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PFGenerator {

    private boolean saveImage;
    
    
    @Autowired
    HandlePF handlePF;
    
    @Autowired
    HandleFundData fundData;

    public ImageResponse getImage(String type, Transaction trans, String dir, String fundName, String graphType, int turningPoint, float stepSize, int maxColumns, int numberOfDays) {
        Levels levels = Levels.getInstance();

        if (!dir.contains("intraday")) {
            if (graphType.equals("EXP")) {
                levels.createExpLevelArray(stepSize, 0.01F);
            } else {
                levels.createNormalLevelArray(stepSize, 0);
            }
        } 
       
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        if (dir.equals("intraday")) {
            dirFull = Constants.INTRADAY_KOERSENDIR;
        }
        List<Dagkoers> rates = null;
        if (numberOfDays == -1) {
        	numberOfDays = Constants.NUMBEROFDAYSTOPRINT;
        }
        if (type.equals("default")) {
            fundData.setNumberOfDays(numberOfDays);
            rates = fundData.getFundRates(fundName, dirFull);
        }
        if (type.equals("trans")) {
            fundData.setNumberOfDays(numberOfDays / 4);
            rates = fundData.getFundRates(fundName, dirFull, trans.getStartDate(), trans.getEndDate());
        }

        ArrayList<Modelregel> PFData = handlePF.createPFData(rates, fundName, graphType, dirFull, turningPoint, stepSize);
        ModelFunctions mf = new ModelFunctions(fundName);
        mf.setPFData(PFData);
        mf.setRates(rates);
        if (type.equals("default")) {
            mf.handlePFRules(turningPoint, stepSize);
        }
        if (type.equals("trans")) {
            mf.handlePFRules(turningPoint, stepSize, trans);
        }

        String imageFile = Constants.IMAGESDIR + fundName + "_" + turningPoint + "_" + stepSize + Constants.PNG;

        removeColumns(PFData, maxColumns);
        ImageResponse imageResponse = null;
        ModelInfo modelInfo = calculateModelInfo(PFData);
        try {
            imageResponse = getImage(PFData, modelInfo, stepSize);
        } catch (Exception e) {
            System.out.println("error creating pfimage for file: " + fundName);
            e.printStackTrace();
        }
        if (saveImage) {
            saveImage(imageFile, imageResponse.getBuffer());
        }
        return imageResponse;

    }

    private void saveImage(String imageFile, BufferedImage buffer)  {
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imageFile));
            ImageIO.write(buffer, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ImageResponse getImage(ArrayList<Modelregel> PFData, ModelInfo modelInfo, float stepSize) throws Exception {
        Levels levels = Levels.getInstance();
        ImageResponse imageResponse = new ImageResponse();
        Modelregel FirstPFRegel = (Modelregel) PFData.get(modelInfo.getFirstModelRule());
        imageResponse.setFirstDate(FirstPFRegel.getDatum());
        int thisNumberOfColumns = modelInfo.getMaxColumnNumber();

        int header = 1;
        int cellSize = 10;

        int width = (6 + thisNumberOfColumns) * cellSize;
        int height = ((header + modelInfo.getHighestModelValue()) - modelInfo.getLowestModelValue()) * cellSize;
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", 0, cellSize));

        int yAxWidth = getStringWidth(g, levels.lookupRate(stepSize, modelInfo.getHighestModelValue())) + 4;
        for (int i = modelInfo.getFirstModelRule(); i <= PFData.size() - 1; i++) {
            Modelregel PFRegel = (Modelregel) PFData.get(i);
            int x_pos = (yAxWidth + cellSize * (PFRegel.getKolomnr()));
            int y_pos = 10 + (cellSize * (modelInfo.getHighestModelValue() - PFRegel.getRijnr()));
            //if (PFRegel.isStijger()) {
                g.setColor(Color.BLACK);
            //} //else {
                //g.setColor(Color.RED);
            //}
            if (PFRegel.getStatus() == DagkoersStatus.POS_RSI) {
                g.setColor(Color.LIGHT_GRAY);
            }
            if (PFRegel.getStatus() == DagkoersStatus.NEG_RSI) {
                g.setColor(Color.LIGHT_GRAY);
            }    
            if (PFRegel.getStatus() == DagkoersStatus.POS_RSI_LARGE) {
                g.setColor(Color.GREEN);
            }
            if (PFRegel.getStatus() == DagkoersStatus.NEG_RSI_LARGE) {
                g.setColor(Color.RED);
            }    
            if (PFRegel.getStatus() == DagkoersStatus.BIGMOVER_UP) {
                g.setColor(Color.magenta);
            }
            if (PFRegel.getStatus() == DagkoersStatus.BIGMOVER_DOWN) {
                g.setColor(Color.cyan);
            }
            if (PFRegel.getStatus() == DagkoersStatus.LONG) {
                g.setColor(Color.GREEN);
            }
            if (PFRegel.getStatus() == DagkoersStatus.SHORT) {
                g.setColor(Color.RED);
            }
            if (PFRegel.getStatus() == DagkoersStatus.LATESTDAY) {
                g.setColor(Color.blue);
            }
            if (PFRegel.getStatus() == DagkoersStatus.DOUBLE_TOP) {
                g.setColor(Color.GREEN);
            }
            if (PFRegel.getStatus() == DagkoersStatus.DOUBLE_BOTTOM) {
                g.setColor(Color.RED);
            }
            g.drawString(PFRegel.getSign(), x_pos, y_pos);

        }
        int k1 = 10;
        int l1 = 0;
        g.setColor(Color.blue);
        for (int levelValue = modelInfo.getHighestModelValue(); levelValue >= modelInfo.getLowestModelValue(); levelValue--) {
            String nivoValue = levels.lookupRate(stepSize, levelValue);
            g.drawString(nivoValue, 0, k1);

            if (l1 == 5) {
                l1 = 0;
                g.setColor(Color.lightGray);
                g.drawLine(0, k1, cellSize * thisNumberOfColumns + 40, k1);
                g.setColor(Color.blue);
            }
            k1 += cellSize;
            l1++;
        }
        imageResponse.setBuffer(buffer);

        return imageResponse;
    }

    private int getStringWidth(Graphics g, String string) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.stringWidth(string);
    }

    private ModelInfo calculateModelInfo(ArrayList<Modelregel> data) {
        ModelInfo modelInfo = new ModelInfo();
        int size = data.size() - 1;
        boolean first = true;
        for (int i = size; i >= 0; i--) {
            Modelregel regel = (Modelregel) data.get(i);
            if (first) {
                first = false;
                modelInfo.setLastDate(regel.getDatum());
                modelInfo.setMaxColumnNumber(regel.getKolomnr());
                modelInfo.setHighestModelValue(regel.getRijnr());
                modelInfo.setLowestModelValue(regel.getRijnr());
            }
            if (regel.getRijnr() > modelInfo.getHighestModelValue())
                modelInfo.setHighestModelValue(regel.getRijnr());
            if (regel.getRijnr() < modelInfo.getLowestModelValue())
                modelInfo.setLowestModelValue(regel.getRijnr());
        }
        return modelInfo;
    }

    private void removeColumns(ArrayList<Modelregel> data, int maxColumns) {
        if ((maxColumns > 0) && (data.size() > 1)) {
            int maxColumnInData = ((Modelregel) data.get(data.size() - 1)).getKolomnr();
            if (maxColumnInData > maxColumns) {
                int firstColumnNumber = maxColumnInData - maxColumns;
                Iterator<Modelregel> iterator = data.iterator();
                while (iterator.hasNext()) {
                    Modelregel mr = iterator.next();
                    
                    if (mr.getKolomnr() < firstColumnNumber) {
                        iterator.remove();
                    } else {
                        int columnNumber = mr.getKolomnr() - firstColumnNumber;
                        mr.setKolomnr(columnNumber);
                    }
                }
                
            }
        }
    }
    public boolean isSaveImage() {
        return saveImage;
    }

    public void setSaveImage(boolean saveImage) {
        this.saveImage = saveImage;
    }

    /**
     * @param handlePF the handlePF to set
     */
    public void setHandlePF(HandlePF handlePF) {
        this.handlePF = handlePF;
    }

    /**
     * @param fundData the fundData to set
     */
    public void setFundData(HandleFundData fundData) {
        this.fundData = fundData;
    }
}
