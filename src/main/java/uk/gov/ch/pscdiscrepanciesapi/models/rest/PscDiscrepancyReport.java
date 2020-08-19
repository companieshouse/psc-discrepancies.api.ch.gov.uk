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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((companyNumber == null) ? 0 : companyNumber.hashCode());
        result = prime * result + ((obligedEntityOrganisationName == null) ? 0 : obligedEntityOrganisationName.hashCode());
        result = prime * result + ((obligedEntityEmail == null) ? 0 : obligedEntityEmail.hashCode());
        result = prime * result + ((obligedEntityName == null) ? 0 : obligedEntityName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof PscDiscrepancyReport)) {
            return false;
        }
        PscDiscrepancyReport other = (PscDiscrepancyReport) obj;
        
        return Objects.equals(super.getEtag(), other.getEtag())
            && Objects.equals(super.getKind(), other.getKind())
            && Objects.deepEquals(super.getLinks(), other.getLinks())
            && Objects.equals(companyNumber, other.companyNumber)
            && Objects.equals(obligedEntityOrganisationName, other.obligedEntityOrganisationName)
            && Objects.equals(obligedEntityEmail, other.obligedEntityEmail)
            && Objects.equals(obligedEntityName, other.obligedEntityName)
            && Objects.equals(status, other.status);
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
