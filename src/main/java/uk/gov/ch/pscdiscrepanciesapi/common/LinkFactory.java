package uk.gov.ch.pscdiscrepanciesapi.common;

import org.springframework.stereotype.Component;

/**
 * Centralised component to create links for the rest of the service, so that the
 * code to create links isn't spread all over the place.
 */
@Component
public class LinkFactory {
    private static final String PSC_DISCREPANCY_REPORT = "psc-discrepancy-reports";
    private static final String PSC_DISCREPANCIES = "discrepancies";
    private static final String SLASH = "/";

    public String createLinkPscDiscrepancyReports() {
        StringBuilder builder = new StringBuilder();
        builder.append(SLASH);
        builder.append(PSC_DISCREPANCY_REPORT);
        return builder.toString();
    }

    public String createLinkPscDiscrepancyReport(String pscDiscrepancyReportId) {
        StringBuilder builder = new StringBuilder();
        builder.append(createLinkPscDiscrepancyReports());
        builder.append(SLASH);
        builder.append(pscDiscrepancyReportId);
        return builder.toString();
    }

    public String createLinkPscDiscrepancies(String pscDiscrepancyReportId) {
        StringBuilder builder = new StringBuilder();
        builder.append(createLinkPscDiscrepancyReport(pscDiscrepancyReportId));
        builder.append(SLASH);
        builder.append(PSC_DISCREPANCIES);
        return builder.toString();
    }

    public String createLinkPscDiscrepancy(String pscDiscrepancyId, String pscDiscrepancyReportId) {
        StringBuilder builder = new StringBuilder();
        builder.append(createLinkPscDiscrepancyReport(pscDiscrepancyReportId));
        builder.append(SLASH);  
        builder.append(PSC_DISCREPANCIES);  
        builder.append(SLASH);  
        builder.append(pscDiscrepancyId);   
        return builder.toString();
    }
}
