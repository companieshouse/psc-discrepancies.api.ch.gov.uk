package uk.gov.ch.pscdiscrepanciesapi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyReportRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportServiceUnitTest {

    private static final String REPORT_ID = "reportId";

    private PscDiscrepancyReport pscDiscrepancyReport;
    private PscDiscrepancyReportEntity pscDiscrepancyReportEntity;

    @Mock
    private PscDiscrepancyReportMapper mockReportMapper;

    @Mock
    private PscDiscrepancyReportRepository mockReportRepo;

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

        assertNotNull(pscDiscrepancyReport);
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
}
