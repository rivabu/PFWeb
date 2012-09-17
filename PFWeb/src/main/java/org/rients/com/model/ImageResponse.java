package org.rients.com.model;

import java.awt.image.BufferedImage;

public class ImageResponse {

    private BufferedImage buffer;
    private byte[] content;
    private String firstDate;

    public BufferedImage getBuffer() {
        return buffer;
    }

    public void setBuffer(BufferedImage buffer) {
        this.buffer = buffer;
    }

    public String getFirstDate() {
        return firstDate;
    }

    public void setFirstDate(String firstDate) {
        this.firstDate = firstDate;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
