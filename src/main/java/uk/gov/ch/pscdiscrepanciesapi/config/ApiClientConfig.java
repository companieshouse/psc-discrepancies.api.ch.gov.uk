package uk.gov.ch.pscdiscrepanciesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Configuration
public class ApiClientConfig {

    @Bean
    ApiClient httpClient(){
        return ApiSdkManager.getSDK();
    }
}
