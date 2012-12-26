package org.rients.com.pfweb.configuration;

import org.rients.com.indexpredictor.HighLowImageGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PFWebConfiguration {

    public @Bean
    HighLowImageGenerator highLowImageGenerator() {
        return new HighLowImageGenerator();
    }

}
