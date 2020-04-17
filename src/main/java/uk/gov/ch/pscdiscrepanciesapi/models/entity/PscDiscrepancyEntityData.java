package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;

public class PscDiscrepancyEntityData {
    @Field("kind")
    private String kind;

    @Field("etag")
    private String etag;

    @Field("details")
    private String details;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
