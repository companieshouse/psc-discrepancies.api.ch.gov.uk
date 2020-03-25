package uk.gov.ch.pscdiscrepanciesapi.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.service.rest.response.ChResponseBody;
import uk.gov.companieshouse.service.rest.response.PluggableResponseEntityFactory;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyControllerUnitTest {

    private static final String REPORT_ID = "reportId";

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
    @DisplayName("Test createPscDiscrepancy is successful")
    void createPscDiscrepancySuccessful() throws ServiceException {
    	
        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, "/psc-discrepancy-reports/123/discrepancies/456");
        pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancy.setLinks(links);
    	ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.created(pscDiscrepancy);

        when(pscDiscrepancyService.createPscDiscrepancy(any(PscDiscrepancy.class), anyString(), any(HttpServletRequest.class))).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController.createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pscDiscrepancy, response.getBody().getSuccessBody());
    }

    @Test
    @DisplayName("Test createPscDiscrepancy is successful")
    void createPscDiscrepancyReturnsValidationServiceResult() throws ServiceException {
    	
        Links links = new Links();
        links.setLink(CoreLinkKeys.SELF, "/psc-discrepancy-reports/123/discrepancies/456");
        pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancy.setLinks(links);
    	ServiceResult<PscDiscrepancy> serviceResult = ServiceResult.created(pscDiscrepancy);

        when(pscDiscrepancyService.createPscDiscrepancy(any(PscDiscrepancy.class), anyString(), any(HttpServletRequest.class))).thenReturn(serviceResult);
        
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController.createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(pscDiscrepancy, response.getBody().getSuccessBody());
    }

    @Test
    @DisplayName("Test createPscDiscrepancy throws a ServiceException")
    void createPscDiscrepancyThrowsServiceException() throws ServiceException {

        doThrow(ServiceException.class).when(pscDiscrepancyService).createPscDiscrepancy(any(PscDiscrepancy.class), anyString(), any(HttpServletRequest.class));
        
        ResponseEntity<ChResponseBody<PscDiscrepancy>> response = pscDiscrepancyController.createPscDiscrepancy(REPORT_ID, pscDiscrepancy, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
