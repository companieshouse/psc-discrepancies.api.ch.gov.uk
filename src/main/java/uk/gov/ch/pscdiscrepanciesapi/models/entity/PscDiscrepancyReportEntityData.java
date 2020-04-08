package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.service.links.Links;

public class PscDiscrepancyReportEntityData {
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

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber, etag, kind, links, obligedEntityEmail, obligedEntityName,
                        status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PscDiscrepancyReportEntityData)) {
            return false;
        }
        PscDiscrepancyReportEntityData other = (PscDiscrepancyReportEntityData) obj;
        return Objects.equals(companyNumber, other.companyNumber)
                        && Objects.equals(etag, other.etag) && Objects.equals(kind, other.kind)
                        && Objects.equals(links, other.links)
                        && Objects.equals(obligedEntityEmail, other.obligedEntityEmail)
                        && Objects.equals(obligedEntityName, other.obligedEntityName)
                        && Objects.equals(status, other.status);
    }

    @Override
    public String toString() {
        return "PscDiscrepancyReportEntityData [kind=" + kind + ", etag=" + etag
                        + ", obligedEntityName=" + obligedEntityName + ", obligedEntityEmail="
                        + obligedEntityEmail + ", companyNumber=" + companyNumber + ", status="
                        + status + ", links=" + links + "]";
    }
}
