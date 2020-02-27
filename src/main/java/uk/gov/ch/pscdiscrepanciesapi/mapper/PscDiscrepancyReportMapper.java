package uk.gov.ch.pscdiscrepanciesapi.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;

@RequestScope
@Mapper(componentModel = "spring")
public interface PscDiscrepancyReportMapper {

    @Mappings({
            @Mapping(source = "pscDiscrepancyReport.data.kind", target = "kind"),
            @Mapping(source = "pscDiscrepancyReport.data.etag", target = "etag"),
            @Mapping(source = "pscDiscrepancyReport.data.obligedEntityName", target = "obligedEntityName"),
            @Mapping(source = "pscDiscrepancyReport.data.obligedEntityEmail", target = "obligedEntityEmail"),
            @Mapping(source = "pscDiscrepancyReport.data.companyNumber", target = "companyNumber"),
            @Mapping(source = "pscDiscrepancyReport.data.status", target = "status"),
            @Mapping(source = "pscDiscrepancyReport.data.links", target = "links")
    })
    PscDiscrepancyReport entityToRest(PscDiscrepancyReportEntity pscDiscrepancyReport);

    @InheritInverseConfiguration
    PscDiscrepancyReportEntity restToEntity(PscDiscrepancyReport pscDiscrepancyReport);
}
