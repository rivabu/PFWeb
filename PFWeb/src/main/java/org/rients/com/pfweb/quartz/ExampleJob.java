package org.rients.com.pfweb.quartz;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.rients.com.executables.BehrDownloadExecutor;
import org.rients.com.pdf.tag.PdfRendererService;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ExampleJob extends QuartzJobBean {

    static Logger LOG = Logger.getLogger("ExampleJob.class");
    private int timeout;
    
    /**
     * Setter called after the ExampleJob is instantiated
     * with the value from the JobDetailBean (5)
     */ 
    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }
    
    protected void executeInternal(JobExecutionContext ctx) throws JobExecutionException {
     // TODO Auto-generated method stub
        Date dNow = new Date( );
        SimpleDateFormat ft = 
        new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");


        
        LOG.debug("Current Date: " + ft.format(dNow));
        try {
            BehrDownloadExecutor executor = new BehrDownloadExecutor();
            executor.testDownloadFavourites();

            PdfRendererService pdfRendererService = new PdfRendererService();
            pdfRendererService.process();
            
            //new SendMail().process();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  }