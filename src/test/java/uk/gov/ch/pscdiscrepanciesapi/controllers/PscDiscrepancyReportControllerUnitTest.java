package uk.gov.ch.pscdiscrepanciesapi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.services.PscDiscrepancyReportService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportControllerUnitTest {

    private static final String REPORT_ID = "reportId";

    private PscDiscrepancyReport pscDiscrepancyReport;

    @Mock
    private PscDiscrepancyReportService mockReportService;

    @InjectMocks
    private PscDiscrepancyReportController pscDiscrepancyReportController;

    @BeforeEach
    void setUp() {
        pscDiscrepancyReport = new PscDiscrepancyReport();
    }

    @Test
    @DisplayName("Test getPscDiscrepancyReport by id is successful")
    void getPscDiscrepancyReportSuccessful() {
        when(mockReportService.findPscDiscrepancyReportById(REPORT_ID)).thenReturn(pscDiscrepancyReport);
        ResponseEntity response = pscDiscrepancyReportController.get(REPORT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pscDiscrepancyReport, response.getBody());
    }

    @Test
    @DisplayName("Test getPscDiscrepancyReport by id is unsuccessful")
    void getPscDiscrepancyReportUnsuccessful() {
        when(mockReportService.findPscDiscrepancyReportById(REPORT_ID)).thenReturn(null);
        ResponseEntity response = pscDiscrepancyReportController.get(REPORT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
