package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.service.rest.ApiObjectImpl;

public class PscDiscrepancy extends ApiObjectImpl {

    @JsonProperty("details")
    private String details;

    @JsonProperty("psc_name")
    private String pscName;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPscName() {
        return pscName;
    }

    public void setPscName(String pscName) {
        this.pscName = pscName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(details);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PscDiscrepancy)) {
            return false;
        }
        PscDiscrepancy other = (PscDiscrepancy) obj;
        return Objects.equals(super.getEtag(), other.getEtag())
            && Objects.equals(super.getKind(), other.getKind())
            && Objects.deepEquals(super.getLinks(), other.getLinks())
            && Objects.equals(details, other.details)
            && Objects.equals(pscName, other.pscName);
    }

    @Override
    public String toString() {
        return "PscDiscrepancy [details=" + details + "psc_name=" + pscName + "]";
    }
}
