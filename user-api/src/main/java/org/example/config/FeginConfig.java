package org.example.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeginConfig {
    @Qualifier(value = "mailgun")
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor(){
        return new BasicAuthRequestInterceptor("api", "8fe92ba07a3395b8ae9136a5484d7853-a2dd40a3-99fe5a42");
    }
}
