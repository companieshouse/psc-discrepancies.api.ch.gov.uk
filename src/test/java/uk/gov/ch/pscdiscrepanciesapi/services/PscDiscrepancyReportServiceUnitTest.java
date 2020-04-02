package uk.gov.ch.pscdiscrepanciesapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Optional;
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
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyReportRepository;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportServiceUnitTest {

    private static final String REPORT_ID = "reportId";
    private static final String SELF_LINK = "/parent/" + REPORT_ID;
    private static final String OBLIGED_ENTITY_EMAIL = "Obliged Entity Email";
    private static final String VALID_EMAIL = "m@m.com";
    private static final String INVALID_EMAIL = "mm.com";

    private PscDiscrepancyReport pscDiscrepancyReport;
    private PscDiscrepancyReportEntity pscDiscrepancyReportEntity;

    @Mock
    private PscDiscrepancyReportMapper mockReportMapper;

    @Mock
    private PscDiscrepancyReportRepository mockReportRepo;

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private LinkFactory linkFactory;

    @InjectMocks
    private PscDiscrepancyReportService pscDiscrepancyReportService;

    @BeforeEach
    void setUp() {
        pscDiscrepancyReport = new PscDiscrepancyReport();
        pscDiscrepancyReportEntity = new PscDiscrepancyReportEntity();
    }

    @Test
    @DisplayName("Test findByPscDiscrepancyReportId is successful")
    void verifyFindByIdSuccessful() {
        when(mockReportRepo.findById(REPORT_ID))
                .thenReturn(Optional.of(pscDiscrepancyReportEntity));
        when(mockReportMapper.entityToRest(pscDiscrepancyReportEntity))
                .thenReturn(pscDiscrepancyReport);

        PscDiscrepancyReport result =
                pscDiscrepancyReportService.findPscDiscrepancyReportById(REPORT_ID);

        assertNotNull(result);
        assertEquals(pscDiscrepancyReport, result);
    }

    @Test
    @DisplayName("Test findByPscDiscrepancyReportId is unsuccessful")
    void verifyFindByIdUnsuccessful() {
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.empty());

        PscDiscrepancyReport result =
                pscDiscrepancyReportService.findPscDiscrepancyReportById(REPORT_ID);

        assertNull(result);
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport returns an valid ServiceResult then a Created response is returned with a SuccessBody.")
    void createPscDiscrepancyReportSuccessful() throws ServiceException {
        
        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);
        
        PscDiscrepancyReportEntityData pscDiscrepancyReportData = new PscDiscrepancyReportEntityData();
        pscDiscrepancyReportData.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReportEntity.setData(pscDiscrepancyReportData);

        when(linkFactory.createLinkPscDiscrepancyReport(anyString())).thenReturn(SELF_LINK);
                
        when(mockReportRepo.insert(pscDiscrepancyReportEntity)).thenReturn(pscDiscrepancyReportEntity);
        when(mockReportMapper.restToEntity(pscDiscrepancyReport)).thenReturn(pscDiscrepancyReportEntity);
        when(mockReportMapper.entityToRest(pscDiscrepancyReportEntity)).thenReturn(pscDiscrepancyReport);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.createPscDiscrepancyReport(pscDiscrepancyReport, request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.CREATED, result.getStatus());
        assertEquals(pscDiscrepancyReport, result.getData());
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport returns an invalid ServiceResult then email is null.")
    void createPscDiscrepancyReportReturnsInvalidServiceResultWhenEmailNull() throws ServiceException {

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " must not be null").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.createPscDiscrepancyReport(pscDiscrepancyReport, request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport returns an invalid ServiceResult then email is in invalid format.")
    void createPscDiscrepancyReportReturnsInvalidServiceResultWhenIncorrectEmailFormat() throws ServiceException {

        pscDiscrepancyReport.setObligedEntityEmail(INVALID_EMAIL);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.createPscDiscrepancyReport(pscDiscrepancyReport, request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport throws a ServiceException then an Internal Server Error response is returned.")
    void createPscDiscrepancyReportThrowsServiceException() {
        
        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);
        
        PscDiscrepancyReportEntityData pscDiscrepancyReportData = new PscDiscrepancyReportEntityData();
        pscDiscrepancyReportData.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReportEntity.setData(pscDiscrepancyReportData);

        when(linkFactory.createLinkPscDiscrepancyReport(anyString())).thenReturn(SELF_LINK);

        when(mockReportRepo.insert(pscDiscrepancyReportEntity)).thenThrow(new MongoException(""));
        when(mockReportMapper.restToEntity(pscDiscrepancyReport)).thenReturn(pscDiscrepancyReportEntity);

        assertThrows(ServiceException.class, () -> pscDiscrepancyReportService
                .createPscDiscrepancyReport(pscDiscrepancyReport, request));
    }
}
