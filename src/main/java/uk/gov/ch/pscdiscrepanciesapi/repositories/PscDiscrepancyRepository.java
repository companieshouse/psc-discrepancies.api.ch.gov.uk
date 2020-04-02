package uk.gov.ch.pscdiscrepanciesapi.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;

@Repository
public interface PscDiscrepancyRepository extends MongoRepository<PscDiscrepancyEntity, String> {
	
	@Query(value="{'data.links.linksMap.psc-discrepancy-reports' : ?0}")
	public List<PscDiscrepancyEntity> getDiscrepancies(String reportLink);
}
