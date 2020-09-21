package uk.gov.ch.pscdiscrepanciesapi.validation;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.companieshouse.charset.CharSet;
import uk.gov.companieshouse.charset.validation.CharSetValidation;
import uk.gov.companieshouse.charset.validation.impl.CharSetValidationImpl;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@Component
public class PscDiscrepancyValidator extends Validators {
    
    private static final String DISCREPANCY_DETAILS = "details";
    private static final String PSC_DOB = "psc_date_of_birth";
    private static final String PSC_NAME = "psc_name";

    private CharSetValidation charSetValidator = new CharSetValidationImpl();

    /**
     * Validates that a pscDiscrepancyReport has all of its mandatory fields set and that those fields
     * that are set are not set to bad values.
     * 
     * @param pscDiscrepancy test subject.
     * @param errs An Err instance is added to this for each validation problem.
     */
    public Errors validateForCreation(PscDiscrepancy pscDiscrepancy, Errors errs) {
        validatePscName(errs, pscDiscrepancy.getPscName());
        validateNotBlank(pscDiscrepancy.getPscDateOfBirth(), PSC_DOB, errs);
        validateNotBlank(pscDiscrepancy.getDetails(), DISCREPANCY_DETAILS, errs);
        return errs;
    }

    public Errors validateOnSubmission(List<PscDiscrepancy> pscDiscrepancies, Errors errs) {
        if (pscDiscrepancies.isEmpty()) {
            Err err = Err.serviceErrBuilder().withError("No discrepancies could be found for the report").build();
            errs.addError(err);
        } else {
            pscDiscrepancies.forEach(d -> validateForCreation(d, errs));
        }
        return errs;
    }

    private Errors validatePscName(Errors errors, String pscName) {
        if (validateNotBlank(pscName, PSC_NAME, errors)
                && !charSetValidator.validateCharSet(CharSet.CHARACTER_SET_2, pscName)) {
            Err error = Err.invalidBodyBuilderWithLocation(PSC_NAME)
                    .withError(PSC_NAME + " contains an invalid character").build();
            errors.addError(error);
        }

        return errors;
    }
}
