package uk.gov.ch.pscdiscrepanciesapi.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ValidatorsUnitTest {
    private static final String TO_TEST = "toTest";
    private static final String LOCATION = "location";
    private Errors errors;

    private static final String NOT_EMPTY_ERROR_MESSAGE = " must not be empty and must not only consist of whitespace";
    private static final String NOT_NULL_ERROR_MESSAGE = " must not be null";

    private Validators validators;

    @BeforeEach
    void setUp() {
        errors = new Errors();
        validators = new Validators();
    }

    @Test
    @DisplayName("Validate a string is not null and not empty successfully")
    void validateNotBlank_Successful() {
        Errors errorsFromValidation = validators.validateNotBlank(TO_TEST, LOCATION, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate a string is not null and not empty unsuccessfully - empty string")
    void validateNotBlank_Unsuccessful_EmptyString() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();

        Errors errorsFromValidation = validators.validateNotBlank("", LOCATION, errors);

        assertTrue(errorsFromValidation.hasErrors());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not null and not empty unsuccessfully - null string")
    void validateNotBlank_Unsuccessful_NullString() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_NULL_ERROR_MESSAGE).build();

        Errors errorsFromValidation = validators.validateNotBlank(null, LOCATION, errors);

        assertTrue(errorsFromValidation.hasErrors());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not null successfully")
    void validateNotNull_Successful() {
        Errors errorsFromValidation = validators.validateNotNull(TO_TEST, LOCATION, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate a string is not null unsuccessfully - null string")
    void validateNotNull_Unsuccessful() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_NULL_ERROR_MESSAGE).build();

        Errors errorsFromValidation = validators.validateNotNull(null, LOCATION, errors);

        assertTrue(errorsFromValidation.hasErrors());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not empty successfully")
    void validateNotEmpty_Successful() {
        Errors errorsFromValidation = validators.validateNotEmpty(TO_TEST, LOCATION, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate a string is not empty unsuccessfully - empty string")
    void validateNotEmpty_Unsuccessful() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();

        Errors errorsFromValidation = validators.validateNotEmpty("", LOCATION, errors);

        assertTrue(errorsFromValidation.hasErrors());
        assertTrue(errorsFromValidation.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is equal successfully")
    void validateEquals_Successful() {
        String actual = "toTest";

        Errors errorsFromValidation = validators.validateEquals(TO_TEST, actual, LOCATION, errors);

        assertFalse(errorsFromValidation.hasErrors());
    }

    @Test
    @DisplayName("Validate a string is equal unsuccessfully")
    void validateEquals_Unsuccessful() {
        String actual = "notToTest";
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION).withError(
                LOCATION + " must equal: '" + TO_TEST + " but is: '" + "'" + actual + "'").build();

        Errors errorsFromValidation = validators.validateEquals(TO_TEST, actual, LOCATION, errors);

        assertTrue(errorsFromValidation.hasErrors());
        assertTrue(errorsFromValidation.containsError(err));
    }
}
