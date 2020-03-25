package uk.gov.ch.pscdiscrepanciesapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.MongoException;

import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyData;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyRepository;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyServiceUnitTest {

    private static final String REPORT_ID = "reportId";
    private static final String DISCREPANCY_ID = "discrepancyId";
    private static final String DETAILS_DATA = "some details";
    private static final String SELF_LINK = "/parent/" + REPORT_ID + "/self/" + DISCREPANCY_ID;
    private static final String PARENT_LINK = "/parent/" + REPORT_ID;
    private static final String DISCREPANCY_DETAILS = "details";

    private PscDiscrepancy pscDiscrepancy;
    private PscDiscrepancyEntity pscDiscrepancyEntity;

    @Mock
    private PscDiscrepancyMapper mockDiscrepancyMapper;

    @Mock
    private PscDiscrepancyRepository mockDiscrepancyRepo;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private LinkFactory linkFactory;

    @InjectMocks
    private PscDiscrepancyService pscDiscrepancyService;

    @BeforeEach
    void setUp() {
        pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancyEntity = new PscDiscrepancyEntity();
        
//        pscDiscrepancy.setDetails(DETAILS_DATA);
//        PscDiscrepancyData pscDiscrepancyData = new PscDiscrepancyData();
//        pscDiscrepancyData.setDetails(DETAILS_DATA);
//        pscDiscrepancyEntity.setData(pscDiscrepancyData);
//
//        when(linkFactory.createLinkPscDiscrepancy(anyString(), anyString())).thenReturn(SELF_LINK);
//        when(linkFactory.createLinkPscDiscrepancyReport(REPORT_ID)).thenReturn(PARENT_LINK);
    }

    @Test
    @DisplayName("Test createPscDiscrepancy is successful")
    void createPscDiscrepancySuccessful() throws ServiceException {
        
        pscDiscrepancy.setDetails(DETAILS_DATA);
        PscDiscrepancyData pscDiscrepancyData = new PscDiscrepancyData();
        pscDiscrepancyData.setDetails(DETAILS_DATA);
        pscDiscrepancyEntity.setData(pscDiscrepancyData);

        when(linkFactory.createLinkPscDiscrepancy(anyString(), anyString())).thenReturn(SELF_LINK);
        when(linkFactory.createLinkPscDiscrepancyReport(REPORT_ID)).thenReturn(PARENT_LINK);
        		
        when(mockDiscrepancyRepo.insert(pscDiscrepancyEntity)).thenReturn(pscDiscrepancyEntity);
        when(mockDiscrepancyMapper.restToEntity(pscDiscrepancy)).thenReturn(pscDiscrepancyEntity);
        when(mockDiscrepancyMapper.entityToRest(pscDiscrepancyEntity)).thenReturn(pscDiscrepancy);

        ServiceResult<PscDiscrepancy> result =
                pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID, request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.CREATED, result.getStatus());
        assertEquals(pscDiscrepancy, result.getData());
    }

    @Test
    @DisplayName("Test createPscDiscrepancy returns an invalid ServiceResult")
    void createPscDiscrepancyReturnsInvalidServiceResult() throws ServiceException {
        
    	Errors errData = new Errors();
		Err error = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS).withError(DISCREPANCY_DETAILS + " must not be null").build();
		errData.addError(error);

        ServiceResult<PscDiscrepancy> result =
                pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID, request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test createPscDiscrepancy throws ServiceExeption")
    void createPscDiscrepancyThrowsServiceException() {
        
        pscDiscrepancy.setDetails(DETAILS_DATA);
        PscDiscrepancyData pscDiscrepancyData = new PscDiscrepancyData();
        pscDiscrepancyData.setDetails(DETAILS_DATA);
        pscDiscrepancyEntity.setData(pscDiscrepancyData);

        when(linkFactory.createLinkPscDiscrepancy(anyString(), anyString())).thenReturn(SELF_LINK);
        when(linkFactory.createLinkPscDiscrepancyReport(REPORT_ID)).thenReturn(PARENT_LINK);
        		
        when(mockDiscrepancyRepo.insert(pscDiscrepancyEntity)).thenThrow(new MongoException(""));
        when(mockDiscrepancyMapper.restToEntity(pscDiscrepancy)).thenReturn(pscDiscrepancyEntity);

	    assertThrows(ServiceException.class, () ->
	    	pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID, request));
    }
}
