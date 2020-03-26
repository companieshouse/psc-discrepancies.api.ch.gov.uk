package uk.gov.ch.pscdiscrepanciesapi.models.rest;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PscDiscrepancies {

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("discrepancies")
    private List<PscDiscrepancy> pscDiscrepancyList;

    @JsonProperty("links")
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

    public List<PscDiscrepancy> getPscDiscrepancyList() {
        return pscDiscrepancyList;
    }

    public void setPscDiscrepancyList(List<PscDiscrepancy> pscDiscrepancies) {
        this.pscDiscrepancyList = pscDiscrepancies;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
