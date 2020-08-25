package uk.gov.ch.pscdiscrepanciesapi.validation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ReportStatus;
import uk.gov.companieshouse.charset.CharSet;
import uk.gov.companieshouse.charset.validation.CharSetValidation;
import uk.gov.companieshouse.charset.validation.impl.CharSetValidationImpl;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@Component
public class PscDiscrepancyReportValidator extends Validators {
    private static final String OBLIGED_ENTITY_EMAIL = "obliged_entity_email";
    private static final String OBLIGED_ENTITY_CONTACT_NAME = "obliged_entity_contact_name";
    private static final String OBLIGED_ENTITY_TYPE = "obliged_entity_type";
    private static final String COMPANY_INCORPORATION_NUMBER = "company_number";
    private static final String STATUS = "status";
    private static final String ETAG = "etag";

    private CharSetValidation charSetValidator = new CharSetValidationImpl();

    private static final Set<String> VALID_STATUSES;

    static {
        Set<ReportStatus> all = EnumSet.allOf(ReportStatus.class);
        Set<String> temp = new HashSet<>();
        for (ReportStatus status : all) {
            temp.add(status.name());
        }
        VALID_STATUSES = Collections.unmodifiableSet(temp);
    }

    /**
     * Validates that a pscDiscrepancyReport has all of its mandatory fields set
     * and that those fields that are set are not set to bad values.
     * 
     * @param pscDiscrepancyReport
     * @param errs An Err instance is added to this for each validation problem.
     */
    public Errors validate(PscDiscrepancyReport pscDiscrepancyReport, Errors errs) {
        validateObligedEntityType(errs, pscDiscrepancyReport.getObligedEntityType());
        validateContactName(errs, pscDiscrepancyReport.getObligedEntityContactName());
        validateEmail(errs, pscDiscrepancyReport.getObligedEntityEmail());
        validateCompanyNumber(errs, pscDiscrepancyReport.getCompanyNumber());
        validateStatus(errs, pscDiscrepancyReport.getStatus());
        return errs;
    }

    /**
     * Validates that a pscDiscrepancyReport has all of its mandatory fields set (those that must be set at creation)
     * and that those fields that are set are not set to bad values.
     * 
     * @param pscDiscrepancyReport test subject.
     * @param errs An Err instance is added to this for each validation problem.
     */
    public Errors validateForCreation(PscDiscrepancyReport pscDiscrepancyReport, Errors errs) {
        validateObligedEntityType(errs, pscDiscrepancyReport.getObligedEntityType());
        return errs;
    }
    
    public Errors validateForUpdate(PscDiscrepancyReport preexistingReport, PscDiscrepancyReport updatedReport) {
        Errors errData = new Errors();
        if (!preexistingReport.getEtag().equals(updatedReport.getEtag())) {
            Err nonMatchingEtag = Err.invalidBodyBuilderWithLocation(ETAG)
                    .withError("Etag does not match. etag in system: " + preexistingReport.getEtag()
                            + " incoming etag: " + updatedReport.getEtag()
                            + " You should GET, patch the result of the GET and UPDATE using that.")
                    .build();
            errData.addError(nonMatchingEtag);
        }
        if(updatedReport.getObligedEntityEmail() != null) {
            validateEmail(errData, updatedReport.getObligedEntityEmail());
        }
        if(updatedReport.getStatus() != null) {
            validateStatus(errData, updatedReport.getStatus());
        }
        if(updatedReport.getObligedEntityContactName() != null) {
            validateContactName(errData, updatedReport.getObligedEntityContactName());
        }
        if(updatedReport.getCompanyNumber() != null) {
            validateCompanyNumber(errData, updatedReport.getCompanyNumber());
        }
        return errData;
    }

    private Errors validateStatus(Errors errors, String status) {

        if (validateNotBlank(status, STATUS, errors) && !VALID_STATUSES.contains(status)) {
            Err error = Err.invalidBodyBuilderWithLocation(STATUS)
                    .withError(STATUS + " is not one of the correct values").build();
            errors.addError(error);
        }
        return errors;
    }

    private Errors validateCompanyNumber(Errors errors, String companyNumber) {

        if (validateNotBlank(companyNumber, COMPANY_INCORPORATION_NUMBER, errors)
                && companyNumber.length() != 8) {
            Err error = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER)
                    .withError(COMPANY_INCORPORATION_NUMBER + " must be 8 characters").build();
            errors.addError(error);
        }
        return errors;
    }

    /**
     * Validate obliged entity contact name
     *
     * @param contactName Contact name to validate
     *
     * @return Errors object containing any errors
     */
    private Errors validateContactName(Errors errors, String contactName) {

        if (validateNotBlank(contactName, OBLIGED_ENTITY_CONTACT_NAME, errors)
                && !charSetValidator.validateCharSet(CharSet.CHARACTER_SET_2, contactName)) {
            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                    .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();
            errors.addError(error);
        }

        return errors;
    }

    /**
     * Validate obliged entity type
     *
     * @param obligedEntityType Type to validate
     *
     * @return Errors object containing any errors
     */
    private Errors validateObligedEntityType(Errors errors, String obligedEntityType) {
        validateNotBlank(obligedEntityType, OBLIGED_ENTITY_TYPE, errors);
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

        if (validateNotBlank(email, OBLIGED_ENTITY_EMAIL, errors)) {
            String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]+$";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);

            if (!matcher.matches()) {
                Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL)
                        .withError(OBLIGED_ENTITY_EMAIL + " is not in the correct format").build();
                errors.addError(error);
            }
        }

        return errors;
    }
}
