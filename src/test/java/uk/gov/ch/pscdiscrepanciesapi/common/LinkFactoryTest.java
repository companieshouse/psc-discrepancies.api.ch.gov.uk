package uk.gov.ch.pscdiscrepanciesapi.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class LinkFactoryTest {
    private static final String BASE_URL = "/psc-discrepancy-reports";
    private static final String PSC_DISCREPANCY_REPORT_ID = "3";
    private static final String PSC_DISCREPANCY_ID = "dis1";
    
    private LinkFactory underTest = new LinkFactory();

    @Test
    @DisplayName("createLinkPscDiscrepancyReports Test")
    void createLinkProsecutionCasesTest() {
        assertEquals(BASE_URL, underTest.createLinkPscDiscrepancyReports());
    }

    @Test
    @DisplayName("createLinkPscDiscrepancyReport Test")
    void createLinkProsecutionCaseTest() {
        assertEquals(BASE_URL + "/" + PSC_DISCREPANCY_REPORT_ID,
                underTest.createLinkPscDiscrepancyReport(PSC_DISCREPANCY_REPORT_ID));
    }

    @Test
    @DisplayName("createLinkPscDiscrepancies Test")
    void createLinkDefendants() {
        assertEquals(BASE_URL + "/" + PSC_DISCREPANCY_REPORT_ID + "/discrepancies",
                underTest.createLinkPscDiscrepancies(PSC_DISCREPANCY_REPORT_ID));
    }

    @Test
    @DisplayName("createLinkPscDiscrepancy Test")
    void createLinkDefendant() {
        assertEquals(
                BASE_URL + "/" + PSC_DISCREPANCY_REPORT_ID + "/discrepancies/" + PSC_DISCREPANCY_ID,
                underTest.createLinkPscDiscrepancy(PSC_DISCREPANCY_ID, PSC_DISCREPANCY_REPORT_ID));
    }
}
