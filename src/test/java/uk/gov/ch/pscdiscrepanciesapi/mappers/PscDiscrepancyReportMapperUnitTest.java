package uk.gov.ch.pscdiscrepanciesapi.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.companieshouse.service.links.Links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportMapperUnitTest {

    private PscDiscrepancyReportMapper pscDiscrepancyReportMapper;

    private static final String KIND = "kind";
    private static final String ETAG = "etag";
    private static final String OBLIGED_ENTTIY_CONTACT_NAME = "contactName";
    private static final String OBLIGED_ENTITY_NAME = "obligedEntityName";
    private static final String OBLIGED_ENTITY_EMAIL = "obligedEntityEmail";
    private static final String OBLIGED_ENTITY_TELEPHONE_NUMBER = "obligedEntityTelephoneNumber";
    private static final String OBLIGED_ENTITY_TYPE = "obligedEntityType";
    private static final String COMPANY_NUMBER = "companyNumber";
    private static final String SUBMISSION_REFERENCE = "0000-0000-0000-0000";
    private static final Links LINKS = new Links();
    private static final Map<String, String> MAP_LINKS = new HashMap<>();

    private PscDiscrepancyReport pscDiscrepancyReport;
    private PscDiscrepancyReportEntity pscDiscrepancyReportEntity;
    private PscDiscrepancyReportEntityData pscDiscrepancyReportData;

    @BeforeEach
    void setUp() throws Exception {
        pscDiscrepancyReportMapper = Mappers.getMapper(PscDiscrepancyReportMapper.class);

        pscDiscrepancyReportEntity = new PscDiscrepancyReportEntity();
        pscDiscrepancyReportData = new PscDiscrepancyReportEntityData();
        pscDiscrepancyReportData.setKind(KIND);
        pscDiscrepancyReportData.setEtag(ETAG);
        pscDiscrepancyReportData.setObligedEntityName(OBLIGED_ENTITY_NAME);
        pscDiscrepancyReportData.setObligedEntityContactName(OBLIGED_ENTTIY_CONTACT_NAME);
        pscDiscrepancyReportData.setObligedEntityEmail(OBLIGED_ENTITY_EMAIL);
        pscDiscrepancyReportData.setObligedEntityTelephoneNumber(OBLIGED_ENTITY_TELEPHONE_NUMBER);
        pscDiscrepancyReportData.setObligedEntityType(OBLIGED_ENTITY_TYPE);
        pscDiscrepancyReportData.setCompanyNumber(COMPANY_NUMBER);
        pscDiscrepancyReportData.setSubmissionReference(SUBMISSION_REFERENCE);
        pscDiscrepancyReportData.setLinks(MAP_LINKS);
        pscDiscrepancyReportEntity.setData(pscDiscrepancyReportData);

        pscDiscrepancyReport = new PscDiscrepancyReport();
        pscDiscrepancyReport.setKind(KIND);
        pscDiscrepancyReport.setEtag(ETAG);
        pscDiscrepancyReport.setObligedEntityName(OBLIGED_ENTITY_NAME);
        pscDiscrepancyReport.setObligedEntityContactName(OBLIGED_ENTTIY_CONTACT_NAME);
        pscDiscrepancyReport.setObligedEntityEmail(OBLIGED_ENTITY_EMAIL);
        pscDiscrepancyReport.setObligedEntityTelephoneNumber(OBLIGED_ENTITY_TELEPHONE_NUMBER);
        pscDiscrepancyReport.setObligedEntityType(OBLIGED_ENTITY_TYPE);
        pscDiscrepancyReport.setCompanyNumber(COMPANY_NUMBER);
        pscDiscrepancyReport.setSubmissionReference(SUBMISSION_REFERENCE);
        pscDiscrepancyReport.setLinks(LINKS);
    }

    @Test
    @DisplayName("Test mapping of PscDiscrepancyReportEntity to PscDiscrepancyReport")
    void validateEntityToRest() {
        PscDiscrepancyReport result = pscDiscrepancyReportMapper.entityToRest(pscDiscrepancyReportEntity);

        assertEquals(pscDiscrepancyReport.getKind(), result.getKind());
        assertEquals(pscDiscrepancyReport.getEtag(), result.getEtag());
        assertEquals(pscDiscrepancyReport.getObligedEntityContactName(),
                result.getObligedEntityContactName());
        assertEquals(pscDiscrepancyReport.getObligedEntityName(), result.getObligedEntityName());
        assertEquals(pscDiscrepancyReport.getObligedEntityEmail(), result.getObligedEntityEmail());
        assertEquals(pscDiscrepancyReport.getObligedEntityTelephoneNumber(),
                result.getObligedEntityTelephoneNumber());
        assertEquals(pscDiscrepancyReport.getObligedEntityType(), result.getObligedEntityType());
        assertEquals(pscDiscrepancyReport.getCompanyNumber(), result.getCompanyNumber());
        assertEquals(pscDiscrepancyReport.getSubmissionReference(), result.getSubmissionReference());
        assertEquals(pscDiscrepancyReport.getLinks(), result.getLinks());
    }

    @Test
    @DisplayName("Test mapping of PscDiscrepancyReport to PscDiscrepancyReportEntity")
    void validateRestToEntity() {
        PscDiscrepancyReportEntity result = pscDiscrepancyReportMapper.restToEntity(pscDiscrepancyReport);

        assertEquals(pscDiscrepancyReportEntity.getData().getKind(), result.getData().getKind());
        assertEquals(pscDiscrepancyReportEntity.getData().getEtag(), result.getData().getEtag());
        assertEquals(pscDiscrepancyReportEntity.getData().getObligedEntityContactName(),
                result.getData().getObligedEntityContactName());
        assertEquals(pscDiscrepancyReportEntity.getData().getObligedEntityName(), result.getData().getObligedEntityName());
        assertEquals(pscDiscrepancyReportEntity.getData().getObligedEntityEmail(), result.getData().getObligedEntityEmail());
        assertEquals(pscDiscrepancyReportEntity.getData().getObligedEntityTelephoneNumber(),
                result.getData().getObligedEntityTelephoneNumber());
        assertEquals(pscDiscrepancyReportEntity.getData().getObligedEntityType(), result.getData().getObligedEntityType());
        assertEquals(pscDiscrepancyReportEntity.getData().getCompanyNumber(), result.getData().getCompanyNumber());
        assertEquals(pscDiscrepancyReportEntity.getData().getSubmissionReference(), result.getData().getSubmissionReference());
        assertEquals(pscDiscrepancyReportEntity.getData().getLinks(), result.getData().getLinks());
    }

}
