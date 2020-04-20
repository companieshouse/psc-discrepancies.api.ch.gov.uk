package uk.gov.ch.pscdiscrepanciesapi.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;

@RequestScope
@Mapper(componentModel = "spring")
public interface PscDiscrepancyReportMapper {

    @Mapping(source = "pscDiscrepancyReportEntity.data.kind", target = "kind")
    @Mapping(source = "pscDiscrepancyReportEntity.data.etag", target = "etag")
    @Mapping(source = "pscDiscrepancyReportEntity.data.obligedEntityName", target = "obligedEntityName")
    @Mapping(source = "pscDiscrepancyReportEntity.data.obligedEntityContactName", target = "obligedEntityContactName")
    @Mapping(source = "pscDiscrepancyReportEntity.data.obligedEntityEmail", target = "obligedEntityEmail")
    @Mapping(source = "pscDiscrepancyReportEntity.data.companyNumber", target = "companyNumber")
    @Mapping(source = "pscDiscrepancyReportEntity.data.status", target = "status")
    @Mapping(source = "pscDiscrepancyReportEntity.data.links", target = "links.links")

    PscDiscrepancyReport entityToRest(PscDiscrepancyReportEntity pscDiscrepancyReportEntity);

    @InheritInverseConfiguration
    PscDiscrepancyReportEntity restToEntity(PscDiscrepancyReport pscDiscrepancyReport);
}
