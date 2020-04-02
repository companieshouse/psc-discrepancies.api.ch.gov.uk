package uk.gov.ch.pscdiscrepanciesapi.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@RestController
@RequestMapping("/psc-discrepancy-reports/{discrepancy-report-id}/discrepancies")
public class PscDiscrepancyController {

    private final PscDiscrepancyService pscDiscrepancyService;

    private final PluggableResponseEntityFactory responseFactory;

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    @Autowired
    public PscDiscrepancyController(PluggableResponseEntityFactory responseFactory,
            PscDiscrepancyService pscDiscrepancyService) {
        this.responseFactory = responseFactory;
        this.pscDiscrepancyService = pscDiscrepancyService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ChResponseBody<PscDiscrepancy>> createPscDiscrepancy(
            @PathVariable("discrepancy-report-id") String pscDiscrepancyReportId,
            @Valid @RequestBody PscDiscrepancy pscDiscrepancy, HttpServletRequest request) {

        ResponseEntity<ChResponseBody<PscDiscrepancy>> pscDiscrepancytoReturn;
        try {
            LOG.infoContext(pscDiscrepancyReportId, "Create a discrepancy for a PSC discrepancy report",
                    pscDiscrepancyService.createPscDiscrepancyDebugMap(pscDiscrepancyReportId, pscDiscrepancy));
            ServiceResult<PscDiscrepancy> pscDiscrepancyResult = pscDiscrepancyService
                    .createPscDiscrepancy(pscDiscrepancy, pscDiscrepancyReportId, request);
            pscDiscrepancytoReturn = responseFactory.createResponse(pscDiscrepancyResult);
        } catch (ServiceException e) {
            final Map<String, Object> debugMap = pscDiscrepancyService
                    .createPscDiscrepancyDebugMap(pscDiscrepancyReportId, pscDiscrepancy);
            LOG.errorRequest(request, e, debugMap);
            // log wrapped cause exception too, as there is a bug in logger that cannot
            // extract
            // causes
            LOG.errorRequest(request, (Exception) e.getCause(), debugMap);
            pscDiscrepancytoReturn = responseFactory.createEmptyInternalServerError();
        }

        return pscDiscrepancytoReturn;
    }

    @RequestMapping(value= {"/{discrepancy-id}"}, method = RequestMethod.GET)
    public ResponseEntity<ChResponseBody<PscDiscrepancy>> getDiscrepancy(
            @PathVariable("discrepancy-report-id") String pscDiscrepancyReportId,
            @PathVariable("discrepancy-id") String pscDiscrepancyId, HttpServletRequest request) {
        ResponseEntity<ChResponseBody<PscDiscrepancy>> pscDiscrepancyToReturn;
        Map<String, Object> debugMap = 
                pscDiscrepancyService.createDebugMapWithoutDiscrepancyObject(pscDiscrepancyReportId, pscDiscrepancyId);
        try {
            LOG.info("Retrieving discrepancy for discrepancy report", debugMap);
            ServiceResult<PscDiscrepancy> pscDiscrepancyResult = 
                    pscDiscrepancyService.getDiscrepancy(pscDiscrepancyReportId, pscDiscrepancyId, request);
            pscDiscrepancyToReturn = responseFactory.createResponse(pscDiscrepancyResult);
        } catch (ServiceException e) {
            LOG.error("Error retrieving discrepancy for report", debugMap);
            LOG.errorRequest(request, (Exception) e.getCause(), debugMap);
            pscDiscrepancyToReturn = responseFactory.createEmptyInternalServerError();
        }
        return pscDiscrepancyToReturn;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> getDiscrepancies(
            @PathVariable("discrepancy-report-id") String pscDiscrepancyReportId, HttpServletRequest request) {
        ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> pscDiscrepanciesToReturn;
        Map<String, Object> debugMap = pscDiscrepancyService.createDebugMapWithoutDiscrepancyObject(pscDiscrepancyReportId,null);
        try {
            LOG.info("Retrieving all discrepancies for discrepancy report", debugMap);
            ServiceResult<List<PscDiscrepancy>> pscDiscrepancyList = pscDiscrepancyService
                    .getDiscrepancies(pscDiscrepancyReportId, request);
            pscDiscrepanciesToReturn = responseFactory.createResponse(pscDiscrepancyList);
        } catch (ServiceException se) {
            LOG.error("Error retrieving all discrepancies for report", debugMap);
            LOG.errorRequest(request, (Exception) se.getCause(), debugMap);
            pscDiscrepanciesToReturn = responseFactory.createEmptyInternalServerError();
        }
        return pscDiscrepanciesToReturn;
    }
}
