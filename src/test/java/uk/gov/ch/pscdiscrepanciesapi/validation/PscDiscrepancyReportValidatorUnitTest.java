package uk.gov.ch.pscdiscrepanciesapi.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyReportValidatorUnitTest {
    private static final String OBLIGED_ENTITY_CONTACT_NAME = "obliged_entity_contact_name";
    private static final String OBLIGED_ENTITY_EMAIL_LOCATION = "obliged_entity_email";
    private static final String OBLIGED_ENTITY_TELEPHONE_NUMBER_LOCATION =
            "obliged_entity_telephone_number";
    private static final String OBLIGED_ENTITY_TYPE = "obliged_entity_type";
    private static final String COMPANY_INCORPORATION_NUMBER_LOCATION = "company_number";
    private static final String STATUS_LOCATION = "status";


    private static final String VALID_OBLIGED_ENTITY_TYPE = "valid_oliged_entity_type";
    private static final String VALID_EMAIL = "valid_email@email.com";
    private static final String VALID_TELEPHONE_NUMBER = "08001234567";
    private static final String VALID_TYPE = "obliged_entity_type";
    private static final String VALID_COMPANY_NUMBER = "12345678";
    private static final String VALID_STATUS = "COMPLETE";
    private static final String VALID_CONTACT_NAME = "ValidContactName";
    private static final String ETAG = "etag";

    private static final String INVALID_CONTACT_NAME = "^InvalidConctactName^";
    private static final String INVALID_COMPANY_NUMBER = "InvalidCompanyNumber";
    private static final String INVALID_OBLIGED_ENTITY_TYPE = "InvalidObligedEntityType";
    private static final String INVALID_STATUS = "NOT_A_VALID_STATUS";
    private static final String INVALID_EMAIL = "Invalid_Email";

    private static final String NOT_NULL_ERROR_MESSAGE = " must not be null";
    private static final String NOT_EMPTY_ERROR_MESSAGE =
            " must not be empty and must not only consist of whitespace";

    private PscDiscrepancyReport pscDiscrepancyReport;
    private PscDiscrepancyReportValidator pscDiscrepancyReportValidator;

    @BeforeEach
    void setUp() {
        pscDiscrepancyReportValidator = new PscDiscrepancyReportValidator();

        pscDiscrepancyReport = new PscDiscrepancyReport();
        pscDiscrepancyReport.setObligedEntityContactName(VALID_CONTACT_NAME);
        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReport.setObligedEntityTelephoneNumber(VALID_TELEPHONE_NUMBER);
        pscDiscrepancyReport.setObligedEntityType(VALID_TYPE);
        pscDiscrepancyReport.setCompanyNumber(VALID_COMPANY_NUMBER);
        pscDiscrepancyReport.setStatus(VALID_STATUS);
        pscDiscrepancyReport.setEtag(ETAG);
    }

    @Test
    @DisplayName("Validate successful creation of a PscDiscrepancyReport")
    void validateCreate_Successful() {
        Errors errors = new Errors();
        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancyReport - null obliged entity type")
    void validateCreate_Unsuccessful_NullObligedEntityType() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                .withError(OBLIGED_ENTITY_TYPE + NOT_NULL_ERROR_MESSAGE).build();

        pscDiscrepancyReport.setObligedEntityType(null);
        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancyReport - empty obliged entity type")
    void validateCreate_Unsuccessful_EmptyObligedEntityType() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                .withError(OBLIGED_ENTITY_TYPE + NOT_EMPTY_ERROR_MESSAGE).build();

        pscDiscrepancyReport.setObligedEntityType("");
        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate successful update of a PscDiscrepancyReport")
    void validateUpdate_Successful() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setObligedEntityEmail("updated_email@email.com");
        updatedReport.setEtag(ETAG);

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);
        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid etag")
    void validateUpdate_Unsuccessful_InvalidEtag() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();

        Err nonMatchingEtag = Err.invalidBodyBuilderWithLocation("etag").withError(
                "Etag does not match. etag in system: " + pscDiscrepancyReport.getEtag()
                        + " incoming etag: " + updatedReport.getEtag()
                        + " You should GET, patch the result of the GET and UPDATE using that.")
                .build();


        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(nonMatchingEtag));
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid email")
    void validateUpdate_Unsuccessful_InvalidEmail() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setEtag(ETAG);
        updatedReport.setObligedEntityEmail(INVALID_EMAIL);

        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid status")
    void validateUpdate_Unsuccessful_InvalidStatus() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setEtag(ETAG);
        updatedReport.setStatus(INVALID_STATUS);

        Err error = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                .withError(STATUS_LOCATION + " is not one of the correct values").build();

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid contact name")
    void validateUpdate_Unsuccessful_InvalidContactName() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setEtag(ETAG);
        updatedReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid company number")
    void validateUpdate_Unsuccessful_InvalidCompanyNumber() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setEtag(ETAG);
        updatedReport.setCompanyNumber(INVALID_COMPANY_NUMBER);

        Err error = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid company number, email, status and contact name")
    void validateUpdate_Unsuccessful_InvalidCompanyNumberEmailStatusAndContactName() {
        PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
        updatedReport.setEtag(ETAG);
        updatedReport.setCompanyNumber(INVALID_COMPANY_NUMBER);
        updatedReport.setObligedEntityEmail(INVALID_EMAIL);
        updatedReport.setStatus(INVALID_STATUS);
        updatedReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

        Err companyNumber = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();
        Err contactName = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();
        Err status = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                .withError(STATUS_LOCATION + " is not one of the correct values").build();
        Err email = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

        Errors errorsFromValidation = pscDiscrepancyReportValidator
                .validateForUpdate(pscDiscrepancyReport, updatedReport);

        assertEquals(4, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(companyNumber));
        assertTrue(errorsFromValidation.containsError(contactName));
        assertTrue(errorsFromValidation.containsError(status));
        assertTrue(errorsFromValidation.containsError(email));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS successfully")
    void validateReport_Successful() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setStatus("COMPLETE");

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid obliged entity type")
    void validateReport_Unsuccessful_InvalidObligedEntityType() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setObligedEntityType(INVALID_OBLIGED_ENTITY_TYPE);

        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                .withError(OBLIGED_ENTITY_TYPE + " must must not be null").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport (without optional OE telephone set) before submission to CHIPS successfully")
    void validateReport_SuccessfulOptionTelephoneNumberNotSet() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setStatus("COMPLETE");
        pscDiscrepancyReport.setObligedEntityTelephoneNumber(null);
        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid email")
    void validateReport_Unsuccessful_InvalidEmail() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setObligedEntityEmail(INVALID_EMAIL);

        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid status")
    void validateReport_Unsuccessful_InvalidStatus() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setStatus(INVALID_STATUS);

        Err error = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                .withError(STATUS_LOCATION + " is not one of the correct values").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid contact name")
    void validateReport_Unsuccessful_InvalidContactName() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

        Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid company number")
    void validateReport_Unsuccessful_InvalidCompanyNumber() {
        Errors errors = new Errors();
        pscDiscrepancyReport.setCompanyNumber(INVALID_COMPANY_NUMBER);

        Err error = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(error));
    }

    @Test
    @DisplayName("Validate the whole PscDiscrepanctReport before submission to CHIPS - invalid company number, email, status and contact name")
    void validateReport_Unsuccessful_InvalidCompanyNumberEmailStatusAndContactName() {
        Errors errors = new Errors();

        pscDiscrepancyReport.setCompanyNumber(INVALID_COMPANY_NUMBER);
        pscDiscrepancyReport.setObligedEntityEmail(INVALID_EMAIL);
        pscDiscrepancyReport.setObligedEntityTelephoneNumber("");
        pscDiscrepancyReport.setStatus(INVALID_STATUS);
        pscDiscrepancyReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

        Err companyNumber = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();
        Err contactName = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();
        Err status = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                .withError(STATUS_LOCATION + " is not one of the correct values").build();
        Err email = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

        Errors errorsFromValidation =
                pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

        assertEquals(4, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(companyNumber));
        assertTrue(errorsFromValidation.containsError(contactName));
        assertTrue(errorsFromValidation.containsError(status));
        assertTrue(errorsFromValidation.containsError(email));
    }
}
