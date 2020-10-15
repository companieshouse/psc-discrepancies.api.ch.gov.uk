package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import uk.gov.companieshouse.service.rest.ApiObjectImpl;

public class PscDiscrepancyReport extends ApiObjectImpl {

    @JsonProperty("obliged_entity_organisation_name")
    private String obligedEntityOrganisationName;

    @JsonProperty("obliged_entity_name")
    private String obligedEntityName;

    @JsonProperty("obliged_entity_contact_name")
    private String obligedEntityContactName;

    @JsonProperty("obliged_entity_email")
    private String obligedEntityEmail;

    @JsonProperty("obliged_entity_telephone_number")
    private String obligedEntityTelephoneNumber;

    @JsonProperty("obliged_entity_type")
    private String obligedEntityType;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("status")
    private String status;

    public String getObligedEntityOrganisationName() {
        return obligedEntityOrganisationName;
    }

    public void setObligedEntityOrganisationName(String obligedEntityOrganisationName) {
        this.obligedEntityOrganisationName = obligedEntityOrganisationName;
    }

    public String getObligedEntityName() {
        return obligedEntityName;
    }

    public void setObligedEntityName(String obligedEntityName) {
        this.obligedEntityName = obligedEntityName;
    }

    public String getObligedEntityContactName() {
        return obligedEntityContactName;
    }

    public void setObligedEntityContactName(String obligedEntityContactName) {
        this.obligedEntityContactName = obligedEntityContactName;
    }

    public String getObligedEntityEmail() {
        return obligedEntityEmail;
    }

    public void setObligedEntityEmail(String obligedEntityEmail) {
        this.obligedEntityEmail = obligedEntityEmail;
    }

    public String getObligedEntityTelephoneNumber() {
        return obligedEntityTelephoneNumber;
    }

    public void setObligedEntityTelephoneNumber(String obligedEntityTelephoneNumber) {
        this.obligedEntityTelephoneNumber = obligedEntityTelephoneNumber;
    }

    public String getObligedEntityType() {
        return obligedEntityType;
    }

    public void setObligedEntityType(String obligedEntityType) {
        this.obligedEntityType = obligedEntityType;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), obligedEntityOrganisationName, obligedEntityName, obligedEntityContactName,
        obligedEntityEmail, obligedEntityTelephoneNumber, obligedEntityType, companyNumber, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PscDiscrepancyReport that = (PscDiscrepancyReport) o;
        return Objects.equals(obligedEntityName, that.obligedEntityName) &&
                Objects.equals(obligedEntityOrganisationName, that.obligedEntityOrganisationName) &&
                Objects.equals(obligedEntityContactName, that.obligedEntityContactName) &&
                Objects.equals(obligedEntityEmail, that.obligedEntityEmail) &&
                Objects.equals(obligedEntityTelephoneNumber, that.obligedEntityTelephoneNumber) &&
                Objects.equals(obligedEntityType, that.obligedEntityType) &&
                Objects.equals(companyNumber, that.companyNumber) &&
                Objects.equals(status, that.status);
    }

    @Override
    public String toString() {
        return "PscDiscrepancyReport [obligedEntityName=" + obligedEntityName
                + ", obligedEntityOrganisationName=" + obligedEntityOrganisationName
                + ", obligedEntityEmail=" + obligedEntityEmail
                + ", companyNumber=" + companyNumber
                + ", status=" + status + "]";
    }

}
