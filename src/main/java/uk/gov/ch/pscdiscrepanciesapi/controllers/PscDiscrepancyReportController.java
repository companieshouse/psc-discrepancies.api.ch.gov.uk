package uk.gov.ch.pscdiscrepanciesapi.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyReportService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@RestController
@RequestMapping("/psc-discrepancy-reports")
public class PscDiscrepancyReportController {

    private final PluggableResponseEntityFactory responseFactory;
    
    @Autowired
    private PscDiscrepancyReportService pscDiscrepancyReportService;

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    @Autowired
    public PscDiscrepancyReportController(PluggableResponseEntityFactory responseFactory,
            PscDiscrepancyReportService pscDiscrepancyReportService) {
        this.responseFactory = responseFactory;
        this.pscDiscrepancyReportService = pscDiscrepancyReportService;
    }
    
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
    
    @PostMapping
    public ResponseEntity<ChResponseBody<PscDiscrepancyReport>> createPscDiscrepancyReport(
            @Valid @RequestBody PscDiscrepancyReport pscDiscrepancyReport,
            HttpServletRequest request) {

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> pscDiscrepancyReportToReturn;
        try {
            ServiceResult<PscDiscrepancyReport> pscDiscrepancyReportResult =
                    pscDiscrepancyReportService.createPscDiscrepancyReport(pscDiscrepancyReport,
                            request);
            pscDiscrepancyReportToReturn =
                    responseFactory.createResponse(pscDiscrepancyReportResult);
        } catch (ServiceException e) {
            pscDiscrepancyReportToReturn = responseFactory.createEmptyInternalServerError();
        }

        return pscDiscrepancyReportToReturn;
    }

    @PutMapping
    public ResponseEntity<ChResponseBody<PscDiscrepancyReport>> updatePscDiscrepancy(
                    @PathVariable("discrepancy-report-id") String pscDiscrepancyReportId,
                    @Valid @RequestBody PscDiscrepancyReport pscDiscrepancyReport, HttpServletRequest request) {

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> pscDiscrepancyReportToReturn;
        try {
            ServiceResult<PscDiscrepancyReport> pscDiscrepancyReportResult =
                            pscDiscrepancyReportService.updatePscDiscrepancyReport(pscDiscrepancyReportId, pscDiscrepancyReport,
                                            request);
            pscDiscrepancyReportToReturn =
                            responseFactory.createResponse(pscDiscrepancyReportResult);
        } catch (ServiceException e) {
            pscDiscrepancyReportToReturn = responseFactory.createEmptyInternalServerError();
        }

        return pscDiscrepancyReportToReturn;
    }
}