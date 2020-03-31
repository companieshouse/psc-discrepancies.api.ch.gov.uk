package uk.gov.ch.pscdiscrepanciesapi.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.common.Kind;
import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
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
    
    /**
     * Create a PSC discrepancy report.
     * 
     * @param pscDiscrepancyReport Report details to be stored
     * @param request Http request
     * 
     * @return ServiceResult object with created PSC discrepancy report
     * 
     * @throws ServiceException
     */
    public ServiceResult<PscDiscrepancyReport> createPscDiscrepancyReport(
            PscDiscrepancyReport pscDiscrepancyReport, HttpServletRequest request)
            throws ServiceException {
        
        Errors validationErrors = validateEmail(pscDiscrepancyReport.getObligedEntityEmail());

        if (validationErrors.hasErrors()) {
            Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("validationErrors", validationErrors);
            LOG.error("Validation errors", debugMap);
            return ServiceResult.invalid(validationErrors);
        } else {
            try {
                PscDiscrepancyReportEntity reportToStore =
                        pscDiscrepancyReportMapper.restToEntity(pscDiscrepancyReport);

                String pscDiscrepancyReportId = UUID.randomUUID().toString();
                reportToStore.setId(pscDiscrepancyReportId);
                reportToStore.setCreatedAt(LocalDateTime.now());
                reportToStore.getData().setKind(Kind.PSC_DISCREPANCY_REPORT);
                reportToStore.getData().setEtag(createEtag());
                reportToStore.getData().setLinks(linksForCreation(pscDiscrepancyReportId));

                PscDiscrepancyReportEntity storedReport =
                        pscDiscrepancyReportRepository.insert(reportToStore);

                PscDiscrepancyReport reportToReturn =
                        pscDiscrepancyReportMapper.entityToRest(storedReport);

                return ServiceResult.created(reportToReturn);
            } catch (MongoException me) {
                ServiceException serviceException =
                        new ServiceException("Exception storing PSC discrepancy report: ", me);
                Map<String, Object> debugMap = new HashMap<>();
                debugMap.put("validationErrors", validationErrors);
                LOG.errorRequest(request, serviceException,
                        createPscDiscrepancyReportDebugMap(pscDiscrepancyReport));
                throw serviceException;
            }
        }
    }
    
    /**
     * Validate obliged entity email.
     * 
     * @param email Email to validate
     * 
     * @return Errors object containing any errors
     */
    private Errors validateEmail(String email) {
        Errors errData = new Errors();
        Err error = null;
        if (email == null || email.isEmpty()) {
            error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " must not be null").build();
            errData.addError(error);
        } else {
            String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]+$";
            
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if(!matcher.matches()) {
                error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                    .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
                errData.addError(error);
            }
        }

        return errData;
    }

    private String createEtag() {
        return GenerateEtagUtil.generateEtag();
    }
    
    private Links linksForCreation(String pscDiscrepancyReportId) {
    
        Links links = new Links();

        String selfLink = linkFactory.createLinkPscDiscrepancyReport(pscDiscrepancyReportId);
        links.setLink(CoreLinkKeys.SELF, selfLink);

        return links;
    }
    
    /**
     * Create a debug map for structured logging
     * 
     * @param pscDiscrepancyReport
     * 
     * @return Debug map
     */
    public Map<String,Object> createPscDiscrepancyReportDebugMap(PscDiscrepancyReport pscDiscrepancyReport) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("obliged_entity_name", pscDiscrepancyReport.getObligedEntityName());
        debugMap.put("obliged_entity_email", pscDiscrepancyReport.getObligedEntityEmail());
        debugMap.put("company_number", pscDiscrepancyReport.getCompanyNumber());
        debugMap.put("status", pscDiscrepancyReport.getStatus());
        return debugMap;
    }
}
