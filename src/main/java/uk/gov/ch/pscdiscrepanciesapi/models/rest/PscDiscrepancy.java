package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.service.rest.ApiObjectImpl;

public class PscDiscrepancy extends ApiObjectImpl {

    @JsonProperty("details")
    private String details;

    @JsonProperty("psc_name")
    private String pscName;

    @JsonProperty("psc_date_of_birth")
    private String pscDateOfBirth;

    @JsonProperty("psc_discrepancy_types")
    private List<String> pscDiscrepancyTypes;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<String> getPscDiscrepancyTypes() {
        return pscDiscrepancyTypes;
    }

    public void setPscDiscrepancyTypes(List<String> pscDiscrepancyTypes) {
        this.pscDiscrepancyTypes = pscDiscrepancyTypes;
    }

    public String getPscName() {
        return pscName;
    }

    public void setPscName(String pscName) {
        this.pscName = pscName;
    }

    public String getPscDateOfBirth() {
        return pscDateOfBirth;
    }

    public void setPscDateOfBirth(String pscDateOfBirth) {
        this.pscDateOfBirth = pscDateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PscDiscrepancy that = (PscDiscrepancy) o;
        return Objects.equals(details, that.details) &&
                Objects.equals(pscName, that.pscName) &&
                Objects.equals(pscDateOfBirth, that.pscDateOfBirth) &&
                Objects.equals(pscDiscrepancyTypes, that.pscDiscrepancyTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), details, pscName, pscDateOfBirth, pscDiscrepancyTypes);
    }

    @Override
    public String toString() {
        return "PscDiscrepancy{" +
                "details='" + details + '\'' +
                ", pscName='" + pscName + '\'' +
                ", pscDateOfBirth='" + pscDateOfBirth + '\'' +
                ", pscDiscrepancyType=" + pscDiscrepancyTypes +
                '}';
    }
}
