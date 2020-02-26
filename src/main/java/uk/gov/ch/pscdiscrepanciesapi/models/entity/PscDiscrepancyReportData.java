package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

class PscDiscrepancyReportData {
    @Field("kind")
    private String kind;

    @Field("etag")
    private String etag;

    @Field("obliged_entity_name")
    private String obligedEntityName;

    @Field("obliged_entity_email")
    private String obligedEntityEmail;

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

    public String getObligedEntityName() {
        return obligedEntityName;
    }

    public void setObligedEntityName(String obligedEntityName) {
        this.obligedEntityName = obligedEntityName;
    }

    public String getObligedEntityEmail() {
        return obligedEntityEmail;
    }

    public void setObligedEntityEmail(String obligedEntityEmail) {
        this.obligedEntityEmail = obligedEntityEmail;
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
}
