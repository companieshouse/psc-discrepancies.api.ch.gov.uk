package uk.gov.ch.pscdiscrepanciesapi.validation;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.ch.pscdiscrepanciesapi.common.Validators;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@Component
public class PscDiscrepancyValidator extends Validators {
    
    private static final String DISCREPANCY_DETAILS = "details";
    
    /**
     * Validates that a pscDiscrepancyReport has all of its mandatory fields set and that those fields
     * that are set are not set to bad values.
     * 
     * @param pscDiscrepancy test subject.
     * @param errs An Err instance is added to this for each validation problem.
     */
    public Errors validateForCreation(PscDiscrepancy pscDiscrepancy, Errors errs) {
        return validateNotBlank(pscDiscrepancy.getDetails(), DISCREPANCY_DETAILS, errs);
    }
    
    public Errors validateOnSubmission(List<PscDiscrepancy> pscDiscrepancies, Errors errs) {
        if (pscDiscrepancies.isEmpty()) {
            Err err = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS).withError(" no discrepancies for report").build();
            errs.addError(err);
        } else {
            pscDiscrepancies.forEach(d -> {validateForCreation(d, errs);});
        }
        return errs;
    }
}
