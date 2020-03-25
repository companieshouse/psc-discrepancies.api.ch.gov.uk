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

    @Mapping(source = "pscDiscrepancy.data.kind", target = "kind")
    @Mapping(source = "pscDiscrepancy.data.etag", target = "etag")
    @Mapping(source = "pscDiscrepancy.data.details", target = "details")
    @Mapping(source = "pscDiscrepancy.data.links", target = "links")

    PscDiscrepancy entityToRest(PscDiscrepancyEntity pscDiscrepancy);

    @InheritInverseConfiguration
    PscDiscrepancyEntity restToEntity(PscDiscrepancy pscDiscrepancy);
}
