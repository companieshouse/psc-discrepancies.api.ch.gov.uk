package uk.gov.ch.pscdiscrepanciesapi.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@Configuration
public class ResponseEntityFactoriesConfig {
    @Bean
    public PluggableResponseEntityFactory createResponseFactory() {
        return PluggableResponseEntityFactory.buildWithStandardFactories();
    }
}
