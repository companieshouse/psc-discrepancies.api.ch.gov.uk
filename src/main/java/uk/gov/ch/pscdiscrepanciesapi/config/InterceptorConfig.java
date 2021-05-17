package uk.gov.ch.pscdiscrepanciesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;

import static uk.gov.companieshouse.api.util.security.Permission.Key.USER_PSC_DISCREPANCY_REPORT;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public CRUDAuthenticationInterceptor crudAuthenticationInterceptor(){
        return new CRUDAuthenticationInterceptor(USER_PSC_DISCREPANCY_REPORT);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(crudAuthenticationInterceptor());
    }
}
