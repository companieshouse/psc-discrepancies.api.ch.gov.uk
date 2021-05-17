package uk.gov.ch.pscdiscrepanciesapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.beans.PersistenceDelegate;

import static uk.gov.companieshouse.api.util.security.Permission.Key.USER_PSC_DISCREPANCY_REPORT;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    @Bean
    public CRUDAuthenticationInterceptor crudAuthenticationInterceptor(){
        return new CRUDAuthenticationInterceptor(USER_PSC_DISCREPANCY_REPORT);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(crudAuthenticationInterceptor());

    }
}
