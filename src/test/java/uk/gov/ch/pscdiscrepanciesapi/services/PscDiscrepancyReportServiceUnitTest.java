package uk.gov.ch.pscdiscrepanciesapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mongodb.MongoException;
import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.common.PscSubmissionSender;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ReportStatus;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyReportRepository;
import uk.gov.ch.pscdiscrepanciesapi.validation.PscDiscrepancyReportValidator;
import uk.gov.ch.pscdiscrepanciesapi.validation.PscDiscrepancyValidator;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportServiceUnitTest {
    private static final String DISCREPANCY_DETAILS = "discrepancy";
    private static final String REQUEST_ID = "requestId_1";
    private static final String REPORT_ID = "reportId";
    private static final String SELF_LINK = "/parent/" + REPORT_ID;
    private static final String OBLIGED_ENTITY_EMAIL = "Obliged Entity Email";
    private static final String OBLIGED_ENTITY_TELEPHONE_NUMBER_LOCATION =
            "obliged_entity_telephone_number";
    private static final String OBLIGED_ENTITY_CONTACT_NAME = "Obliged Entity Contact Name";
    private static final String STATUS = "Status";
    private static final String ETAG = "Etag";
    private static final String VALID_EMAIL = "m@m.com";
    private static final String INVALID_EMAIL = "mm.com";
    private static final String VALID_TELEPHONE_NUMBER = "08001234567";
    private static final String VALID_TYPE= "obliged entity type";
    private static final String ETAG_1 = "1";
    private static final String VALID_CONTACT_NAME = "valid-contact-náme";
    private static final String INVALID_CONTACT_NAME = "^invalid-contact-name^";
    private static final String NOT_EMPTY_ERROR_MESSAGE =
            " must not be empty and must not only consist of whitespace";
    private PscDiscrepancyReport pscDiscrepancyReport;
    private PscDiscrepancyReportEntity pscDiscrepancyReportEntity;

    @Mock
    private PscDiscrepancyReportMapper mockReportMapper;

    @Mock
    private PscDiscrepancyReportRepository mockReportRepo;

    @Mock
    private HttpServletRequest mockRequest;
    
    @Mock
    private PscSubmissionSender mockSender;
    
    @Mock
    private PscDiscrepancyService mockDiscrepancyService;
    
    @Mock
    private PscDiscrepancyReportValidator mockReportDiscrepancyValidator;
    
    @Mock
    private PscDiscrepancyValidator mockDiscrepancyValidator;
    
    @Mock
    private LinkFactory mockLinkFactory;

    private PscDiscrepancyReportService pscDiscrepancyReportService;

    @BeforeEach
    void setUp() {
        pscDiscrepancyReport = new PscDiscrepancyReport();
        pscDiscrepancyReportEntity = new PscDiscrepancyReportEntity();
        pscDiscrepancyReportService = new PscDiscrepancyReportService(mockReportRepo, mockReportMapper, mockSender,
                mockDiscrepancyService, mockLinkFactory, mockReportDiscrepancyValidator, mockDiscrepancyValidator);
    }

    private void setupMockRequestWithMockSession() {
        HttpSession mockSession = mock(HttpSession.class);
        when(mockRequest.getSession()).thenReturn(mockSession);
        when(mockSession.getId()).thenReturn(REQUEST_ID);
    }

    @Test
    @DisplayName("Test findByPscDiscrepancyReportId is successful")
    void verifyFindByIdSuccessful() {
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(pscDiscrepancyReportEntity));
        when(mockReportMapper.entityToRest(pscDiscrepancyReportEntity)).thenReturn(pscDiscrepancyReport);

        PscDiscrepancyReport result = pscDiscrepancyReportService.findPscDiscrepancyReportById(REPORT_ID);

        assertNotNull(result);
        assertEquals(pscDiscrepancyReport, result);
    }

    @Test
    @DisplayName("Test findByPscDiscrepancyReportId is unsuccessful")
    void verifyFindByIdUnsuccessful() {
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.empty());

        PscDiscrepancyReport result = pscDiscrepancyReportService.findPscDiscrepancyReportById(REPORT_ID);

        assertNull(result);
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport is successful, then it returns a created ServiceResult.")
    void createPscDiscrepancyReportSuccessful() throws ServiceException {

        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);

        PscDiscrepancyReportEntityData pscDiscrepancyReportData = new PscDiscrepancyReportEntityData();
        pscDiscrepancyReportData.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReportEntity.setData(pscDiscrepancyReportData);

        when(mockLinkFactory.createLinkPscDiscrepancyReport(anyString())).thenReturn(SELF_LINK);

        when(mockReportRepo.insert(pscDiscrepancyReportEntity)).thenReturn(pscDiscrepancyReportEntity);
        when(mockReportMapper.restToEntity(pscDiscrepancyReport)).thenReturn(pscDiscrepancyReportEntity);
        when(mockReportMapper.entityToRest(pscDiscrepancyReportEntity)).thenReturn(pscDiscrepancyReport);
        when(mockReportDiscrepancyValidator.validateForCreation(eq(pscDiscrepancyReport), any(Errors.class))).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService
                .createPscDiscrepancyReport(pscDiscrepancyReport, mockRequest);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.CREATED, result.getStatus());
        assertEquals(pscDiscrepancyReport, result.getData());
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport is supplied with a null email, then it returns an invalid ServiceResult.")
    void createPscDiscrepancyReportReturnsInvalidServiceResultWhenEmailNull() throws ServiceException {

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " must not be empty or null").build();
        errData.addError(error);

        when(mockReportDiscrepancyValidator.validateForCreation(eq(pscDiscrepancyReport), any(Errors.class))).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService
                .createPscDiscrepancyReport(pscDiscrepancyReport, mockRequest);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When createPscDiscrepancy is supplied with a badly formatted obliged entity email, then it returns an invalid ServiceResult.")
    void createPscDiscrepancyReportReturnsInvalidServiceResultWhenIncorrectEmailFormat() throws ServiceException {

        pscDiscrepancyReport.setObligedEntityEmail(INVALID_EMAIL);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
        errData.addError(error);

        when(mockReportDiscrepancyValidator.validateForCreation(eq(pscDiscrepancyReport), any(Errors.class))).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService
                .createPscDiscrepancyReport(pscDiscrepancyReport, mockRequest);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When createPscDiscrepancyReport catches a MongoException, then it transforms it to a ServiceException.")
    void createPscDiscrepancyReportThrowsServiceException() {

        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);

        PscDiscrepancyReportEntityData pscDiscrepancyReportData = new PscDiscrepancyReportEntityData();
        pscDiscrepancyReportData.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReportEntity.setData(pscDiscrepancyReportData);

        when(mockLinkFactory.createLinkPscDiscrepancyReport(anyString())).thenReturn(SELF_LINK);

        when(mockReportRepo.insert(pscDiscrepancyReportEntity)).thenThrow(new MongoException(""));
        when(mockReportMapper.restToEntity(pscDiscrepancyReport)).thenReturn(pscDiscrepancyReportEntity);

        when(mockReportDiscrepancyValidator.validateForCreation(eq(pscDiscrepancyReport), any(Errors.class))).thenReturn(new Errors());

        assertThrows(ServiceException.class,
                () -> pscDiscrepancyReportService.createPscDiscrepancyReport(pscDiscrepancyReport, mockRequest));
    }

    @Test
    @DisplayName("When updateDiscrepancyReport finds an existing report to update, then it updates it and returns success")
    void updatePscDiscrepancyReport() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        doReturn(Optional.of(preexistingReportEntity)).when(mockReportRepo).findById(REPORT_ID);

        doReturn(preexistingReport).when(mockReportMapper).entityToRest(preexistingReportEntity);

        PscDiscrepancyReportEntity savedEntity = mock(PscDiscrepancyReportEntity.class);
        PscDiscrepancyReport savedReport = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());
        doReturn(savedEntity).when(mockReportRepo).save(preexistingReportEntity);
        doReturn(savedReport).when(mockReportMapper).entityToRest(savedEntity);
        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());
        reportWithUpdatesToApply.setObligedEntityContactName(VALID_CONTACT_NAME);
        reportWithUpdatesToApply.setObligedEntityTelephoneNumber(VALID_TELEPHONE_NUMBER);
        reportWithUpdatesToApply.setObligedEntityType(VALID_TYPE);

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertEquals(ServiceResultStatus.UPDATED, result.getStatus());
        assertEquals(ReportStatus.INVALID.toString(), savedReport.getStatus());
        assertSame(savedReport, result.getData());
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with an invalid character in the contact name, then it returns an invalid ServiceResult")
    void updatePscDiscrepancyReport_InvalidContactName() throws ServiceException{
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity))
                .thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());
        reportWithUpdatesToApply.setObligedEntityContactName(INVALID_CONTACT_NAME);

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID, reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with an empty contact name, then it returns an invalid ServiceResult")
    void updatePscDiscrepancyReport_EmptyContactName() throws ServiceException{
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity))
                .thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " must not be empty").build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());
        reportWithUpdatesToApply.setObligedEntityContactName("");

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID, reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with a badly formatted obliged entity email, then it returns an invalid ServiceResult.")
    void updatePscDiscrepancyReportReturnsInvalidServiceResultWhenIncorrectEmailFormat() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity)).thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(INVALID_EMAIL, ReportStatus.INVALID.toString());

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with an empty telephone number, then it returns an invalid ServiceResult")
    void updatePscDiscrepancyReport_EmptyTelephoneNumber() throws ServiceException{
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity))
                .thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error =
                Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TELEPHONE_NUMBER_LOCATION)
                        .withError(
                                OBLIGED_ENTITY_TELEPHONE_NUMBER_LOCATION + NOT_EMPTY_ERROR_MESSAGE)
                        .build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());
        reportWithUpdatesToApply.setObligedEntityTelephoneNumber("");

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result =
                pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID, reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with a null status, then it returns an invalid ServiceResult.")
    void updatePscDiscrepancyReportReturnsInvalidServiceResultWhenNullStatus() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity)).thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(STATUS).withError(STATUS + " must not be empty or null").build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, null);

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with an empty status, then it returns an invalid ServiceResult.")
    void updatePscDiscrepancyReportReturnsInvalidServiceResultWhenEmptyStatus() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity)).thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(STATUS).withError(STATUS + " must not be empty or null").build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, "");

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with an unknown status type, then it returns an invalid ServiceResult.")
    void updatePscDiscrepancyReportReturnsInvalidServiceResultWhenUnknownStatus() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity)).thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(STATUS).withError(STATUS + " is not one of the correct values")
                .build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, "Bad status");

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("When updatePscDiscrepancy is supplied with different etag to that of the stored discrepancy, then it returns an invalid ServiceResult.")
    void updatePscDiscrepancyReportReturnsInvalidServiceResultWhenDifferentEtag() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.of(preexistingReportEntity));
        when(mockReportMapper.entityToRest(preexistingReportEntity)).thenReturn(preexistingReport);

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(ETAG).withError(ETAG + " etag does not match. etag in system")
                .build();
        errData.addError(error);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, "Bad status");
        reportWithUpdatesToApply.setEtag("2");

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(errData);

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
    }

    @Test
    @DisplayName("When updateDiscrepancyReport cannot find existing report, then it returns not found")
    void updatePscDiscrepancyReportCannotFindExistingReturnsNotFound() throws ServiceException {
        when(mockReportRepo.findById(REPORT_ID)).thenReturn(Optional.empty());
        PscDiscrepancyReport reportWithUpdatesToApply = new PscDiscrepancyReport();
        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);
        assertEquals(ServiceResultStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    @DisplayName("When updatePscDiscrepancyReport catches a MongoException from existing report search, then it transforms it to a ServiceException.")
    void updatePscDiscrepancyReportThrowsServiceException() {
        when(mockReportRepo.findById(REPORT_ID)).thenThrow(MongoException.class);
        assertThrows(ServiceException.class, () -> pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                pscDiscrepancyReport, mockRequest));
    }

    @Test
    @DisplayName("When a completed report is submitted to updatePscDiscrepancyReport, and the submit to chips succeeds, then the report is saved with a status of SUBMITTED")
    void updatePscDiscrepancyReportSendReportSubmitted() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        doReturn(Optional.of(preexistingReportEntity)).when(mockReportRepo).findById(REPORT_ID);
        doReturn(preexistingReport).when(mockReportMapper).entityToRest(preexistingReportEntity);

        PscDiscrepancyReportEntity mockSavedEntity = mock(PscDiscrepancyReportEntity.class);
        PscDiscrepancyReport savedReport = createReport(VALID_EMAIL, ReportStatus.COMPLETE.toString());
        doReturn(mockSavedEntity).when(mockReportRepo).save(preexistingReportEntity);
        doReturn(savedReport).when(mockReportMapper).entityToRest(mockSavedEntity);

        List<PscDiscrepancy> savedDiscrepancies = createDiscrepancies(DISCREPANCY_DETAILS);
        doReturn(ServiceResult.found(savedDiscrepancies)).when(mockDiscrepancyService).getDiscrepancies(REPORT_ID,
                mockRequest);

        PscSubmission expectedSubmission = new PscSubmission();
        expectedSubmission.setReport(savedReport);
        expectedSubmission.setDiscrepancies(savedDiscrepancies);

        setupMockRequestWithMockSession();
        doReturn(true).when(mockSender).send(eq(expectedSubmission), any(CloseableHttpClient.class), eq(REQUEST_ID));

        PscDiscrepancyReportEntityData mockSavedEntityData = mock(PscDiscrepancyReportEntityData.class);
        when(mockSavedEntity.getData()).thenReturn(mockSavedEntityData);
        doNothing().when(mockSavedEntityData).setStatus(ReportStatus.SUBMITTED.toString());
        PscDiscrepancyReportEntity mockSavedSentEntity = mock(PscDiscrepancyReportEntity.class);
        doReturn(mockSavedSentEntity).when(mockReportRepo).save(mockSavedEntity);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(new Errors());
        when(mockReportDiscrepancyValidator.validate(eq(savedReport), any(Errors.class))).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);

        assertEquals(ServiceResultStatus.UPDATED, result.getStatus());
        assertEquals(ReportStatus.COMPLETE.toString(), savedReport.getStatus());
        assertSame(savedReport, result.getData());
    }

    @Test
    @DisplayName("When a completed report is submitted to updatePscDiscrepancyReport, and the submit to chips fails, then the report is saved with a status of FAILED_TO_SUBMIT")
    void updatePscDiscrepancyReportSendReportFailedToSubmit() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        doReturn(Optional.of(preexistingReportEntity)).when(mockReportRepo).findById(REPORT_ID);
        doReturn(preexistingReport).when(mockReportMapper).entityToRest(preexistingReportEntity);

        PscDiscrepancyReportEntity mockSavedEntity = mock(PscDiscrepancyReportEntity.class);
        PscDiscrepancyReport savedReport = createReport(VALID_EMAIL, ReportStatus.COMPLETE.toString());
        doReturn(mockSavedEntity).when(mockReportRepo).save(preexistingReportEntity);
        doReturn(savedReport).when(mockReportMapper).entityToRest(mockSavedEntity);

        List<PscDiscrepancy> savedDiscrepancies = createDiscrepancies(DISCREPANCY_DETAILS);
        doReturn(ServiceResult.found(savedDiscrepancies)).when(mockDiscrepancyService).getDiscrepancies(REPORT_ID,
                mockRequest);

        PscSubmission expectedSubmission = new PscSubmission();
        expectedSubmission.setReport(savedReport);
        expectedSubmission.setDiscrepancies(savedDiscrepancies);

        setupMockRequestWithMockSession();
        doReturn(false).when(mockSender).send(eq(expectedSubmission), any(CloseableHttpClient.class), eq(REQUEST_ID));

        PscDiscrepancyReportEntityData mockSavedEntityData = mock(PscDiscrepancyReportEntityData.class);
        when(mockSavedEntity.getData()).thenReturn(mockSavedEntityData);
        doNothing().when(mockSavedEntityData).setStatus(ReportStatus.FAILED_TO_SUBMIT.toString());
        PscDiscrepancyReportEntity mockSavedSentEntity = mock(PscDiscrepancyReportEntity.class);
        doReturn(mockSavedSentEntity).when(mockReportRepo).save(mockSavedEntity);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(new Errors());
        when(mockReportDiscrepancyValidator.validate(eq(savedReport), any(Errors.class))).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);

        assertEquals(ServiceResultStatus.UPDATED, result.getStatus());
        assertEquals(ReportStatus.COMPLETE.toString(), savedReport.getStatus());
        assertSame(savedReport, result.getData());
    }

    @Test
    @DisplayName("When update leads to a report being sent to chips and the saving of the result of that sent throws a MongoEx, then that ex is swallowed.")
    void updatePscDiscrepancyReportSendReportCatchesMongoEx() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        doReturn(Optional.of(preexistingReportEntity)).when(mockReportRepo).findById(REPORT_ID);
        doReturn(preexistingReport).when(mockReportMapper).entityToRest(preexistingReportEntity);

        PscDiscrepancyReportEntity mockSavedEntity = mock(PscDiscrepancyReportEntity.class);
        PscDiscrepancyReport savedReport = createReport(VALID_EMAIL, ReportStatus.COMPLETE.toString());
        doReturn(mockSavedEntity).when(mockReportRepo).save(preexistingReportEntity);
        doReturn(savedReport).when(mockReportMapper).entityToRest(mockSavedEntity);

        List<PscDiscrepancy> savedDiscrepancies = createDiscrepancies(DISCREPANCY_DETAILS);
        doReturn(ServiceResult.found(savedDiscrepancies)).when(mockDiscrepancyService).getDiscrepancies(REPORT_ID,
                mockRequest);

        PscSubmission expectedSubmission = new PscSubmission();
        expectedSubmission.setReport(savedReport);
        expectedSubmission.setDiscrepancies(savedDiscrepancies);
        setupMockRequestWithMockSession();
        when(mockSender.send(eq(expectedSubmission), any(CloseableHttpClient.class), eq(REQUEST_ID))).thenReturn(true);

        PscDiscrepancyReportEntityData mockSavedEntityData = mock(PscDiscrepancyReportEntityData.class);

        when(mockSavedEntity.getData()).thenReturn(mockSavedEntityData);
        doNothing().when(mockSavedEntityData).setStatus(ReportStatus.SUBMITTED.toString());
        doThrow(MongoException.class).when(mockReportRepo).save(mockSavedEntity);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(new Errors());
        when(mockReportDiscrepancyValidator.validate(eq(savedReport), any(Errors.class))).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);

        assertEquals(ServiceResultStatus.UPDATED, result.getStatus());
        assertEquals(ReportStatus.COMPLETE.toString(), savedReport.getStatus());
        assertSame(savedReport, result.getData());
    }

    @Test
    @DisplayName("When update leads to a report being sent to chips and the send throws a ServiceException, then the exception is swallowed and report status is saved as FAILED_TO_SUBMIT.")
    void updatePscDiscrepancyReportSendReportCatchesServiceEx() throws ServiceException {
        PscDiscrepancyReportEntity preexistingReportEntity = new PscDiscrepancyReportEntity();
        PscDiscrepancyReportEntityData preexistingReportEntityData = createReportData(VALID_EMAIL,
                ReportStatus.INCOMPLETE.toString());
        preexistingReportEntity.setData(preexistingReportEntityData);
        PscDiscrepancyReport preexistingReport = createReport(VALID_EMAIL, ReportStatus.INCOMPLETE.toString());
        doReturn(Optional.of(preexistingReportEntity)).when(mockReportRepo).findById(REPORT_ID);
        doReturn(preexistingReport).when(mockReportMapper).entityToRest(eq(preexistingReportEntity));

        PscDiscrepancyReportEntity mockSavedEntity = mock(PscDiscrepancyReportEntity.class);
        PscDiscrepancyReport savedReport = createReport(VALID_EMAIL, ReportStatus.COMPLETE.toString());
        doReturn(mockSavedEntity).when(mockReportRepo).save(preexistingReportEntity);
        doReturn(savedReport).when(mockReportMapper).entityToRest(mockSavedEntity);

        List<PscDiscrepancy> savedDiscrepancies = createDiscrepancies(DISCREPANCY_DETAILS);
        doReturn(ServiceResult.found(savedDiscrepancies)).when(mockDiscrepancyService).getDiscrepancies(REPORT_ID,
                mockRequest);

        PscSubmission expectedSubmission = new PscSubmission();
        expectedSubmission.setReport(savedReport);
        expectedSubmission.setDiscrepancies(savedDiscrepancies);

        setupMockRequestWithMockSession();
        when(mockSender.send(eq(expectedSubmission), any(CloseableHttpClient.class), eq(REQUEST_ID)))
                .thenThrow(ServiceException.class);

        PscDiscrepancyReportEntityData mockSavedEntityData = mock(PscDiscrepancyReportEntityData.class);
        when(mockSavedEntity.getData()).thenReturn(mockSavedEntityData);
        doNothing().when(mockSavedEntityData).setStatus(ReportStatus.FAILED_TO_SUBMIT.toString());
        PscDiscrepancyReportEntity mockSavedSentEntity = mock(PscDiscrepancyReportEntity.class);
        doReturn(mockSavedSentEntity).when(mockReportRepo).save(mockSavedEntity);

        PscDiscrepancyReport reportWithUpdatesToApply = createReport(VALID_EMAIL, ReportStatus.INVALID.toString());

        when(mockReportDiscrepancyValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply)).thenReturn(new Errors());
        when(mockReportDiscrepancyValidator.validate(eq(savedReport), any(Errors.class))).thenReturn(new Errors());

        ServiceResult<PscDiscrepancyReport> result = pscDiscrepancyReportService.updatePscDiscrepancyReport(REPORT_ID,
                reportWithUpdatesToApply, mockRequest);

        assertEquals(ServiceResultStatus.UPDATED, result.getStatus());
        assertEquals(ReportStatus.COMPLETE.toString(), savedReport.getStatus());
        assertSame(savedReport, result.getData());
    }

    private List<PscDiscrepancy> createDiscrepancies(String... discrepancies) {
        List<PscDiscrepancy> pscDiscrepancies = new ArrayList<>();
        for (String discrepancy : discrepancies) {
            PscDiscrepancy pscDiscrepancy = new PscDiscrepancy();
            pscDiscrepancy.setDetails(discrepancy);
            pscDiscrepancies.add(pscDiscrepancy);
        }
        return pscDiscrepancies;
    }

    private PscDiscrepancyReport createReport(String obligedEntityEmail, String status) {
        PscDiscrepancyReport report = new PscDiscrepancyReport();
        report.setObligedEntityEmail(obligedEntityEmail);
        report.setStatus(status);
        report.setEtag(ETAG_1);
        return report;
    }

    private PscDiscrepancyReportEntityData createReportData(String obligedEntityEmail, String status) {
        PscDiscrepancyReportEntityData report = new PscDiscrepancyReportEntityData();
        report.setObligedEntityEmail(obligedEntityEmail);
        report.setStatus(status);
        report.setEtag(ETAG_1);
        return report;
    }
}
