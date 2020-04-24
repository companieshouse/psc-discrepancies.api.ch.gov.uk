package uk.gov.ch.pscdiscrepanciesapi.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertTrue(validators.validateNotBlank(TO_TEST, LOCATION, errors));
    }

    @Test
    @DisplayName("Validate a string is not null and not empty unsuccessfully - empty string")
    void validateNotBlank_Unsuccessful_EmptyString() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();

        boolean isNotBlank = validators.validateNotBlank("", LOCATION, errors);

        assertFalse(isNotBlank);
        assertEquals(1, errors.size());
        assertTrue(errors.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not null and not empty unsuccessfully - null string")
    void validateNotBlank_Unsuccessful_NullString() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_NULL_ERROR_MESSAGE).build();

        boolean isNotBlank = validators.validateNotBlank(null, LOCATION, errors);

        assertFalse(isNotBlank);
        assertEquals(1, errors.size());
        assertTrue(errors.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not null successfully")
    void validateNotNull_Successful() {
        assertTrue(validators.validateNotNull(TO_TEST, LOCATION, errors));
    }

    @Test
    @DisplayName("Validate a string is not null unsuccessfully - null string")
    void validateNotNull_Unsuccessful() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_NULL_ERROR_MESSAGE).build();

        boolean isNotNull = validators.validateNotNull(null, LOCATION, errors);

        assertFalse(isNotNull);
        assertEquals(1, errors.size());
        assertTrue(errors.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is not empty successfully")
    void validateNotEmpty_Successful() {
        assertTrue(validators.validateNotEmpty(TO_TEST, LOCATION, errors));
    }

    @Test
    @DisplayName("Validate a string is not empty unsuccessfully - empty string")
    void validateNotEmpty_Unsuccessful() {
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION)
                .withError(LOCATION + NOT_EMPTY_ERROR_MESSAGE).build();

        boolean isNotEmpty = validators.validateNotEmpty("", LOCATION, errors);

        assertFalse(isNotEmpty);
        assertEquals(1, errors.size());
        assertTrue(errors.containsError(err));
    }

    @Test
    @DisplayName("Validate a string is equal successfully")
    void validateEquals_Successful() {
        String actual = "toTest";

        assertTrue(validators.validateEquals(TO_TEST, actual, LOCATION, errors));
    }

    @Test
    @DisplayName("Validate a string is equal unsuccessfully")
    void validateEquals_Unsuccessful() {
        String actual = "notToTest";
        Err err = Err.invalidBodyBuilderWithLocation(LOCATION).withError(
                LOCATION + " must equal: '" + TO_TEST + " but is: '" + "'" + actual + "'").build();

        boolean isEqual = validators.validateEquals(TO_TEST, actual, LOCATION, errors);

        assertFalse(isEqual);
        assertEquals(1, errors.size());
        assertTrue(errors.containsError(err));
    }
}
