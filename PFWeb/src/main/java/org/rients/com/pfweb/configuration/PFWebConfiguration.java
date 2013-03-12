package org.rients.com.pfweb.configuration;

import org.rients.com.constants.Constants;
import org.rients.com.pfweb.services.FundPropertiesService;
import org.rients.com.pfweb.services.FundPropertiesServiceImpl;
import org.rients.com.pfweb.services.HandleFundData;
import org.rients.com.pfweb.services.HighLowImageGenerator;
import org.rients.com.pfweb.services.PFGenerator;
import org.rients.com.pfweb.services.RSIGenerator;
import org.rients.com.services.FileIOService;
import org.rients.com.services.FileIOServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




@Configuration
public class PFWebConfiguration {

    public @Bean
    HighLowImageGenerator highLowImageGenerator() {
        return new HighLowImageGenerator();
    }
    
    public @Bean
    FundPropertiesService fundPropertiesService() {
        return new FundPropertiesServiceImpl();
    }
    
    public @Bean
    PFGenerator pFGenerator() {
        return new PFGenerator();
    }
    
    public @Bean
    RSIGenerator rSIGenerator() {
        return new RSIGenerator();
    }
    
    public @Bean
    HandleFundData handleFundData() {
        return new HandleFundData();
    }

    public @Bean
    FileIOService fileIOService() {
        return new FileIOServiceImpl(Constants.KOERSENDIR , Constants.TEMPDIR);
    }

}
