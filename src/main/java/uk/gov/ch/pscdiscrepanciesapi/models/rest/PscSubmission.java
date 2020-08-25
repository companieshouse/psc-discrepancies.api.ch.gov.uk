package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        return Objects.deepEquals(discrepancies, other.discrepancies);
    }

    @Override
    public String toString() {
        return "PscSubmission [requestId=" + requestId + ", report=" + report + ", discrepancies="
                        + discrepancies + "]";
    }
    
    /**
     * Create a debug map for structured logging
     *
     * @return Debug map
     */
    public Map<String, Object> debugMap() {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("submission", toString());
        return debugMap;
    }


}
