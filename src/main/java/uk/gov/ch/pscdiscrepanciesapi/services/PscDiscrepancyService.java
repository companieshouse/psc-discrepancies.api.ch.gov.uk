package uk.gov.ch.pscdiscrepanciesapi.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.common.Kind;
import uk.gov.ch.pscdiscrepanciesapi.common.LinkFactory;
import uk.gov.ch.pscdiscrepanciesapi.common.PscDiscrepancyLinkKeys;
import uk.gov.ch.pscdiscrepanciesapi.mappers.PscDiscrepancyMapper;
import uk.gov.ch.pscdiscrepanciesapi.models.entity.PscDiscrepancyEntity;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.repositories.PscDiscrepancyRepository;
import uk.gov.ch.pscdiscrepanciesapi.validation.PscDiscrepancyValidator;
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
public class PscDiscrepancyService {

    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private static final String DISCREPANCY_DETAILS = "details";
    private static final String DISCREPANCY_ID = "discrepancy-id";
    private static final String DISCREPANCY_REPORT_ID = "discrepancy-report-id";
    private static final String REPORT_URI = "/psc-discrepancy-reports/";

    @Autowired
    private PscDiscrepancyRepository pscDiscrepancyRepository;

    @Autowired
    private PscDiscrepancyMapper pscDiscrepancyMapper;

    @Autowired
    private LinkFactory linkFactory;

    @Autowired
    private PscDiscrepancyValidator validator;

    /**
     * Create a PSC Discrepancy record.
     * 
     * @param pscDiscrepancy PSC Discrepancy from UI
     * @param pscDiscrepancyReportId
     * @param request
     * 
     * @return PSC Discrepancy record that was created
     * @throws ServiceException
     */
    public ServiceResult<PscDiscrepancy> createPscDiscrepancy(PscDiscrepancy pscDiscrepancy,
            String pscDiscrepancyReportId, HttpServletRequest request) throws ServiceException {

        Errors validationErrors = validator.validateForCreation(pscDiscrepancy, new Errors());
        if (validationErrors.hasErrors()) {
            LOG.error("Validation errors", createPscDiscrepancyDebugMap(pscDiscrepancyReportId, pscDiscrepancy));
            return ServiceResult.invalid(createErrors(DISCREPANCY_DETAILS, MUST_NOT_BE_NULL));
        }
        
        try {
            PscDiscrepancyEntity pscDiscrepancyEntity =
                    pscDiscrepancyMapper.restToEntity(pscDiscrepancy);

            String pscDiscrepancyId = UUID.randomUUID().toString();
            pscDiscrepancyEntity.setId(pscDiscrepancyId);
            pscDiscrepancyEntity.setCreatedAt(LocalDateTime.now());
            pscDiscrepancyEntity.getData().setKind(Kind.PSC_DISCREPANCY);
            pscDiscrepancyEntity.getData().setEtag(createEtag());
            pscDiscrepancyEntity.getData()
                    .setLinks(linksForCreation(pscDiscrepancyId, pscDiscrepancyReportId));

            PscDiscrepancyEntity createdPscDiscrepancyEntity =
                    pscDiscrepancyRepository.insert(pscDiscrepancyEntity);

            PscDiscrepancy createdPscDiscrepancy =
                    pscDiscrepancyMapper.entityToRest(createdPscDiscrepancyEntity);

            return ServiceResult.created(createdPscDiscrepancy);
        } catch (MongoException me) {
            ServiceException serviceException =
                    new ServiceException("Exception storing PSC discrepancy: ", me);
            LOG.errorRequest(request, serviceException,
                    createPscDiscrepancyDebugMap(pscDiscrepancyReportId, pscDiscrepancy));
            throw serviceException;
        }
    }

    /**
     * Get a single PSC Discrepancy details record by ID
     * 
     * @param pscDiscrepancyId
     * @param request
     * @return the PSC Discrepancy which has the matching ID
     * @throws ServiceException
     */
    public ServiceResult<PscDiscrepancy> getDiscrepancy(String pscDiscrepancyReportId, String pscDiscrepancyId, HttpServletRequest request) throws ServiceException {
        if (pscDiscrepancyId == null || pscDiscrepancyId.isEmpty()) {
            return ServiceResult.invalid(createErrors(DISCREPANCY_ID, MUST_NOT_BE_NULL));
        }
        try {
            Optional<PscDiscrepancyEntity> storedDiscrepancy = pscDiscrepancyRepository.findById(pscDiscrepancyId);
            if (storedDiscrepancy.isPresent()) {
                PscDiscrepancy pscDiscrepancy = storedDiscrepancy
                        .map(pscDiscrepancyEntity -> pscDiscrepancyMapper.entityToRest(pscDiscrepancyEntity))
                        .orElse(null);
                return ServiceResult.found(pscDiscrepancy);
            } else {
                return ServiceResult.notFound();
            }
        } catch (MongoException me) {
            ServiceException serviceException = new ServiceException("Exception retrieving PSC discrepancy: ", me);
            LOG.errorRequest(request, serviceException, createDebugMapWithoutDiscrepancyObject(pscDiscrepancyReportId, pscDiscrepancyId));
            throw serviceException;
        }
    }

    /**
     * Get all discrepancies which exist for a given report
     * 
     * @param pscDiscrepancyReportId the ID of the report
     * @return the List of PSC Discrepancies
     * @throws ServiceException
     */
    public ServiceResult<List<PscDiscrepancy>> getDiscrepancies(String pscDiscrepancyReportId, HttpServletRequest request) throws ServiceException {
        if (pscDiscrepancyReportId == null || pscDiscrepancyReportId.isEmpty()) {
            return ServiceResult.invalid(createErrors(DISCREPANCY_REPORT_ID, MUST_NOT_BE_NULL));
        }
        try {
            List<PscDiscrepancyEntity> storedDiscrepancies = pscDiscrepancyRepository
                    .getDiscrepancies(REPORT_URI + pscDiscrepancyReportId);
            if (storedDiscrepancies != null && !storedDiscrepancies.isEmpty()) {
                List<PscDiscrepancy> retrievedDiscrepancies = new ArrayList<>();
                for (PscDiscrepancyEntity pscDiscrepancyEntity : storedDiscrepancies) {
                    retrievedDiscrepancies.add(pscDiscrepancyMapper.entityToRest(pscDiscrepancyEntity));
                }
                return ServiceResult.found(retrievedDiscrepancies);
            } else {
                return ServiceResult.notFound();
            }
        } catch (MongoException me) {
            ServiceException serviceException = new ServiceException("Exception retrieving PSC discrepancy: ", me);
            LOG.errorRequest(request, serviceException, createDebugMapWithoutDiscrepancyObject(pscDiscrepancyReportId, null));
            throw serviceException;
        }
    }

    private String createEtag() {
        return GenerateEtagUtil.generateEtag();
    }

    
    /**
     * Create links for a PSC discrepancy
     * 
     * @param pscDiscrepancyId ID of PSC discrepancy
     * @param pscDiscrepancyReportId ID of PSC discrepancy report
     * 
     * @return a Links object containing links to self and parent
     */
    private Map<String, String> linksForCreation(String pscDiscrepancyId, String pscDiscrepancyReportId) {

        Links links = new Links();

        String selfLink = linkFactory.createLinkPscDiscrepancy(pscDiscrepancyId, pscDiscrepancyReportId);
        links.setLink(CoreLinkKeys.SELF, selfLink);

        String pscDiscrepancyReportLink = linkFactory.createLinkPscDiscrepancyReport(pscDiscrepancyReportId);
        links.setLink(PscDiscrepancyLinkKeys.PSC_DISCREPANCY_REPORT, pscDiscrepancyReportLink);

        return links.getLinks();
    }

    /**
     * Create a debug map for structured logging
     * 
     * @param pscDiscrepancy
     * 
     * @return Debug map
     */
    public Map<String, Object> createPscDiscrepancyDebugMap(String pscDiscrepancyReportId,
            PscDiscrepancy pscDiscrepancy) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(DISCREPANCY_REPORT_ID, pscDiscrepancyReportId);
        debugMap.put(DISCREPANCY_DETAILS, pscDiscrepancy.getDetails());
        return debugMap;
    }
    
    /**
     * Create a debug map for structured logging when there is no PscDiscrepancy object
     * @param pscDiscrepancyReportId the id of the psc discrepancy report
     * @param pscDiscrepancyId the id of the psc discrepancy details
     * @return
     */
    public Map<String, Object> createDebugMapWithoutDiscrepancyObject(String pscDiscrepancyReportId, String pscDiscrepancyId){
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(DISCREPANCY_REPORT_ID, pscDiscrepancyReportId);
        if(pscDiscrepancyId != null) {
            debugMap.put(DISCREPANCY_ID, pscDiscrepancyId);
        }
        return debugMap;
    }

    /**
     * Create an error object
     * 
     * @param field   the field that is causing the error
     * @param message the message about the field
     * @return the completed Errors object
     */
    private Errors createErrors(String field, String message) {
        Errors errData = new Errors();
        Err error = Err.invalidBodyBuilderWithLocation(field).withError(field + message).build();
        errData.addError(error);
        return errData;
    }
}
