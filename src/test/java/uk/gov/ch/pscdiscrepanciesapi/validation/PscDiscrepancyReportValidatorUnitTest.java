package uk.gov.ch.pscdiscrepanciesapi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

@ExtendWith(MockitoExtension.class)
class PscDiscrepancyReportValidatorUnitTest {

    private static final String OBLIGED_ENTITY_ORGANISATION_NAME = "obliged_entity_organisation_name";
    private static final String OBLIGED_ENTITY_CONTACT_NAME = "obliged_entity_contact_name";
    private static final String OBLIGED_ENTITY_EMAIL_LOCATION = "obliged_entity_email";
    private static final String OBLIGED_ENTITY_TELEPHONE_NUMBER_LOCATION = "obliged_entity_telephone_number";
    private static final String OBLIGED_ENTITY_TYPE = "obliged_entity_type";
    private static final String COMPANY_INCORPORATION_NUMBER_LOCATION = "company_number";
    private static final String STATUS_LOCATION = "status";


    private static final String VALID_OBLIGED_ENTITY_TYPE = "valid_oliged_entity_type";
    private static final String VALID_EMAIL = "valid_email@email.com";
    private static final String VALID_TELEPHONE_NUMBER = "08001234567";
    private static final String VALID_TYPE = "1";
    private static final String VALID_COMPANY_NUMBER = "12345678";
    private static final String VALID_STATUS = "COMPLETE";
    private static final String VALID_CONTACT_NAME = "ValidContactName";
    private static final String VALID_ORGANISATION_NAME = "ValidOrganisationName";
    private static final String ETAG = "etag";

    private static final String INVALID_ORGANISATION_NAME = "^InvalidOrganisationName^";
    private static final String INVALID_CONTACT_NAME = "^InvalidConctactName^";
    private static final String INVALID_COMPANY_NUMBER = "InvalidCompanyNumber";
    private static final String INVALID_OBLIGED_ENTITY_TYPE = "";
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
        pscDiscrepancyReport.setObligedEntityOrganisationName(VALID_ORGANISATION_NAME);
        pscDiscrepancyReport.setObligedEntityContactName(VALID_CONTACT_NAME);
        pscDiscrepancyReport.setObligedEntityEmail(VALID_EMAIL);
        pscDiscrepancyReport.setObligedEntityTelephoneNumber(VALID_TELEPHONE_NUMBER);
        pscDiscrepancyReport.setObligedEntityType(VALID_TYPE);
        pscDiscrepancyReport.setCompanyNumber(VALID_COMPANY_NUMBER);
        pscDiscrepancyReport.setStatus(VALID_STATUS);
        pscDiscrepancyReport.setEtag(ETAG);
    }

    @Nested
    class CreationTests{
        @Test
        @DisplayName("Validate successful creation of a PscDiscrepancyReport")
        void successful() {
            Errors errors = new Errors();
            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

            assertFalse(errorsFromValidation.hasErrors());
        }

        @Test
        @DisplayName("Validate unsuccessful creation of a PscDiscrepancyReport - null obliged entity type")
        void unsuccessful_NullObligedEntityType() {
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
        void unsuccessful_EmptyObligedEntityType() {
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
        @DisplayName("Validate unsuccessful creation of a PscDiscrepancyReport - Non integer obliged entity type")
        void unsuccessful_NonIntObligedEntityType() {
            Errors errors = new Errors();
            Err err = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                    .withError(OBLIGED_ENTITY_TYPE + " must be a valid integer.").build();

            pscDiscrepancyReport.setObligedEntityType("test");
            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(err));
        }

        @Test
        @DisplayName("Validate unsuccessful creation of a PscDiscrepancyReport - Out of valid range obliged entity type")
        void unsuccessful_TooLargeObligedEntityType() {
            Errors errors = new Errors();
            Err err = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                    .withError(OBLIGED_ENTITY_TYPE + " does not match a valid obliged entity.").build();

            pscDiscrepancyReport.setObligedEntityType("0");
            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validateForCreation(pscDiscrepancyReport, errors);

                assertEquals(1, errorsFromValidation.size());
                assertTrue(errorsFromValidation.containsError(err));
            }

    }

    @Nested
    class UpdateTests {
        @Test
        @DisplayName("Validate successful update of a PscDiscrepancyReport")
        void successful() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setObligedEntityEmail("updated_email@email.com");
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);
            assertFalse(errorsFromValidation.hasErrors());
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid etag")
        void unsuccessful_InvalidEtag() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);

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
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid email - invalid format")
        void unsuccessful_InvalidEmailInvalidFormat() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setObligedEntityEmail(INVALID_EMAIL);

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                    .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid email - blank")
        void unsuccessful_InvalidEmailBlank() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setObligedEntityEmail("");

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                    .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " must not be empty and must not only consist of whitespace").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid status - not a valid status")
        void unsuccessful_InvalidStatusNotAValidValue() {
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
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid status - blank")
        void unsuccessful_InvalidStatusBlank() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus("");

            Err error = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                    .withError(STATUS_LOCATION + " must not be empty and must not only consist of whitespace").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid contact name - invalid char")
        void unsuccessful_InvalidContactNameInvalidChar() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                    .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid organisation name")
        void unsuccessful_InvalidOrganisationName() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setObligedEntityOrganisationName(INVALID_ORGANISATION_NAME);

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_ORGANISATION_NAME)
                    .withError(OBLIGED_ENTITY_ORGANISATION_NAME + " contains an invalid character").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid contact name - blank")
        void unsuccessful_InvalidContactNameBlank() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setObligedEntityContactName("");
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                    .withError(OBLIGED_ENTITY_CONTACT_NAME + " must not be empty and must not only consist of whitespace").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid company number - not 8 chars")
        void unsuccessful_InvalidCompanyNumberNot8Chars() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setCompanyNumber(INVALID_COMPANY_NUMBER);

            Err error = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                    .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid company number - blank")
        void unsuccessful_InvalidCompanyNumberBlank() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setStatus(PscDiscrepancyReportValidatorUnitTest.VALID_STATUS);
            updatedReport.setCompanyNumber("");

            Err error = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                    .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must not be empty and must not only consist of whitespace").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - invalid company number, organisation name, email, status and contact name")
        void unsuccessful_InvalidCompanyNumberOrganisationNameEmailStatusAndContactName() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            updatedReport.setCompanyNumber(INVALID_COMPANY_NUMBER);
            updatedReport.setObligedEntityEmail(INVALID_EMAIL);
            updatedReport.setObligedEntityOrganisationName(INVALID_ORGANISATION_NAME);
            updatedReport.setStatus(INVALID_STATUS);
            updatedReport.setObligedEntityContactName(INVALID_CONTACT_NAME);

            Err companyNumber = Err.invalidBodyBuilderWithLocation(COMPANY_INCORPORATION_NUMBER_LOCATION)
                    .withError(COMPANY_INCORPORATION_NUMBER_LOCATION + " must be 8 characters").build();
            Err contactName = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_CONTACT_NAME)
                    .withError(OBLIGED_ENTITY_CONTACT_NAME + " contains an invalid character").build();
            Err organisationName = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_ORGANISATION_NAME)
                    .withError(OBLIGED_ENTITY_ORGANISATION_NAME + " contains an invalid character").build();
            Err status = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                    .withError(STATUS_LOCATION + " is not one of the correct values").build();
            Err email = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_EMAIL_LOCATION)
                    .withError(OBLIGED_ENTITY_EMAIL_LOCATION + " is not in the correct format").build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(5, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(organisationName));
            assertTrue(errorsFromValidation.containsError(companyNumber));
            assertTrue(errorsFromValidation.containsError(contactName));
            assertTrue(errorsFromValidation.containsError(status));
            assertTrue(errorsFromValidation.containsError(email));
        }

        @Test
        @DisplayName("Validate unsuccessful update of a PscDiscrepancyReport - null status")
        void unsuccessful_NullStatus() {
            PscDiscrepancyReport updatedReport = new PscDiscrepancyReport();
            updatedReport.setEtag(ETAG);
            Err nullStatus = Err.invalidBodyBuilderWithLocation(STATUS_LOCATION)
                    .withError(STATUS_LOCATION + NOT_NULL_ERROR_MESSAGE)
                    .build();

            Errors errorsFromValidation = pscDiscrepancyReportValidator
                    .validateForUpdate(pscDiscrepancyReport, updatedReport);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(nullStatus));
        }
    }

    @Nested
    class chipsSubmission {
        @Test
        @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS successfully")
        void successful() {
            Errors errors = new Errors();
            pscDiscrepancyReport.setStatus("COMPLETE");

            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

            assertFalse(errorsFromValidation.hasErrors());
        }

        @Test
        @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid obliged entity type")
        void unsuccessful_InvalidObligedEntityType() {
            Errors errors = new Errors();
            pscDiscrepancyReport.setObligedEntityType(INVALID_OBLIGED_ENTITY_TYPE);

            Err error = Err.invalidBodyBuilderWithLocation(OBLIGED_ENTITY_TYPE)
                    .withError(OBLIGED_ENTITY_TYPE + " must not be empty and must not only consist of whitespace").build();

            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

            assertEquals(1, errorsFromValidation.size());
            assertTrue(errorsFromValidation.containsError(error));
        }

        @Test
        @DisplayName("Validate the whole PscDiscrepancyReport (without optional OE telephone set) before submission to CHIPS successfully")
        void successfulOptionTelephoneNumberNotSet() {
            Errors errors = new Errors();
            pscDiscrepancyReport.setStatus("COMPLETE");
            pscDiscrepancyReport.setObligedEntityTelephoneNumber(null);
            Errors errorsFromValidation =
                    pscDiscrepancyReportValidator.validate(pscDiscrepancyReport, errors);

            assertFalse(errorsFromValidation.hasErrors());
        }

        @Test
        @DisplayName("Validate the whole PscDiscrepancyReport before submission to CHIPS - invalid email")
        void unsuccessful_InvalidEmail() {
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
        void unsuccessful_InvalidStatus() {
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
        void unsuccessful_InvalidContactName() {
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
        void unsuccessful_InvalidCompanyNumber() {
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
        void unsuccessful_InvalidCompanyNumberEmailStatusAndContactName() {
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
}

