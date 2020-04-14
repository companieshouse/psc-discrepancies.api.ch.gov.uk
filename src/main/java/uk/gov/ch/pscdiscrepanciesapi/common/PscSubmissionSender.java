package uk.gov.ch.pscdiscrepanciesapi.common;

import java.io.IOException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import uk.gov.ch.pscdiscrepanciesapi.PscDiscrepancyApiApplication;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.service.ServiceException;

/**
 * Listens for the creation of a PscDiscrepancySurvey, converts it to JSON, and sends by HTTP POST
 * it to the supplied URL.
 *
 */
@Service
public class PscSubmissionSender {

    private static final String CHIPS_REST_INTERFACE_ENDPOINT = "CHIPS_REST_INTERFACE_ENDPOINT";
    private static final Logger LOG = LoggerFactory.getLogger(PscDiscrepancyApiApplication.APP_NAMESPACE);

    private final String postUrl;
    
    public PscSubmissionSender() {
        this(new EnvironmentReaderImpl());
    }
    
    public PscSubmissionSender(EnvironmentReader environmentReader) {
        this.postUrl = environmentReader.getMandatoryString(CHIPS_REST_INTERFACE_ENDPOINT);
    }

    public boolean send(PscSubmission submission,CloseableHttpClient client,ObjectMapper objectMapper,String requestId) throws ServiceException {
        try {
            submission.setRequestId(requestId);
            String discrepancyJson = objectMapper.writeValueAsString(submission);
            String sanitisedJson = JsonSanitizer.sanitize(discrepancyJson);
            StringEntity entity = new StringEntity(sanitisedJson);
            LOG.info("About to submit report: " + submission);
            HttpPost httpPost = new HttpPost(postUrl);
            httpPost.setEntity(entity);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                    LOG.info("Successfully sent report");
                    return true;
                } else {
                    LOG.error("Failed to send report: " + response);
                    return false;
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error serialising to JSON: ", e);
            throw new ServiceException("Error serialising to JSON", e);
        } catch (IOException e) {
            LOG.error("Error serialising to JSON or sending payload: ", e);
            throw new ServiceException("Error serialising to JSON or sending payload", e);
        }
    }
}
