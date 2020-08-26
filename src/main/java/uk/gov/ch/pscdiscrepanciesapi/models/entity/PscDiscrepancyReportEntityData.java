package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import java.util.Map;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

public class PscDiscrepancyReportEntityData {
    @Field("kind")
    private String kind;

    @Field("etag")
    private String etag;

    @Field("obliged_entity_organisation_name")
    private String obligedEntityOrganisationName;

    @Field("obliged_entity_contact_name")
    private String obligedEntityContactName;

    @Field("obliged_entity_name")
    private String obligedEntityName;

    @Field("obliged_entity_email")
    private String obligedEntityEmail;

    @Field("obliged_entity_telephone_number")
    private String obligedEntityTelephoneNumber;

    @Field("obliged_entity_type")
    private String obligedEntityType;

    @Field("company_number")
    private String companyNumber;

    @Field("status")
    private String status;

    @Field("links")
    private Map<String, String> links;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

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

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PscDiscrepancyReportEntityData that = (PscDiscrepancyReportEntityData) o;
        return Objects.equals(kind, that.kind) &&
                Objects.equals(etag, that.etag) &&
                Objects.equals(obligedEntityOrganisationName, that.obligedEntityOrganisationName) &&
                Objects.equals(obligedEntityContactName, that.obligedEntityContactName) &&
                Objects.equals(obligedEntityName, that.obligedEntityName) &&
                Objects.equals(obligedEntityEmail, that.obligedEntityEmail) &&
                Objects.equals(obligedEntityTelephoneNumber, that.obligedEntityTelephoneNumber) &&
                Objects.equals(obligedEntityType, that.obligedEntityType) &&
                Objects.equals(companyNumber, that.companyNumber) &&
                Objects.equals(status, that.status) &&
                Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, etag, obligedEntityOrganisationName, obligedEntityContactName, obligedEntityName,
                obligedEntityEmail, obligedEntityTelephoneNumber, obligedEntityType, companyNumber, status, links);
    }

    @Override
    public String toString() {
        return "PscDiscrepancyReportEntityData [kind=" + kind
                        + ", etag=" + etag
                        + ", obligedEntityOrganisationName=" + obligedEntityOrganisationName
                        + ", obligedEntityName=" + obligedEntityName
                        + ", obligedEntityEmail=" + obligedEntityEmail
                        + ", companyNumber=" + companyNumber
                        + ", status=" + status
                        + ", links=" + links + "]";
    }

}
