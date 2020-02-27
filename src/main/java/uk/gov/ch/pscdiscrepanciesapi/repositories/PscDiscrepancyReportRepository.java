package uk.gov.ch.pscdiscrepanciesapi.repositories;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;

@Repository
public interface PscDiscrepancyReportRepository extends MongoRepository<PscDiscrepancyReportEntity, String> {
    List<PscDiscrepancyReportEntity> findPscDiscrepancyReportById(String reportId);
}
