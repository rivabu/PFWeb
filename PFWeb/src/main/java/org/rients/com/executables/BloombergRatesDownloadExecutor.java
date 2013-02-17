/*
 * Copyright Alliander 2009
 */
package org.rients.com.executables;

import java.util.Properties;

import org.rients.com.services.bloomberg.BloombergRatesDownloader;
import org.rients.com.utils.PropertiesUtils;



/**
 * Testcase for class {@link VersionController}
 */
public class BloombergRatesDownloadExecutor  {
    private transient BloombergRatesDownloader controller;

    BloombergRatesDownloadExecutor() {
        super();
        controller = new BloombergRatesDownloader();
    }

    public void download() {
        controller.downloadNow();
    }
    
    public static void main(String[] args) throws Exception {
        Properties props = PropertiesUtils.getPropertiesFromClasspath("application.properties");
        long time = Long.parseLong((String)props.getProperty("bloomberg.thread.time"));
        BloombergRatesDownloadExecutor executor = new BloombergRatesDownloadExecutor();
        while (true) {
            executor.download();
            Thread.sleep(time);
        }
    }
}
