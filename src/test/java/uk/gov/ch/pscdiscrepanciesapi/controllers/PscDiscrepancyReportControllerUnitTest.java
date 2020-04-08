package uk.gov.ch.pscdiscrepanciesapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ch.pscdiscrepanciesapi.common.ResponseEntityFactoriesConfig;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyReportService;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.links.CoreLinkKeys;
import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportControllerUnitTest {

    private static final String REPORT_ID = "reportId";
    private static final String SELF_LINK = "/psc-discrepancy-reports/123";
    private static final String OBLIGED_ENTITY_EMAIL = "Obliged Entity Email";

    private PscDiscrepancyReport pscDiscrepancyReport;

    @Mock
    private PscDiscrepancyReportService mockReportService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PscDiscrepancyReportController pscDiscrepancyReportController;

    @BeforeEach
    void setUp() {
        pscDiscrepancyReport = new PscDiscrepancyReport();
        PluggableResponseEntityFactory responseFactory = new ResponseEntityFactoriesConfig().createResponseFactory();
        pscDiscrepancyReportController = new PscDiscrepancyReportController(responseFactory, mockReportService);
    }

    @Test
    @DisplayName("Test getPscDiscrepancyReport by id is successful")
    void getPscDiscrepancyReportSuccessful() {
        when(mockReportService.findPscDiscrepancyReportById(REPORT_ID)).thenReturn(pscDiscrepancyReport);
        ResponseEntity<PscDiscrepancyReport> response = pscDiscrepancyReportController.get(REPORT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscDiscrepancyReport, response.getBody());
    }

    @Test
    @DisplayName("Test getPscDiscrepancyReport by id is unsuccessful")
    void getPscDiscrepancyReportUnsuccessful() {
        when(mockReportService.findPscDiscrepancyReportById(REPORT_ID)).thenReturn(null);
        ResponseEntity<PscDiscrepancyReport> response = pscDiscrepancyReportController.get(REPORT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport returns a valid ServiceResult then a Created response is returned with a SuccessBody.")
    void createPscDiscrepancyReportSuccessful() throws ServiceException {
        
        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, SELF_LINK);
        pscDiscrepancyReport.setLinks(links);
        ServiceResult<PscDiscrepancyReport> serviceResult = ServiceResult.created(pscDiscrepancyReport);

        when(mockReportService.createPscDiscrepancyReport(any(PscDiscrepancyReport.class),
                any(HttpServletRequest.class))).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                pscDiscrepancyReportController.createPscDiscrepancyReport(pscDiscrepancyReport,
                        request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pscDiscrepancyReport, response.getBody().getSuccessBody());
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport returns an invalid ServiceResult then a Bad Request response is returned with an ErrorBody.")
    void createPscDiscrepancyReportReturnsBadRequestOnInvalidServiceResult() throws ServiceException {

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " must not be null").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancyReport> serviceResult = ServiceResult.invalid(errData);

        when(mockReportService.createPscDiscrepancyReport(any(PscDiscrepancyReport.class),
                any(HttpServletRequest.class))).thenReturn(serviceResult);

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                pscDiscrepancyReportController.createPscDiscrepancyReport(pscDiscrepancyReport,
                        request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorBody().hasErrors());
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport throws a ServiceException then an Internal Server Error response is returned.")
    void createPscDiscrepancyThrowsServiceException() throws ServiceException {

        doThrow(ServiceException.class).when(mockReportService).createPscDiscrepancyReport(
                any(PscDiscrepancyReport.class), any(HttpServletRequest.class));

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                pscDiscrepancyReportController.createPscDiscrepancyReport(pscDiscrepancyReport,
                        request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When updatePscDiscrepancyReport returns a valid ServiceResult then a updated response is returned with a SuccessBody.")
    void updatePscDiscrepancyReportSuccessful() throws ServiceException {

        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, SELF_LINK);
        pscDiscrepancyReport.setLinks(links);
        ServiceResult<PscDiscrepancyReport> serviceResult =
                        ServiceResult.updated(pscDiscrepancyReport);

        when(mockReportService.updatePscDiscrepancyReport(any(String.class),
                        any(PscDiscrepancyReport.class), any(HttpServletRequest.class)))
                                        .thenReturn(serviceResult);

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                        pscDiscrepancyReportController.updatePscDiscrepancyReport(REPORT_ID,
                                        pscDiscrepancyReport, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscDiscrepancyReport, response.getBody().getSuccessBody());
    }

    @Test
    @DisplayName("When updatePscDiscrepancyReport returns an invalid ServiceResult then a Bad Request response is returned with an ErrorBody.")
    void updatePscDiscrepancyReportReturnsBadRequestOnInvalidServiceResult()
                    throws ServiceException {

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                        .withError(OBLIGED_ENTITY_EMAIL + " must not be null").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancyReport> serviceResult = ServiceResult.invalid(errData);

        when(mockReportService.updatePscDiscrepancyReport(any(String.class),
                        any(PscDiscrepancyReport.class), any(HttpServletRequest.class)))
                                        .thenReturn(serviceResult);

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                        pscDiscrepancyReportController.updatePscDiscrepancyReport(REPORT_ID,
                                        pscDiscrepancyReport, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorBody().hasErrors());
    }

    @Test
    @DisplayName("When updatePscDiscrepancyReport throws a ServiceException then an Internal Server Error response is returned.")
    void updatePscDiscrepancyThrowsServiceException() throws ServiceException {

        doThrow(ServiceException.class).when(mockReportService).updatePscDiscrepancyReport(
                        any(String.class), any(PscDiscrepancyReport.class),
                        any(HttpServletRequest.class));

        ResponseEntity<ChResponseBody<PscDiscrepancyReport>> response =
                        pscDiscrepancyReportController.updatePscDiscrepancyReport(REPORT_ID,
                                        pscDiscrepancyReport, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
