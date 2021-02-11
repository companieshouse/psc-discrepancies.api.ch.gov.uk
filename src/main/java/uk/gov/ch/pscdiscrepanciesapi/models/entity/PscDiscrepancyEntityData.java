package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;

public class PscDiscrepancyEntityData {
    @Field("kind")
    private String kind;

    @Field("etag")
    private String etag;

    @Field("psc_name")
    private String pscName;

    @Field("psc_date_of_birth")
    private String pscDateOfBirth;

    @Field("details")
    private String details;

    @Field("psc_discrepancy_types")
    private List<String> pscDiscrepancyTypes;

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

    public String getPscName() {
        return pscName;
    }

    public List<String> getPscDiscrepancyTypes() {return pscDiscrepancyTypes; }

    public void setPscDiscrepancyTypes(List<String> pscDiscrepancyTypes) { this.pscDiscrepancyTypes = pscDiscrepancyTypes; }

    public void setPscName(String pscName) {
        this.pscName = pscName;
    }

    public String getPscDateOfBirth() {
        return pscDateOfBirth;
    }

    public void setPscDateOfBirth(String pscDateOfBirth) {
        this.pscDateOfBirth = pscDateOfBirth;
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
