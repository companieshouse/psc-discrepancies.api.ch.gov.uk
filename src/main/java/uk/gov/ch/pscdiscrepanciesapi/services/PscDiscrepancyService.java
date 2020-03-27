package uk.gov.ch.pscdiscrepanciesapi.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    private static final String DISCREPANCY_DETAILS = "details";

    @Autowired
    private PscDiscrepancyRepository pscDiscrepancyRepository;

    @Autowired
    private PscDiscrepancyMapper pscDiscrepancyMapper;

    @Autowired
    private LinkFactory linkFactory;

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

        if (pscDiscrepancy.getDetails() == null || pscDiscrepancy.getDetails().isEmpty()) {
            Errors errData = new Errors();
            Err error = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS)
                    .withError(DISCREPANCY_DETAILS + " must not be null").build();
            errData.addError(error);
            return ServiceResult.invalid(errData);
        } else {
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
    }
    
    private String createEtag() {
        return GenerateEtagUtil.generateEtag();
    }
    
    private Links linksForCreation(String pscDiscrepancyId, String pscDiscrepancyReportId) {
    	
        Links links = new Links();

        String selfLink = linkFactory.createLinkPscDiscrepancy(pscDiscrepancyId, pscDiscrepancyReportId);
        links.setLink(CoreLinkKeys.SELF, selfLink);

        String pscDiscrepancyReportLink = linkFactory.createLinkPscDiscrepancyReport(pscDiscrepancyReportId);
        links.setLink(PscDiscrepancyLinkKeys.PSC_DISCREPANCY_REPORT, pscDiscrepancyReportLink);

        return links;
    }
    
    /**
     * Create a debug map for structured logging
     * 
     * @param pscDiscrepancy
     * 
     * @return Debug map
     */
    public Map<String,Object> createPscDiscrepancyDebugMap(String pscDiscrepancyReportId, PscDiscrepancy pscDiscrepancy) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("discrepancy-report-id", pscDiscrepancyReportId);
        debugMap.put(DISCREPANCY_DETAILS, pscDiscrepancy.getDetails());
        return debugMap;
    }
}
