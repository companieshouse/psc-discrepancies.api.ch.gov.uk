package uk.gov.ch.pscdiscrepanciesapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyReportService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping("/psc-discrepancy-reports")
public class PscDiscrepancyReportController {

    @Autowired
    private PscDiscrepancyReportService pscDiscrepancyReportService;

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    @GetMapping(value = "/{psc-discrepancy-report-id}")
    public ResponseEntity<PscDiscrepancyReport> get(@PathVariable("psc-discrepancy-report-id") String id) {
        PscDiscrepancyReport pscDiscrepancyReport =
                pscDiscrepancyReportService.findPscDiscrepancyReportById(id);

        if(pscDiscrepancyReport == null) {
        	LOG.info("No PSC discrepancy report found with ID " + id);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pscDiscrepancyReport);
    }
}
