package uk.gov.ch.pscdiscrepanciesapi.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ReportStatus;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.service.ServiceException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PscSubmissionSenderTest {
    private static final String REQUEST_ID = "1234";
    private static final String REST_API = "http://test.ch:00000/chips";
    private static final String CHIPS_REST_INTERFACE_ENDPOINT = "CHIPS_REST_INTERFACE_ENDPOINT";
    private static final String DISCREPANCY_DETAILS = "discrepancy";
    private static final String VALID_EMAIL = "m@m.com";
    private static final String ETAG_1 = "1";

    @Mock
    private CloseableHttpResponse response;

    @Mock
    private HttpEntity entity;

    @Mock
    private StatusLine statusLine;

    @Mock
    private CloseableHttpClient client;

    @Mock
    private EnvironmentReader environmentReader;

    private PscSubmissionSender submissionSender;

    @Captor
    private ArgumentCaptor<HttpPost> capturedPost;
    private PscSubmission submission;

    @BeforeEach
    public void setUp() {
        when(environmentReader.getMandatoryString(CHIPS_REST_INTERFACE_ENDPOINT)).thenReturn(REST_API);
        submissionSender = new PscSubmissionSender(environmentReader);
        submission = new PscSubmission();
        PscDiscrepancyReport report = createReport(VALID_EMAIL, ReportStatus.COMPLETE.toString());
        List<PscDiscrepancy> discrepancies = createDiscrepancies(DISCREPANCY_DETAILS);
        submission.setReport(report);
        submission.setDiscrepancies(discrepancies);
    }
    
    private String getCapturedPostBody() throws IOException {
        HttpPost httpPost = capturedPost.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        return IOUtils.toString(stringEntity.getContent(), StandardCharsets.UTF_8);
    }
    
    private String toJson(PscSubmission pscSubmission) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(pscSubmission);
    }
    @Test
    public void testJsonSentSuccessfully() throws ClientProtocolException, IOException, ServiceException {
        
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);
        assertTrue(submissionSender.send(submission, client, REQUEST_ID));
        
        verify(client).execute(capturedPost.capture());
        String capturedPostBody = getCapturedPostBody();
        String expectedPostBody = toJson(submission);
        assertEquals(expectedPostBody, capturedPostBody);
    }

    @Test
    public void testJsonSentUnsuccessfully() throws ClientProtocolException, IOException, ServiceException {
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
        assertFalse(submissionSender.send(submission, client, REQUEST_ID));

        verify(client).execute(capturedPost.capture());

        String capturedPostBody = getCapturedPostBody();
        String expectedPostBody = toJson(submission);
        assertEquals(expectedPostBody, capturedPostBody);
    }

    @Test
    public void testThrowsIOException() throws ClientProtocolException, IOException, ServiceException {
        when(client.execute(any(HttpPost.class))).thenThrow(new IOException());

        ServiceException se = assertThrows(ServiceException.class,
                () -> submissionSender.send(submission, client, REQUEST_ID));

        String exceptionMessage = se.getMessage();
        assertTrue(exceptionMessage.contains("Error serialising to JSON or sending payload"));
    }
    
    private List<PscDiscrepancy> createDiscrepancies(String... discrepancies) {
        List<PscDiscrepancy> pscDiscrepancies = new ArrayList<>();
        for (String discrepancy : discrepancies) {
            PscDiscrepancy pscDiscrepancy = new PscDiscrepancy();
            pscDiscrepancy.setDetails(discrepancy);
            pscDiscrepancies.add(pscDiscrepancy);
        }
        return pscDiscrepancies;
    }

    private PscDiscrepancyReport createReport(String obligedEntityEmail, String status) {
        PscDiscrepancyReport report = new PscDiscrepancyReport();
        report.setObligedEntityEmail(obligedEntityEmail);
        report.setStatus(status);
        report.setEtag(ETAG_1);
        return report;
    }
    
    
}
