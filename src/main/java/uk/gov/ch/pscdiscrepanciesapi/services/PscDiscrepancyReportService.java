package uk.gov.ch.pscdiscrepanciesapi.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ReportStatus;
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
    private static final String STATUS = "Status";

    private static final Set<String> VALID_STATUSES;

    static {
        Set<ReportStatus> all = EnumSet.allOf(ReportStatus.class);
        Set<String> temp = new HashSet<>();
        for (ReportStatus status : all) {
            temp.add(status.name());
        }
        VALID_STATUSES = Collections.unmodifiableSet(temp);
    }

    private final PscDiscrepancyReportRepository pscDiscrepancyReportRepository;

    private final PscDiscrepancyReportMapper pscDiscrepancyReportMapper;

    private final LinkFactory linkFactory;

    public PscDiscrepancyReportService(@Autowired PscDiscrepancyReportRepository pscDiscrepancyReportRepository,
                     @Autowired PscDiscrepancyReportMapper pscDiscrepancyReportMapper,
                     @Autowired LinkFactory linkFactory) {
                        this.pscDiscrepancyReportRepository = pscDiscrepancyReportRepository;
                        this.pscDiscrepancyReportMapper = pscDiscrepancyReportMapper;
                        this.linkFactory = linkFactory;
    }

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
        
        Errors validationErrors = validateCreate(pscDiscrepancyReport);

        if (validationErrors.hasErrors()) {
            LOG.error("Validation errors", buildErrorLogMap(validationErrors));
            return ServiceResult.invalid(validationErrors);
        }

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
            LOG.errorRequest(request, serviceException,
                    createPscDiscrepancyReportDebugMap(pscDiscrepancyReport));
            throw serviceException;
        }
    }

    public ServiceResult<PscDiscrepancyReport> updatePscDiscrepancyReport(String reportId,
                    PscDiscrepancyReport reportWithUpdatesToApply, HttpServletRequest request)
                    throws ServiceException {
        final ServiceResult<PscDiscrepancyReport> reportToReturn;
        try {
            Optional<PscDiscrepancyReportEntity> queryResult =
                            pscDiscrepancyReportRepository.findById(reportId);
            if (!queryResult.isPresent()) {
                reportToReturn = ServiceResult.notFound();
            } else {
                PscDiscrepancyReportEntity preexistingReportEntity = queryResult.get();
                PscDiscrepancyReport preexistingReport =
                                pscDiscrepancyReportMapper.entityToRest(preexistingReportEntity);

                Errors validationErrors =
                                validateUpdate(preexistingReport, reportWithUpdatesToApply);
                if (validationErrors.hasErrors()) {
                    LOG.error("Validation errors", buildErrorLogMap(validationErrors));
                    reportToReturn = ServiceResult.invalid(validationErrors);
                } else {
                    PscDiscrepancyReportEntityData preexistingReportEntityData =
                                    preexistingReportEntity.getData();
                    // Now copy over all values that are allowed to be updated
                    // We do this to prevent malicious/inadvertent changing of values that must
                    // not be set by anything other than this service, e.g. kind, links, etag...
                    preexistingReportEntityData.setStatus(reportWithUpdatesToApply.getStatus());
                    preexistingReportEntityData.setObligedEntityEmail(
                                    reportWithUpdatesToApply.getObligedEntityEmail());
                    preexistingReportEntityData
                                    .setCompanyNumber(reportWithUpdatesToApply.getCompanyNumber());
                    // Update the etag value, as this has changed
                    preexistingReportEntityData.setEtag(createEtag());

                    PscDiscrepancyReportEntity storedReportEntity =
                                    pscDiscrepancyReportRepository.save(preexistingReportEntity);

                    PscDiscrepancyReport storedReport =
                                    pscDiscrepancyReportMapper.entityToRest(storedReportEntity);
                    reportToReturn = ServiceResult.updated(storedReport);
                }
            }
        } catch (MongoException me) {
            ServiceException serviceException =
                            new ServiceException("Exception storing PSC discrepancy report: ", me);
            LOG.errorRequest(request, serviceException,
                            createPscDiscrepancyReportDebugMap(reportWithUpdatesToApply));
            throw serviceException;
        }
        return reportToReturn;
    }

    private Errors validateUpdate(PscDiscrepancyReport preexistingReport,
                    PscDiscrepancyReport updatedReport) {
        Errors errData = new Errors();
        if (!preexistingReport.getEtag().equals(updatedReport.getEtag())) {
            Err nonMatchingEtag = Err.invalidBodyBuilderWithLocation("Links")
                            .withError("Etag does not match. etag in system: " + preexistingReport.getEtag()
                                            + " incoming etag: "
                                            + updatedReport.getEtag()
                                            + " You should GET, patch the result of the GET and UPDATE using that.")
                            .build();
            errData.addError(nonMatchingEtag);
        }
        validateEmail(errData, updatedReport.getObligedEntityEmail());
        validateStatus(errData, updatedReport.getStatus());
        return errData;
    }

    private Errors validateCreate(PscDiscrepancyReport report) {
        Errors errors = new Errors();
        validateEmail(errors, report.getObligedEntityEmail());
        return errors;
    }

    private Map<String, Object> buildErrorLogMap(Errors validationErrors) {
        Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("validationErrors", validationErrors);
        return debugMap;

    }

    private Errors validateStatus(Errors errors, String status) {
        Err error = null;
        if (status == null || status.isEmpty()) {
            error = Err.invalidBodyBuilderWithLocation(STATUS)
                            .withError(STATUS + " must not be empty or null").build();
            errors.addError(error);
        } else {
            if (!VALID_STATUSES.contains(status)) {
                error = Err.invalidBodyBuilderWithLocation(STATUS)
                                .withError(STATUS + " is not one of the correct values").build();
                errors.addError(error);
            }
        }
        return errors;
    }

    /**
     * Validate obliged entity email.
     * 
     * @param email Email to validate
     * 
     * @return Errors object containing any errors
     */
    private Errors validateEmail(Errors errors, String email) {
        Err error = null;
        if (email == null || email.isEmpty()) {
            error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                .withError(OBLIGED_ENTITY_EMAIL + " must not be empty or null").build();
            errors.addError(error);
        } else {
            String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]+$";
            
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if(!matcher.matches()) {
                error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                    .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
                errors.addError(error);
            }
        }

        return errors;
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
