package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.service.links.Links;

import java.util.Map;

public class PscDiscrepancyReport {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("obliged_entity_name")
    private String obligedEntityName;

    @JsonProperty("obliged_entity_email")
    private String obligedEntityEmail;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("links")
    private Links links;

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

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
    
}
