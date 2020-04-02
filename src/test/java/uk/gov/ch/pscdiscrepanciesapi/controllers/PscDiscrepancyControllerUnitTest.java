package uk.gov.ch.pscdiscrepanciesapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.ch.pscdiscrepanciesapi.common.ResponseEntityFactoriesConfig;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyService;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.links.CoreLinkKeys;
import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;
import uk.gov.companieshouse.service.rest.response.ChResponseBody;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyControllerUnitTest {

    private static final String REPORT_ID = "reportId";
    private static final String DISCREPANCY_DETAILS = "details";
    private static final String DISCREPANCY_REPORT_ID = "123";
    private static final String DISCREPANCY_DETAILS_ID = "456";
    private static final String REPORT_SELF_LINK = "/psc-discrepancy-reports/" + DISCREPANCY_REPORT_ID;
    private static final String SELF_LINK = REPORT_SELF_LINK + "/discrepancies/" + DISCREPANCY_DETAILS_ID;
    private static final String MUST_NOT_BE_NULL = " must not be null";

    private PscDiscrepancy pscDiscrepancy;

    @Mock
    private PscDiscrepancyService pscDiscrepancyService;

    @Mock
    private HttpServletRequest request;

    @Mock
    ResponseEntity<ChResponseBody<PscDiscrepancy>> pscDiscrepancytoReturn;

    private PscDiscrepancyController pscDiscrepancyController;

    @BeforeEach
    void setUp() {
        pscDiscrepancy = new PscDiscrepancy();
        PluggableResponseEntityFactory responseFactory = new ResponseEntityFactoriesConfig().createResponseFactory();
        pscDiscrepancyController = new PscDiscrepancyController(responseFactory, pscDiscrepancyService);
    }

    @Test
    @DisplayName("When createPscDiscrepancy returns an valid ServiceResult then a Created response is returned with a SuccessBody.")
    void createPscDiscrepancySuccessful() throws ServiceException {

        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, SELF_LINK);
        pscDiscrepancy.setLinks(links);
        ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.created(pscDiscrepancy);

        when(pscDiscrepancyService.createPscDiscrepancy(any(PscDiscrepancy.class), anyString(),
                any(HttpServletRequest.class))).thenReturn(serviceResult);

        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController
                .createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pscDiscrepancy, response.getBody().getSuccessBody());
    }

    @Test
    @DisplayName("When createPscDiscrepancy returns an invalid ServiceResult then a Bad Request response is returned with an ErrorBody.")
    void createPscDiscrepancyReturnsValidationServiceResult() throws ServiceException {

        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, SELF_LINK);
        pscDiscrepancy.setLinks(links);
        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS)
                .withError(DISCREPANCY_DETAILS + " must not be null").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.invalid(errData);

        when(pscDiscrepancyService.createPscDiscrepancy(any(PscDiscrepancy.class), anyString(),
                any(HttpServletRequest.class))).thenReturn(serviceResult);

        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController
                .createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorBody().hasErrors());
    }

    @Test
    @DisplayName("When createPscDiscrepancy throws a ServiceException then an Internal Server Error response is returned.")
    void createPscDiscrepancyThrowsServiceException() throws ServiceException {

        doThrow(ServiceException.class).when(pscDiscrepancyService).createPscDiscrepancy(any(PscDiscrepancy.class),
                anyString(), any(HttpServletRequest.class));

        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController
                .createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When getDiscrepancy successfully returns a discrepancy")
    void getDiscrepancySuccessful() throws ServiceException {
        pscDiscrepancy = populatePscDiscrepancy();
        ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.found(pscDiscrepancy);
        when(pscDiscrepancyService.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request)).thenReturn(serviceResult);
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = 
                pscDiscrepancyController.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscDiscrepancy, response.getBody().getSuccessBody());
        assertEquals(pscDiscrepancy.getDetails(), response.getBody().getSuccessBody().getDetails());
        assertEquals(pscDiscrepancy.getEtag(), response.getBody().getSuccessBody().getEtag());
        assertEquals(pscDiscrepancy.getKind(), response.getBody().getSuccessBody().getKind());
        assertEquals(pscDiscrepancy.getLinks(), response.getBody().getSuccessBody().getLinks());
    }
    
    @Test
    @DisplayName("When getDiscrepancy cannot find discrepancy returns Not Found and no response body")
    void getDiscrepancyUnsuccessful() throws ServiceException {
        ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.notFound();
        when(pscDiscrepancyService.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request)).thenReturn(serviceResult);
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = 
                pscDiscrepancyController.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When getDiscrepancy returns an invalid ServiceResult returns Bad Request response along with an error body")
    void getDiscrepancyReturnsValidationServiceResult() throws ServiceException {
        Errors errData = createErrors(DISCREPANCY_DETAILS_ID, MUST_NOT_BE_NULL);
        ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.invalid(errData);
        
        when(pscDiscrepancyService.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request)).thenReturn(serviceResult);
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = 
                pscDiscrepancyController.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorBody().hasErrors());
    }

    @Test
    @DisplayName("When getDiscrepancy throws a ServiceException then an Internal Server Error response is returned")
    void getDiscrepancyThrowsServiceException() throws ServiceException {
        when(pscDiscrepancyService.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request)).thenThrow(new ServiceException(""));
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = 
                pscDiscrepancyController.getDiscrepancy(DISCREPANCY_REPORT_ID, DISCREPANCY_DETAILS_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When getDiscrepancies successfully returns a service response of a list of discrepancies")
    void getDiscrepanciesSuccessful() throws ServiceException {
        List<PscDiscrepancy> discrepancies = new ArrayList<>();
        pscDiscrepancy = populatePscDiscrepancy();
        discrepancies.add(pscDiscrepancy);
        ServiceResult<List<PscDiscrepancy>> serviceResult = ServiceResult.found(discrepancies);
        
        when(pscDiscrepancyService.getDiscrepancies(DISCREPANCY_REPORT_ID, request)).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> response = 
                pscDiscrepancyController.getDiscrepancies(DISCREPANCY_REPORT_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(discrepancies, response.getBody().getSuccessBody());
    }
    
    @Test
    @DisplayName("When getDiscrepancies is unsuccessful returns a service response with not found status")
    void getDiscrepanciesUnsuccessful() throws ServiceException {
        ServiceResult<List<PscDiscrepancy>> serviceResult = ServiceResult.notFound();
        when(pscDiscrepancyService.getDiscrepancies(DISCREPANCY_REPORT_ID, request)).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> response = 
                pscDiscrepancyController.getDiscrepancies(DISCREPANCY_REPORT_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("When getDiscrepancies returns an invalid ServiceResult then a Bad Request is returned with an error body")
    void getDiscrepanciesReturnValidationServiceResult() throws ServiceException {
        Errors errData = createErrors(DISCREPANCY_DETAILS_ID, MUST_NOT_BE_NULL);
        ServiceResult<List<PscDiscrepancy>> serviceResult = ServiceResult.invalid(errData);
        when(pscDiscrepancyService.getDiscrepancies(DISCREPANCY_REPORT_ID, request)).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> response = 
                pscDiscrepancyController.getDiscrepancies(DISCREPANCY_REPORT_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getErrorBody().hasErrors());
    }

    @Test
    @DisplayName("When getDiscrepancies throws a ServiceException then an Internal Server Error response is returned")
    void getDiscrepanciesThrowsServiceException() throws ServiceException {
        when(pscDiscrepancyService.getDiscrepancies(DISCREPANCY_REPORT_ID, request)).thenThrow(new ServiceException(""));
        ResponseEntity<ChResponseBody<List<PscDiscrepancy>>> response = 
                pscDiscrepancyController.getDiscrepancies(DISCREPANCY_REPORT_ID, request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
    
    private PscDiscrepancy populatePscDiscrepancy() {
        pscDiscrepancy.setDetails(DISCREPANCY_DETAILS);
        pscDiscrepancy.setEtag("etag");
        pscDiscrepancy.setKind("kind");
        Links links = new Links();
        Map<String, String> map = new HashMap<>();
        map.put("self", SELF_LINK);
        map.put("psc-discrepancy-report", REPORT_SELF_LINK);
        links.setLinks(map);
        pscDiscrepancy.setLinks(links);
        return pscDiscrepancy;
    }
    
    private Errors createErrors(String field, String message) {
        Errors errors = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(field).withError(field + message).build();
        errors.addError(error);
        return errors;
    }
}
