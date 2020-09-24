package uk.gov.ch.pscdiscrepanciesapi.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.context.annotation.RequestScope;

import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;

@RequestScope
@Mapper(componentModel = "spring")
public interface PscDiscrepancyMapper {

    @Mapping(source = "pscDiscrepancyEntity.data.kind", target = "kind")
    @Mapping(source = "pscDiscrepancyEntity.data.etag", target = "etag")
    @Mapping(source = "pscDiscrepancyEntity.data.details", target = "details")
    @Mapping(source = "pscDiscrepancyEntity.data.pscName", target = "pscName")
    @Mapping(source = "pscDiscrepancyEntity.data.pscDateOfBirth", target = "pscDateOfBirth")
    @Mapping(source = "pscDiscrepancyEntity.data.links", target = "links.links")

    PscDiscrepancy entityToRest(PscDiscrepancyEntity pscDiscrepancyEntity);

    @InheritInverseConfiguration
    PscDiscrepancyEntity restToEntity(PscDiscrepancy pscDiscrepancy);
}
