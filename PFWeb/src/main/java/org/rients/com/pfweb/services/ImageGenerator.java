package org.rients.com.pfweb.services;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.rients.com.matrix.dataholder.Matrix;
import org.rients.com.model.ImageResponse;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerator {

    ImageResponse getImage(Matrix matrix, int DAYS) {
        ImageResponse imageResponse = new ImageResponse();
        int maxLengthFundNames = matrix.getMaxFundnameLength() + 2;

        int cellSize = 10;

        int fundNameLength = getStringWidth(StringUtils.rightPad("", maxLengthFundNames + 1), cellSize);

        int width = fundNameLength + fundNameLength + (DAYS * cellSize);
        int lines = (matrix.getAantalColumns() / 5) + 1;
        int height = (matrix.getAantalColumns() * cellSize) + lines;
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier", 0, cellSize));

        g.setColor(Color.GRAY);
        g.drawLine(0, 0, width, 0);
        int lineCount = 1;
        Set<String> aexDates = matrix.getColumn(0).getDates();

        for (int fondsTeller = 0; fondsTeller < matrix.getAantalColumns(); fondsTeller++) {
            int x_pos = 0;
            int y_pos = ((fondsTeller + 1) * cellSize) + lineCount;
            g.setColor(Color.BLACK);
            g.drawString(formatFundName(matrix.getColumn(fondsTeller).getColumnName(), maxLengthFundNames, true), x_pos, y_pos);

            // j moet eigenlijk de datum zijn!

            Iterator<String> iterator = aexDates.iterator();
            int counter = 0;
            while (iterator.hasNext()) {
                String date = iterator.next();
                int value = (Integer) matrix.getColumn(fondsTeller).getValue(date);
                if (value == 0) {
                    g.setColor(Color.WHITE);
                }
                if (value == 1) {
                    g.setColor(Color.GREEN);
                }
                if (value == -1) {
                    g.setColor(Color.RED);
                }
                if (value == -2) {
                    g.setColor(Color.GRAY);
                }

                g.fillRect(fundNameLength + counter * cellSize, (fondsTeller * cellSize) + lineCount, cellSize, cellSize);
                counter++;
            }
            g.setColor(Color.BLACK);
            g.drawString(formatFundName(matrix.getColumn(fondsTeller).getColumnName(), maxLengthFundNames, false), fundNameLength
                    + DAYS * cellSize, ((fondsTeller + 1) * cellSize) + lineCount);
            if (fondsTeller % 5 == 4) {
                int y_pos_line = (fondsTeller * cellSize) + cellSize + lineCount;
                g.setColor(Color.GRAY);
                g.drawLine(0, y_pos_line, width, y_pos_line);
                lineCount++;
            }
        }

        imageResponse.setBuffer(buffer);

        return imageResponse;
    }

    private String formatFundName(String fundName, int maxSize, boolean semicolonRight) {
        String result = StringUtils.rightPad(fundName, maxSize - 2);
        if (semicolonRight) {
            result = result + " :";
        } else {
            result = ": " + result;
        }
        return result;
    }

    private int getStringWidth(String string, int fontSize) {
        BufferedImage buffer = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffer.createGraphics();
        g.setFont(new Font("Courier", 0, fontSize));
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.stringWidth(string);
    }

}
