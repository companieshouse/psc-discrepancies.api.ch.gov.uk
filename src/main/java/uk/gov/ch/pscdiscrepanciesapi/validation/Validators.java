package uk.gov.ch.pscdiscrepanciesapi.validation;

import java.util.Objects;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.service.rest.err.Err;
import uk.gov.companieshouse.service.rest.err.Errors;

/**
 * Common code for validators. In time, this may move to the rest service common
 * libs.
 */
@Component
public class Validators {
    
    private static final String NOT_NULL_ERROR_MESSAGE = " must not be null";
    private static final String NOT_EMPTY_ERROR_MESSAGE = " must not be empty and must not only consist of whitespace";

    public Errors validateNotBlank(String toTest, String location, Errors errs) {
        errs = validateNotNull(toTest, location, errs);
        if (!errs.hasErrors()) {
            errs = validateNotEmpty(toTest, location, errs);
        }
        return errs;
    }

    public Errors validateNotNull(Object toTest, String location, Errors errs) {
        if (toTest == null) {
            Err err = Err.invalidBodyBuilderWithLocation(location).withError(location + NOT_NULL_ERROR_MESSAGE).build();
            errs.addError(err);
        }
        return errs;
    }

    public Errors validateNotEmpty(Object toTest, String location, Errors errs) {
        if (toTest.toString().trim().isEmpty()) {
            Err err = Err.invalidBodyBuilderWithLocation(location).withError(location + NOT_EMPTY_ERROR_MESSAGE).build();
            errs.addError(err);
        }
        return errs;
    }

    public Errors validateEquals(Object expected, Object actual, String location, Errors errs) {
        if (!Objects.equals(expected, actual)) {
            Err err = Err.invalidBodyBuilderWithLocation(location).withError(location + " must equal: '" + expected + " but is: '"
                            + "'" + actual + "'").build();
            errs.addError(err);
        }
        return errs;
    }
}
