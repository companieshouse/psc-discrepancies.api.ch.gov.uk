package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import uk.gov.companieshouse.service.links.Links;

public class PscDiscrepancyData {
    @Field("kind")
    private String kind;

    @Field("etag")
    private String etag;

    @Field("details")
    private String details;

    @Field("links")
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
