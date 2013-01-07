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
import org.rients.com.model.ImageResponse;
import org.rients.com.pfweb.services.modelfunctions.ModelFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rients.trading.download.model.Dagkoers;
import rients.trading.download.model.DagkoersStatus;
import rients.trading.download.model.Levels;
import rients.trading.download.model.ModelInfo;
import rients.trading.download.model.Modelregel;

@Service
public class PFGenerator {

    private boolean saveImage;
    Levels levels = new Levels();
    
    @Autowired
    HandlePF handlePF;
    
    @Autowired
    HandleFundData fundData;

    public ImageResponse getImage(String dir, String fundName, int turningPoint, float stepSize, int maxColumns) {

        if (!dir.contains("intraday")) {
            levels.createExpLevelArray(stepSize, 0.01F);
        } else {
            levels.createExpLevelArray(stepSize, 20F);
        }
        
        String dirFull = Constants.KOERSENDIR + dir + Constants.SEP;
        if (dir.equals("intraday")) {
            dirFull = Constants.INTRADAY_KOERSENDIR;
        }
        fundData.setNumberOfDays(Constants.NUMBEROFDAYSTOPRINT);
        List<Dagkoers> rates = fundData.getFundRates(fundName, dirFull);

        ArrayList<Modelregel> PFData = handlePF.createPFData(rates, fundName, dirFull, turningPoint, stepSize);
        ModelFunctions mf = new ModelFunctions(fundName);
        mf.setPFData(PFData);
        mf.handlePFRules(turningPoint, stepSize);

        String imageFile = Constants.IMAGESDIR + fundName + "_" + turningPoint + "_" + stepSize + Constants.PNG;

        removeColumns(PFData, maxColumns);
        
        ModelInfo modelInfo = calculateModelInfo(PFData);

        ImageResponse imageResponse = getImage(PFData, modelInfo, stepSize);

        if (saveImage) {
            saveImage(imageFile, imageResponse.getBuffer());
        }
        return imageResponse;

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

    private ImageResponse getImage(ArrayList<Modelregel> PFData, ModelInfo modelInfo, float stepSize) {
        ImageResponse imageResponse = new ImageResponse();
        Modelregel FirstPFRegel = (Modelregel) PFData.get(modelInfo.getFirstModelRule());
        imageResponse.setFirstDate(FirstPFRegel.getDatum());
        int thisNumberOfColumns = modelInfo.getMaxColumnNumber();

        int header = 8;
        int cellSize = 10;

        int width = (6 + thisNumberOfColumns) * cellSize;
        int height = ((header + modelInfo.getHighestModelValue()) - modelInfo.getLowestModelValue()) * cellSize;
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", 0, cellSize));

        int yAxWidth = getStringWidth(g, levels.lookupRate(modelInfo.getHighestModelValue()));
        for (int i = modelInfo.getFirstModelRule(); i <= PFData.size() - 1; i++) {
            Modelregel PFRegel = (Modelregel) PFData.get(i);
            int x_pos = (yAxWidth + cellSize * (PFRegel.getKolomnr()));
            int y_pos = 30 + (cellSize * (modelInfo.getHighestModelValue() - PFRegel.getRijnr()));
            //if (PFRegel.isStijger()) {
                g.setColor(Color.BLACK);
            //} //else {
                //g.setColor(Color.RED);
            //}
            if (PFRegel.getStatus() == DagkoersStatus.BIGMOVER_UP) {
                g.setColor(Color.GREEN);
            }
            if (PFRegel.getStatus() == DagkoersStatus.BIGMOVER_DOWN) {
                g.setColor(Color.RED);
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
        int k1 = 30;
        int l1 = 0;
        g.setColor(Color.blue);
        for (int levelValue = modelInfo.getHighestModelValue(); levelValue >= modelInfo.getLowestModelValue(); levelValue--) {
            String nivoValue = levels.lookupRate(levelValue);
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
        if (maxColumns > 0) {
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
