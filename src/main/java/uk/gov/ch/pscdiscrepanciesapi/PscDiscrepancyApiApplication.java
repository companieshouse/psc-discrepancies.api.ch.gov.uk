package uk.gov.ch.pscdiscrepanciesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class PscDiscrepancyApiApplication {

    public static final String APP_NAMESPACE = "psc-discrepancy-reports";
    public static final Logger LOGGER = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    public static void main(String[] args) {
        SpringApplication.run(PscDiscrepancyApiApplication.class, args);
    }

}
