package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import java.util.List;

public class PscSubmission {
    
    private String requestId;
    private PscDiscrepancyReport report;
    private List<PscDiscrepancy> discrepancies;

    public PscDiscrepancyReport getReport() {
        return report;
    }

    public void setReport(PscDiscrepancyReport report) {
        this.report = report;
    }

    public List<PscDiscrepancy> getDiscrepancies() {
        return discrepancies;
    }

    public void setDiscrepancies(List<PscDiscrepancy> discrepancies) {
        this.discrepancies = discrepancies;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
}
