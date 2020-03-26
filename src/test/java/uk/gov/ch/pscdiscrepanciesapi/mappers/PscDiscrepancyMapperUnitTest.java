package uk.gov.ch.pscdiscrepanciesapi.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyData;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.companieshouse.service.links.Links;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyMapperUnitTest {

    private PscDiscrepancyMapper pscDiscrepancyMapper;

    private static final String KIND = "kind";
    private static final String ETAG = "etag";
    private static final String DISCREPANCY_DETAILS = "details";
    private static final Links LINKS = new Links();

    private PscDiscrepancy pscDiscrepancy;
    private PscDiscrepancyEntity pscDiscrepancyEntity;
    private PscDiscrepancyData pscDiscrepancyData;

    @BeforeEach
    void setUp() throws Exception {
        pscDiscrepancyMapper = Mappers.getMapper(PscDiscrepancyMapper.class);

        pscDiscrepancyEntity = new PscDiscrepancyEntity();
        pscDiscrepancyData = new PscDiscrepancyData();
        pscDiscrepancyData.setKind(KIND);
        pscDiscrepancyData.setEtag(ETAG);
        pscDiscrepancyData.setDetails(DISCREPANCY_DETAILS);
        pscDiscrepancyData.setLinks(LINKS);
        pscDiscrepancyEntity.setData(pscDiscrepancyData);

        pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancy.setKind(KIND);
        pscDiscrepancy.setEtag(ETAG);
        pscDiscrepancy.setDetails(DISCREPANCY_DETAILS);
        pscDiscrepancy.setLinks(LINKS);
    }

    @Test
    @DisplayName("Test mapping of PscDiscrepancyEntity to PscDiscrepancy")
    void validateEntityToRest() {
        PscDiscrepancy result = pscDiscrepancyMapper.entityToRest(pscDiscrepancyEntity);

        assertEquals(pscDiscrepancy.getKind(), result.getKind());
        assertEquals(pscDiscrepancy.getEtag(), result.getEtag());
        assertEquals(pscDiscrepancy.getDetails(), result.getDetails());
        assertEquals(pscDiscrepancy.getLinks(), result.getLinks());
    }

    @Test
    @DisplayName("Test mapping of PscDiscrepancy to PscDiscrepancyEntity")
    void validateRestToEntity() {
        PscDiscrepancyEntity result = pscDiscrepancyMapper.restToEntity(pscDiscrepancy);

        assertEquals(pscDiscrepancyEntity.getData().getKind(), result.getData().getKind());
        assertEquals(pscDiscrepancyEntity.getData().getEtag(), result.getData().getEtag());
        assertEquals(pscDiscrepancyEntity.getData().getDetails(), result.getData().getDetails());
        assertEquals(pscDiscrepancyEntity.getData().getLinks(), result.getData().getLinks());
    }

}
