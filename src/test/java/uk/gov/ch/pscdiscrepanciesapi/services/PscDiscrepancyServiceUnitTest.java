package uk.gov.ch.pscdiscrepanciesapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import uk.gov.ch.pscdiscrepanciesapi.common.PscDiscrepancyLinkKeys;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyRepository;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.ServiceResultStatus;
import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyServiceUnitTest {

    private static final String REPORT_ID = "discrepancy-report-id";
    private static final String DISCREPANCY_ID = "discrepancy-id";
    private static final String DETAILS_DATA = "some details";
    private static final String SELF_LINK = "/psc-discrepancy-reports/" + REPORT_ID + "/discrepancies/"
            + DISCREPANCY_ID;
    private static final String PARENT_LINK = "/psc-discrepancy-reports/" + REPORT_ID;
    private static final String DISCREPANCY_DETAILS = "details";
    private static final String KIND = "kind";
    private static final String ETAG = "etag";
    private static final int YEAR = 2020;
    private static final int MONTH = 1;
    private static final int DAY = 1;

    private PscDiscrepancy pscDiscrepancy;
    private PscDiscrepancyEntity pscDiscrepancyEntity;
    private List<PscDiscrepancyEntity> pscDiscrepancyEntityList;
    private List<PscDiscrepancy> pscDiscrepancyList;

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
    }

    @Test
    @DisplayName("Test createPscDiscrepancy is successful")
    void createPscDiscrepancySuccessful() throws ServiceException {

        pscDiscrepancy.setDetails(DETAILS_DATA);
        PscDiscrepancyEntityData pscDiscrepancyData = new PscDiscrepancyEntityData();
        pscDiscrepancyData.setDetails(DETAILS_DATA);
        pscDiscrepancyEntity.setData(pscDiscrepancyData);

        when(linkFactory.createLinkPscDiscrepancy(anyString(), anyString())).thenReturn(SELF_LINK);
        when(linkFactory.createLinkPscDiscrepancyReport(REPORT_ID)).thenReturn(PARENT_LINK);

        when(mockDiscrepancyRepo.insert(pscDiscrepancyEntity)).thenReturn(pscDiscrepancyEntity);
        when(mockDiscrepancyMapper.restToEntity(pscDiscrepancy)).thenReturn(pscDiscrepancyEntity);
        when(mockDiscrepancyMapper.entityToRest(pscDiscrepancyEntity)).thenReturn(pscDiscrepancy);

        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID,
                request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.CREATED, result.getStatus());
        assertEquals(pscDiscrepancy, result.getData());
    }

    @Test
    @DisplayName("Test createPscDiscrepancy returns an invalid ServiceResult")
    void createPscDiscrepancyReturnsInvalidServiceResult() throws ServiceException {

        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS)
                .withError(DISCREPANCY_DETAILS + " must not be null").build();
        errData.addError(error);

        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID,
                request);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test createPscDiscrepancy throws ServiceExeption")
    void createPscDiscrepancyThrowsServiceException() {

        pscDiscrepancy.setDetails(DETAILS_DATA);
        PscDiscrepancyEntityData pscDiscrepancyData = new PscDiscrepancyEntityData();
        pscDiscrepancyData.setDetails(DETAILS_DATA);
        pscDiscrepancyEntity.setData(pscDiscrepancyData);

        when(linkFactory.createLinkPscDiscrepancy(anyString(), anyString())).thenReturn(SELF_LINK);
        when(linkFactory.createLinkPscDiscrepancyReport(REPORT_ID)).thenReturn(PARENT_LINK);

        when(mockDiscrepancyRepo.insert(pscDiscrepancyEntity)).thenThrow(new MongoException(""));
        when(mockDiscrepancyMapper.restToEntity(pscDiscrepancy)).thenReturn(pscDiscrepancyEntity);

        assertThrows(ServiceException.class,
                () -> pscDiscrepancyService.createPscDiscrepancy(pscDiscrepancy, REPORT_ID, request));
    }

    @Test
    @DisplayName("Test getDiscrepancies returns a list of discrepancies")
    void getDiscrepanciesIsSuccessful() throws ServiceException {
        pscDiscrepancyEntityList = new ArrayList<>();
        pscDiscrepancyEntityList.add(createTestDiscrepancyEntity(DETAILS_DATA, ETAG, KIND));
        pscDiscrepancy = createTestDiscrepancy(DETAILS_DATA, ETAG, KIND);
        pscDiscrepancyList = new ArrayList<>();
        pscDiscrepancyList.add(pscDiscrepancy);

        when(mockDiscrepancyRepo.getDiscrepancies(PARENT_LINK)).thenReturn(pscDiscrepancyEntityList);
        when(mockDiscrepancyMapper.entityToRest(pscDiscrepancyEntityList.get(0))).thenReturn(pscDiscrepancy);

        ServiceResult<List<PscDiscrepancy>> result = pscDiscrepancyService.getDiscrepancies(REPORT_ID);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.FOUND, result.getStatus());
        assertEquals(pscDiscrepancyList, result.getData());

    }

    @Test
    @DisplayName("Test getDiscrepancies using null report id returns invalid")
    void getDiscrepanciesNullIdReturnsInvalid() throws ServiceException {
        ServiceResult<List<PscDiscrepancy>> result = pscDiscrepancyService.getDiscrepancies(null);

        Err error = createErrors(REPORT_ID, " must not be null");

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test getDiscrepancies using empty String report id returns invalid")
    void getDiscrepanciesEmptyReportIdReturnsInvalid() throws ServiceException {
        ServiceResult<List<PscDiscrepancy>> result = pscDiscrepancyService.getDiscrepancies("");

        Err error = createErrors(REPORT_ID, " must not be null");

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test getDiscrepancies repository returns null list which returns not found")
    void getDiscrepanciesNullListReturnsNotFound() throws ServiceException {

        when(mockDiscrepancyRepo.getDiscrepancies(PARENT_LINK)).thenReturn(null);

        ServiceResult<List<PscDiscrepancy>> result = pscDiscrepancyService.getDiscrepancies(REPORT_ID);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Test getDiscrepancies repository returns empty list which returns not found")
    void getDiscrepanciesEmptyListReturnsNotFound() throws ServiceException {
        when(mockDiscrepancyRepo.getDiscrepancies(PARENT_LINK)).thenReturn(new ArrayList<PscDiscrepancyEntity>());

        ServiceResult<List<PscDiscrepancy>> result = pscDiscrepancyService.getDiscrepancies(REPORT_ID);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Test getDiscrepancies repository throws a MongoException which throws a ServiceException")
    void getDiscrepanciesMongoExceptionThrowsServiceException() {
        when(mockDiscrepancyRepo.getDiscrepancies(PARENT_LINK)).thenThrow(new MongoException(""));
        assertThrows(ServiceException.class, () -> pscDiscrepancyService.getDiscrepancies(REPORT_ID));

    }

    @Test
    @DisplayName("Test getDiscrepancy returns a discrepancy")
    void getDiscrepancyIsSuccessful() throws ServiceException {
        pscDiscrepancyEntity = createTestDiscrepancyEntity(DETAILS_DATA, ETAG, KIND);
        pscDiscrepancy = createTestDiscrepancy(DETAILS_DATA, ETAG, KIND);
        Optional<PscDiscrepancyEntity> optionalEntity = Optional.of(pscDiscrepancyEntity);

        when(mockDiscrepancyRepo.findById(DISCREPANCY_ID)).thenReturn(optionalEntity);
        when(mockDiscrepancyMapper.entityToRest(pscDiscrepancyEntity)).thenReturn(pscDiscrepancy);

        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.getDiscrepancy(DISCREPANCY_ID);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.FOUND, result.getStatus());
        assertEquals(pscDiscrepancy, result.getData());
    }

    @Test
    @DisplayName("Test getDiscrepancy using null discrepancy id returns invalid")
    void getDiscrepancyNullIdReturnsInvalid() throws ServiceException {
        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.getDiscrepancy(null);

        Err error = createErrors(DISCREPANCY_ID, " must not be null");

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test getDiscrepancy using empty String discrepancy id returns invalid")
    void getDiscrepancyEmptyReportIdReturnsInvalid() throws ServiceException {
        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.getDiscrepancy("");

        Err error = createErrors(DISCREPANCY_ID, " must not be null");

        assertNotNull(result);
        assertEquals(ServiceResultStatus.VALIDATION_ERROR, result.getStatus());
        assertTrue(result.getErrors().containsError(error));
    }

    @Test
    @DisplayName("Test getDiscrepancy repository returns null should return not found")
    void getDiscrepancyNullListReturnsNotFound() throws ServiceException {
        when(mockDiscrepancyRepo.findById(DISCREPANCY_ID)).thenReturn(null);

        ServiceResult<PscDiscrepancy> result = pscDiscrepancyService.getDiscrepancy(DISCREPANCY_ID);

        assertNotNull(result);
        assertEquals(ServiceResultStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    @DisplayName("Test getDiscrepancy repository throws a MongoException which throws a ServiceException")
    void getDiscrepancyMongoExceptionThrowsServiceException() {
        when(mockDiscrepancyRepo.findById(DISCREPANCY_ID)).thenThrow(new MongoException(""));
        assertThrows(ServiceException.class, () -> pscDiscrepancyService.getDiscrepancy(DISCREPANCY_ID));
    }

    private PscDiscrepancy createTestDiscrepancy(String details, String etag, String kind) {
        PscDiscrepancy pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancy.setDetails(details);
        pscDiscrepancy.setEtag(etag);
        pscDiscrepancy.setKind(kind);
        return pscDiscrepancy;
    }

    private PscDiscrepancyEntity createTestDiscrepancyEntity(String details, String etag, String kind) {
        PscDiscrepancyEntity pscDiscrepancyEntity = new PscDiscrepancyEntity();
        pscDiscrepancyEntity.setCreatedAt(LocalDateTime.of(YEAR, MONTH, DAY, 0, 0, 0, 0));
        pscDiscrepancyEntity.setId(DISCREPANCY_ID);

        PscDiscrepancyEntityData pscDiscrepancyEntityData = new PscDiscrepancyEntityData();
        pscDiscrepancyEntityData.setDetails(details);
        pscDiscrepancyEntityData.setEtag(etag);
        pscDiscrepancyEntityData.setKind(kind);
        pscDiscrepancyEntityData.setLinks(createLinks());
        pscDiscrepancyEntity.setData(pscDiscrepancyEntityData);

        pscDiscrepancyEntity.setData(pscDiscrepancyEntityData);

        return pscDiscrepancyEntity;
    }

    private Links createLinks() {
        Links links = new Links();
        Map<String, String> linksMap = new HashMap<String, String>();
        linksMap.put("self", SELF_LINK);
        linksMap.put(PscDiscrepancyLinkKeys.PSC_DISCREPANCY_REPORT.toString(), PARENT_LINK);
        links.setLinks(linksMap);
        return links;
    }

    private Err createErrors(String field, String message) {
        Err error = Err.invalidBodyBuilderWithLocation(field).withError(field + message).build();
        return error;
    }
}
