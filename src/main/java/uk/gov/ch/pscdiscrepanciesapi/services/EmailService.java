package uk.gov.ch.pscdiscrepanciesapi.services;

import avro.shaded.com.google.common.base.Optional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.models.email.ReportConfirmationEmailData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.email_producer.EmailProducer;
import uk.gov.companieshouse.email_producer.EmailSendingException;
import uk.gov.companieshouse.email_producer.model.EmailData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class EmailService {

    public static final String SUBJECT = "Confirmation of PSC Discrepancy Report";
    private final EmailProducer emailProducer;
    private static final String REPORT_SUBMISSION_CONFIRMATION = "psc_discrepancies_submission_confirmation";
    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);
    private final ApiClient httpClient;
    private ApiResponse<CompanyProfileApi> company;

    @Autowired
    public EmailService( EmailProducer emailProducer, ApiClient httpClient) {
        this.emailProducer = emailProducer;
        this.httpClient = httpClient;
    }

    public void sendConfirmation(PscSubmission submission)
            throws ApiErrorResponseException, URIValidationException {

        company = httpClient
                .company()
                .get(String.format("/company/%s", submission.getReport().getCompanyNumber().toUpperCase(Locale.ROOT)))
                .execute();

        for(PscDiscrepancy pscDiscrepancy: submission.getDiscrepancies()){
            sendEmailForPSCDiscrepancy(submission.getReport(), pscDiscrepancy);
        }
    }

    private void sendEmailForPSCDiscrepancy(PscDiscrepancyReport report, PscDiscrepancy pscDiscrepancy) {
        final ReportConfirmationEmailData emailData = new ReportConfirmationEmailData();

        emailData.setTo(report.getObligedEntityEmail());
        emailData.setSubject(SUBJECT);
        emailData.setCompanyName(company.getData().getCompanyName());
        emailData.setCompanyNumber(report.getCompanyNumber());
        emailData.setPscName(pscDiscrepancy.getPscName());

        List<String> types = Optional.fromNullable(pscDiscrepancy.getPscDiscrepancyTypes()).or(Collections.emptyList());
        emailData.setPscTypes(types.toArray(new String[0]));
        emailData.setReferenceNumber(report.getSubmissionReference());
        emailData.setPscMoreInformation(pscDiscrepancy.getDetails());

        LOG.info("Sending Email", toHashMap(emailData));

        sendEmail(emailData, REPORT_SUBMISSION_CONFIRMATION);
    }

    private Map<String, Object> toHashMap(ReportConfirmationEmailData emailData) {
        HashMap<String, Object> ret = new HashMap<>();
        ret.put("COMPANY NAME", emailData.getCompanyName());
        ret.put("COMPANY NUMBER", emailData.getCompanyNumber());
        ret.put("PSC NAME", emailData.getPscName());
        ret.put("PSC EXTRA INFO", emailData.getPscMoreInformation());
        ret.put("REFERENCE NUMBER", emailData.getReferenceNumber());
        ret.put("TO", emailData.getTo());
        ret.put("SUBJECT", emailData.getSubject());
        return ret;
    }

    private void sendEmail(EmailData emailData, String messageType) throws EmailSendingException {
        try {
            emailProducer.sendEmail(emailData, messageType);
            LOG.info(String.format("Submitted %s email to Kafka", messageType));
        } catch (EmailSendingException err) {
            LOG.error("Error sending email", err);
            throw err;
        }
    }
}
