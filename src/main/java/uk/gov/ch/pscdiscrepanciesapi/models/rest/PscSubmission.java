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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((discrepancies == null) ? 0 : discrepancies.hashCode());
        result = prime * result + ((report == null) ? 0 : report.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PscSubmission other = (PscSubmission) obj;
        if (discrepancies == null) {
            if (other.discrepancies != null)
                return false;
        } else if (!discrepancies.equals(other.discrepancies))
            return false;
        if (report == null) {
            if (other.report != null)
                return false;
        } else if (!report.equals(other.report))
            return false;
        if (requestId == null) {
            if (other.requestId != null)
                return false;
        } else if (!requestId.equals(other.requestId))
            return false;
        return true;
    }
    
}
