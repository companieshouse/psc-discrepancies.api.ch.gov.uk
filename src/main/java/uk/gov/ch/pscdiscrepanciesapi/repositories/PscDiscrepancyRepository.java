package uk.gov.ch.pscdiscrepanciesapi.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;

@Repository
public interface PscDiscrepancyRepository extends MongoRepository<PscDiscrepancyEntity, String> {
}
