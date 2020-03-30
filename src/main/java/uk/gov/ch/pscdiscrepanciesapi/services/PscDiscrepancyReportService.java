package uk.gov.ch.pscdiscrepanciesapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.common.Kind;
import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.common.PscDiscrepancyLinkKeys;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyReportRepository;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.links.CoreLinkKeys;
import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

@Service
public class PscDiscrepancyReportService {

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

	private static final String OBLIGED_ENTITY_EMAIL = "Obliged Entity Email";
	
    @Autowired
    private PscDiscrepancyReportRepository pscDiscrepancyReportRepository;

    @Autowired
    private PscDiscrepancyReportMapper pscDiscrepancyReportMapper;
    
    @Autowired
    private LinkFactory linkFactory;

    public PscDiscrepancyReport findPscDiscrepancyReportById(String reportId) {
        Optional<PscDiscrepancyReportEntity> storedReport =
                pscDiscrepancyReportRepository.findById(reportId);

        return storedReport.map(pscDiscrepancyReportEntity -> pscDiscrepancyReportMapper
                .entityToRest(pscDiscrepancyReportEntity)).orElse(null);
    }
    
	public ServiceResult<PscDiscrepancyReport> createPscDiscrepancyReport(PscDiscrepancyReport pscDiscrepancyReport,
			HttpServletRequest request) throws ServiceException {
		if (pscDiscrepancyReport.getObligedEntityEmail() == null
				|| pscDiscrepancyReport.getObligedEntityEmail().isEmpty()) {
			Errors errData = new Errors();
			Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
					.withError(OBLIGED_ENTITY_EMAIL + " must not be null").build();
			errData.addError(error);
			return ServiceResult.invalid(errData);
		} else {
			try {
				PscDiscrepancyReportEntity reportToStore = pscDiscrepancyReportMapper
						.restToEntity(pscDiscrepancyReport);

				String pscDiscrepancyReportId = UUID.randomUUID().toString();
				reportToStore.setId(pscDiscrepancyReportId);
				reportToStore.setCreatedAt(LocalDateTime.now());
				reportToStore.getData().setKind(Kind.PSC_DISCREPANCY_REPORT);
				reportToStore.getData().setEtag(createEtag());
				reportToStore.getData().setLinks(linksForCreation("self", pscDiscrepancyReportId));

				PscDiscrepancyReportEntity storedReport = pscDiscrepancyReportRepository.insert(reportToStore);

				PscDiscrepancyReport reportToReturn = pscDiscrepancyReportMapper.entityToRest(storedReport);

				return ServiceResult.created(reportToReturn);
			} catch (MongoException me) {
				ServiceException serviceException = new ServiceException("Exception storing PSC discrepancy report: ",
						me);
				throw serviceException;
			}
		}
	}

    private String createEtag() {
        return GenerateEtagUtil.generateEtag();
    }
    
    private Links linksForCreation(String self, String pscDiscrepancyReportId) {
    	
        Links links = new Links();

        String selfLink = linkFactory.createLinkPscDiscrepancy(self, pscDiscrepancyReportId);
        links.setLink(CoreLinkKeys.SELF, selfLink);

        return links;
    }
    
}
