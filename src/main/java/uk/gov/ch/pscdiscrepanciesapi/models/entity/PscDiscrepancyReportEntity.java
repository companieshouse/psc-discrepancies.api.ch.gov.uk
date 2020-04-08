package uk.gov.ch.pscdiscrepanciesapi.models.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Objects;

@Document(collection = "psc_discrepancy_reports")
public class PscDiscrepancyReportEntity {
    @Id
    @Field("_id")
    private String id;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("data")
    private PscDiscrepancyReportEntityData data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PscDiscrepancyReportEntityData getData() {
        return data;
    }

    public void setData(PscDiscrepancyReportEntityData data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, data, id, updatedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PscDiscrepancyReportEntity)) {
            return false;
        }
        PscDiscrepancyReportEntity other = (PscDiscrepancyReportEntity) obj;
        return Objects.equals(createdAt, other.createdAt) && Objects.equals(data, other.data)
                        && Objects.equals(id, other.id)
                        && Objects.equals(updatedAt, other.updatedAt);
    }

    @Override
    public String toString() {
        return "PscDiscrepancyReportEntity [id=" + id + ", createdAt=" + createdAt + ", updatedAt="
                        + updatedAt + ", data=" + data + "]";
    }
}
