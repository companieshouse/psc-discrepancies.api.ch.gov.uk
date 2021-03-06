package uk.gov.ch.pscdiscrepanciesapi.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PscDiscrepancyValidatorUnitTest {
    private static final String DISCREPANCY_DETAILS_LOCATION = "details";
    private static final String VALID_DISCREPANCY_DETAILS = "someDetails";
    private static final String PSC_NAME_LOCATION = "psc_name";
    private static final String PSC_DOB_LOCATION = "psc_date_of_birth";
    private static final String VALID_PSC_NAME = "Name";
    private static final String VALID_PSC_DOB = "12/85";

    private static final String NOT_EMPTY_ERROR_MESSAGE = " must not be empty and must not only consist of whitespace";
    private static final String NOT_NULL_ERROR_MESSAGE = " must not be null";
    private static final String INVALID_CHAR_ERROR_MESSAGE = " contains an invalid character";

    private PscDiscrepancy pscDiscrepancy;
    private List<PscDiscrepancy> pscDiscrepancies;
    private PscDiscrepancyValidator pscDiscrepancyValidator;

    @BeforeEach
    void setUp() {
        pscDiscrepancyValidator = new PscDiscrepancyValidator();

        pscDiscrepancy = new PscDiscrepancy();
        pscDiscrepancy.setDetails(VALID_DISCREPANCY_DETAILS);
        pscDiscrepancy.setPscName(VALID_PSC_NAME);
        pscDiscrepancy.setPscDateOfBirth(VALID_PSC_DOB);
    }

    @Test
    @DisplayName("Validate successful creation of a PscDiscrepancy")
    void validateCreate_Successful() {
        Errors errors = new Errors();

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - empty details")
    void validateCreate_Unsuccessful_EmptyDetails() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS_LOCATION)
                .withError(DISCREPANCY_DETAILS_LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();
        pscDiscrepancy.setDetails("");

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - null details")
    void validateCreate_Unsuccessful_NullDetails() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS_LOCATION)
                .withError(DISCREPANCY_DETAILS_LOCATION + NOT_NULL_ERROR_MESSAGE).build();
        pscDiscrepancy.setDetails(null);

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - empty name")
    void validateCreate_Unsuccessful_EmptyName() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(PSC_NAME_LOCATION)
                .withError(PSC_NAME_LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();
        pscDiscrepancy.setPscName("");

        Errors errorsFromValidation = pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - null name")
    void validateCreate_Unsuccessful_NullName() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(PSC_NAME_LOCATION)
                .withError(PSC_NAME_LOCATION + NOT_NULL_ERROR_MESSAGE).build();
        pscDiscrepancy.setPscName(null);

        Errors errorsFromValidation = pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - invalid name")
    void validateCreate_Unsuccessful_InvalidName() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(PSC_NAME_LOCATION)
                .withError(PSC_NAME_LOCATION + INVALID_CHAR_ERROR_MESSAGE).build();
        pscDiscrepancy.setPscName("N^@!M");

        Errors errorsFromValidation = pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - null date of birth")
    void validateCreate_Unsuccessful_NullDOB() {
        Errors errors = new Errors();
        Err err = Err.invalidBodyBuilderWithLocation(PSC_DOB_LOCATION)
                .withError(PSC_DOB_LOCATION + NOT_NULL_ERROR_MESSAGE).build();
        pscDiscrepancy.setPscDateOfBirth(null);

        Errors errorsFromValidation = pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful creation of a PscDiscrepancy - invalid name, empty date of birth and empty details")
    void validateCreate_Unsuccessful_InvalidNameAndEmptyDetails() {
        Errors errors = new Errors();
        Err pscNameErr = Err.invalidBodyBuilderWithLocation(PSC_NAME_LOCATION)
                .withError(PSC_NAME_LOCATION + INVALID_CHAR_ERROR_MESSAGE).build();
        Err detailsErr = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS_LOCATION)
                .withError(DISCREPANCY_DETAILS_LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();
        Err dobErr = Err.invalidBodyBuilderWithLocation(PSC_DOB_LOCATION)
                .withError(PSC_DOB_LOCATION + NOT_NULL_ERROR_MESSAGE).build();
        pscDiscrepancy.setPscName("N^@!M");
        pscDiscrepancy.setDetails("");
        pscDiscrepancy.setPscDateOfBirth(null);

        Errors errorsFromValidation = pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(3, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(pscNameErr));
        assertTrue(errorsFromValidation.containsError(detailsErr));
        assertTrue(errorsFromValidation.containsError(dobErr));
    }

    @Test
    @DisplayName("Validate successful submission of PscDiscrepancyDetails")
    void validateOnSubmission_Successful() {
        Errors errors = new Errors();
        pscDiscrepancies = new ArrayList<>();
        pscDiscrepancies.add(pscDiscrepancy);

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateOnSubmission(pscDiscrepancies, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate unsuccessful submission of PscDiscrepancyDetails - empty discrepancy list")
    void validateOnSubmission_Unsuccessful_EmptyDiscrepancyList() {
        Errors errors = new Errors();
        pscDiscrepancies = new ArrayList<>();
        Err err =
                Err.serviceErrBuilder().withError("No discrepancies could be found for the report").build();

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateOnSubmission(pscDiscrepancies, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful submission of PscDiscrepancyDetails - empty discrepancy in list")
    void validateOnSubmission_Unsuccessful_EmptyDiscrepancyInList() {
        Errors errors = new Errors();
        pscDiscrepancies = new ArrayList<>();
        pscDiscrepancy.setDetails("");
        pscDiscrepancies.add(pscDiscrepancy);

        Err err = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS_LOCATION)
                .withError(DISCREPANCY_DETAILS_LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate unsuccessful submission of PscDiscrepancyDetails - null discrepancy in list")
    void validateOnSubmission_Unsuccessful_NullDiscrepancyInList() {
        Errors errors = new Errors();
        pscDiscrepancies = new ArrayList<>();
        pscDiscrepancy.setDetails(null);
        pscDiscrepancies.add(pscDiscrepancy);

        Err err = Err.invalidBodyBuilderWithLocation(DISCREPANCY_DETAILS_LOCATION)
                .withError(DISCREPANCY_DETAILS_LOCATION + NOT_NULL_ERROR_MESSAGE).build();

        Errors errorsFromValidation =
                pscDiscrepancyValidator.validateForCreation(pscDiscrepancy, errors);

        assertEquals(1, errorsFromValidation.size());
        assertTrue(errorsFromValidation.containsError(err));
    }
}
