package uk.gov.ch.pscdiscrepanciesapi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyReportService;

@RestController
@RequestMapping("/psc-discrepancy-reports")
public class PscDiscrepancyReportController {

    @Autowired
    private PscDiscrepancyReportService pscDiscrepancyReportService;

    @GetMapping(value = "/{psc-discrepancy-report-id}")
    public ResponseEntity get(@PathVariable("psc-discrepancy-report-id") String id) {
        PscDiscrepancyReport pscDiscrepancyReport =
                pscDiscrepancyReportService.findPscDiscrepancyReportById(id);

        if(pscDiscrepancyReport == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(pscDiscrepancyReport);
    }
}
