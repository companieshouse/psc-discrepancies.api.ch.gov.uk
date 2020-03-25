package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.ApiObjectImpl;

public class PscDiscrepancy extends ApiObjectImpl {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("details")
    private String details;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
