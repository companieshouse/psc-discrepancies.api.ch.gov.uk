package uk.gov.ch.pscdiscrepanciesapi.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.common.Kind;
import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.common.PscSubmissionSender;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyReportMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyReportEntityData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ReportStatus;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyReportRepository;
import uk.gov.ch.pscdiscrepanciesapi.validation.PscDiscrepancyReportValidator;
import uk.gov.ch.pscdiscrepanciesapi.validation.PscDiscrepancyValidator;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.service.ServiceException;
import uk.gov.companieshouse.service.ServiceResult;
import uk.gov.companieshouse.service.links.CoreLinkKeys;
import uk.gov.companieshouse.service.links.Links;
import uk.gov.companieshouse.service.rest.err.Errors;

@Service
public class PscDiscrepancyReportService {

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    private final PscDiscrepancyReportRepository pscDiscrepancyReportRepository;

    private final PscDiscrepancyReportMapper pscDiscrepancyReportMapper;
    
    private final PscDiscrepancyService pscDiscrepancyService;

    private final LinkFactory linkFactory;
    private final PscSubmissionSender pscSubmissionSender;

    @Autowired
    private PscDiscrepancyReportValidator pscDiscrepancyReportValidator;

    @Autowired
    private PscDiscrepancyValidator pscDiscrepancyValidator;

    public PscDiscrepancyReportService(@Autowired PscDiscrepancyReportRepository pscDiscrepancyReportRepository,
            @Autowired PscDiscrepancyReportMapper pscDiscrepancyReportMapper,
            @Autowired PscSubmissionSender pscSubmissionSender, @Autowired PscDiscrepancyService pscDiscrepancyService,
            @Autowired LinkFactory linkFactory) {
        this.pscDiscrepancyReportRepository = pscDiscrepancyReportRepository;
        this.pscDiscrepancyReportMapper = pscDiscrepancyReportMapper;
        this.pscSubmissionSender = pscSubmissionSender;
        this.pscDiscrepancyService = pscDiscrepancyService;
        this.linkFactory = linkFactory;
    }

    public PscDiscrepancyReport findPscDiscrepancyReportById(String reportId) {
        Optional<PscDiscrepancyReportEntity> storedReport = pscDiscrepancyReportRepository.findById(reportId);

        return storedReport
                .map(pscDiscrepancyReportEntity -> pscDiscrepancyReportMapper.entityToRest(pscDiscrepancyReportEntity))
                .orElse(null);
    }

    /**
     * Create a PSC discrepancy report.
     * 
     * @param pscDiscrepancyReport Report details to be stored
     * @param request              Http request
     * 
     * @return ServiceResult object with created PSC discrepancy report
     * 
     * @throws ServiceException
     */
    public ServiceResult<PscDiscrepancyReport> createPscDiscrepancyReport(PscDiscrepancyReport pscDiscrepancyReport,
            HttpServletRequest request) throws ServiceException {

        Errors validationErrors = pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, new Errors());

        if (validationErrors.hasErrors()) {
            LOG.error("Validation errors", buildErrorLogMap(validationErrors));
            return ServiceResult.invalid(validationErrors);
        }

        try {
            PscDiscrepancyReportEntity reportToStore = pscDiscrepancyReportMapper.restToEntity(pscDiscrepancyReport);

            String pscDiscrepancyReportId = UUID.randomUUID().toString();
            reportToStore.setId(pscDiscrepancyReportId);
            reportToStore.setCreatedAt(LocalDateTime.now());
            reportToStore.getData().setKind(Kind.PSC_DISCREPANCY_REPORT);
            reportToStore.getData().setEtag(createEtag());
            reportToStore.getData().setLinks(linksForCreation(pscDiscrepancyReportId));

            PscDiscrepancyReportEntity storedReport = pscDiscrepancyReportRepository.insert(reportToStore);

            PscDiscrepancyReport reportToReturn = pscDiscrepancyReportMapper.entityToRest(storedReport);

            return ServiceResult.created(reportToReturn);
        } catch (MongoException me) {
            ServiceException serviceException = new ServiceException("Exception storing PSC discrepancy report: ", me);
            LOG.errorRequest(request, serviceException, createPscDiscrepancyReportDebugMap(pscDiscrepancyReport));
            throw serviceException;
        }
    }

    /**
     * Updates the report with ID reportId, but only after running validation
     * checks. If the report cannot be found, this returns
     * {@link ServiceResult#notFound()}. If there is one or more validation
     * failures, this returns {@link ServiceResult#invalid(Errors)} containing
     * details of all the problems found.
     * 
     * @param reportId                 ID of the report to update.
     * @param reportWithUpdatesToApply Contains the changes
     * @param request                  The original request that lead to this method
     *                                 being called.
     * @return The updated and stored report, with a new etag.
     * @throws ServiceException If an unexpected problem is encountered, e.g. the
     *                          underlying database throws an exception.
     */
    public ServiceResult<PscDiscrepancyReport> updatePscDiscrepancyReport(String reportId,
            PscDiscrepancyReport reportWithUpdatesToApply, HttpServletRequest request) throws ServiceException {
        final ServiceResult<PscDiscrepancyReport> reportToReturn;
        try {
            Optional<PscDiscrepancyReportEntity> queryResult = pscDiscrepancyReportRepository.findById(reportId);
            if (!queryResult.isPresent()) {
                reportToReturn = ServiceResult.notFound();
            } else {
                PscDiscrepancyReportEntity preexistingReportEntity = queryResult.get();
                PscDiscrepancyReport preexistingReport = pscDiscrepancyReportMapper
                        .entityToRest(preexistingReportEntity);

                Errors validationErrors = pscDiscrepancyReportValidator.validateForUpdate(preexistingReport, reportWithUpdatesToApply);
                
                if (validationErrors.hasErrors()) {
                    LOG.error("Validation errors", buildErrorLogMap(validationErrors));
                    reportToReturn = ServiceResult.invalid(validationErrors);
                } else {
                    PscDiscrepancyReportEntityData preexistingReportEntityData = preexistingReportEntity.getData();
                    // Now copy over all values that are allowed to be updated
                    // We do this to prevent malicious/inadvertent changing of values that must
                    // not be set by anything other than this service, e.g. kind, links, etag...
                    preexistingReportEntityData.setStatus(reportWithUpdatesToApply.getStatus());
                    preexistingReportEntityData.setObligedEntityEmail(
                                    reportWithUpdatesToApply.getObligedEntityEmail());
                    preexistingReportEntityData
                                    .setCompanyNumber(reportWithUpdatesToApply.getCompanyNumber());
                    preexistingReportEntityData.setObligedEntityContactName(
                            reportWithUpdatesToApply.getObligedEntityContactName());
                    // Update the etag value, as this has changed
                    preexistingReportEntityData.setEtag(createEtag());

                    PscDiscrepancyReportEntity storedReportEntity = pscDiscrepancyReportRepository
                            .save(preexistingReportEntity);

                    PscDiscrepancyReport storedReport = pscDiscrepancyReportMapper.entityToRest(storedReportEntity);
                    reportToReturn = ServiceResult.updated(storedReport);

                    if (storedReport.getStatus().equals(ReportStatus.COMPLETE.toString())) {
                        onReportCompleted(storedReport, storedReportEntity, request, reportId);
                    }
                }
            }
        } catch (MongoException me) {
            ServiceException serviceException = new ServiceException("Exception storing PSC discrepancy report: ", me);
            LOG.errorRequest(request, serviceException, createPscDiscrepancyReportDebugMap(reportWithUpdatesToApply));
            throw serviceException;
        }
        return reportToReturn;
    }

    private Map<String, Object> buildErrorLogMap(Errors validationErrors) {
        Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("validationErrors", validationErrors);
        return debugMap;

    }

    private String createEtag() {
        return GenerateEtagUtil.generateEtag();
    }

    private Map<String, String> linksForCreation(String pscDiscrepancyReportId) {
        Links links = new Links();

        String selfLink = linkFactory.createLinkPscDiscrepancyReport(pscDiscrepancyReportId);
        links.setLink(CoreLinkKeys.SELF, selfLink);

        return links.getLinks();
    }

    /**
     * Create a debug map for structured logging
     * 
     * @param pscDiscrepancyReport
     * 
     * @return Debug map
     */
    private Map<String, Object> createPscDiscrepancyReportDebugMap(PscDiscrepancyReport pscDiscrepancyReport) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("obliged_entity_contact_name", pscDiscrepancyReport.getObligedEntityContactName());
        debugMap.put("obliged_entity_email", pscDiscrepancyReport.getObligedEntityEmail());
        debugMap.put("company_number", pscDiscrepancyReport.getCompanyNumber());
        debugMap.put("status", pscDiscrepancyReport.getStatus());
        return debugMap;
    }

    private void onReportCompleted(PscDiscrepancyReport storedReport, PscDiscrepancyReportEntity storedReportEntity,
            HttpServletRequest request, String reportId) {
        boolean reportSent = false;
        
        Errors validationErrors = new Errors();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Validate everything that needs to be sent to CHIPS
            validationErrors = pscDiscrepancyReportValidator.validate(storedReport, new Errors());
            PscSubmission reportToSubmit = new PscSubmission();
            ServiceResult<List<PscDiscrepancy>> reportDetails = pscDiscrepancyService.getDiscrepancies(reportId,
                    request);
            pscDiscrepancyValidator.validateOnSubmission(reportDetails.getData(), validationErrors);

            if (!validationErrors.hasErrors()) {
                reportToSubmit.setReport(storedReport);
                reportToSubmit.setDiscrepancies(reportDetails.getData());
                reportSent = pscSubmissionSender.send(reportToSubmit, httpClient, request.getSession().getId());
            }
        } catch (ServiceException ex) {
            LOG.error("ERROR Sending JSON to CHIPS Rest Interfaces ", ex);
        } catch (IOException e) {
            LOG.error("ERROR closing client when sending JSON to CHIPS Rest Interfaces ", e);
        }

        try {
            PscDiscrepancyReportEntityData sentReportEntityData = storedReportEntity.getData();
            if(validationErrors.hasErrors()) {
                LOG.error("Validation errors", buildErrorLogMap(validationErrors));
                sentReportEntityData.setStatus(ReportStatus.INVALID.toString());
            } else if (reportSent) {
                sentReportEntityData.setStatus(ReportStatus.SUBMITTED.toString());
            } else {
                sentReportEntityData.setStatus(ReportStatus.FAILED_TO_SUBMIT.toString());
            }
            pscDiscrepancyReportRepository.save(storedReportEntity);
        } catch (MongoException mongoEx) {
            LOG.error("Error saving report with new status after attempting to submit report, with reportSent: "
                    + reportSent, mongoEx);
        }
    }
}
