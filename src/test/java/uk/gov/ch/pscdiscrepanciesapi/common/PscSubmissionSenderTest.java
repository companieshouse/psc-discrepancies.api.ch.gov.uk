package uk.gov.ch.pscdiscrepanciesapi.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.service.ServiceException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PscSubmissionSenderTest {

    @Mock
    private CloseableHttpResponse response;
    @Mock
    private HttpEntity entity;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StatusLine statusLine;
    @Mock
    private CloseableHttpClient client;
    @Mock
    private EnvironmentReader environmentReader;

    private PscSubmissionSender submissionSender;
    @Captor
    private ArgumentCaptor<HttpPost> argCaptor;
    private static final String REQUEST_ID = "1234";
    private static final String REST_API = "http://test.ch:00000/chips";
    private static final String CHIPS_REST_INTERFACE_ENDPOINT = "CHIPS_REST_INTERFACE_ENDPOINT";
    private PscSubmission submission;

    @BeforeEach
    public void setUp() {
        when(environmentReader.getMandatoryString(CHIPS_REST_INTERFACE_ENDPOINT)).thenReturn(REST_API);
        submissionSender = new PscSubmissionSender(environmentReader);
        submission = new PscSubmission();
    }

    @Test
    public void testJsonSentSuccessfully() throws ClientProtocolException, IOException, ServiceException {
        when(objectMapper.writeValueAsString(submission)).thenReturn("\"jsonSuccessful\"");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);
        submissionSender.send(submission, client, objectMapper, REQUEST_ID);
        // assertTrue(submissionSender.send(submission, client, objectMapper, null));

        verify(client).execute(argCaptor.capture());

        HttpPost httpPost = argCaptor.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        String result = IOUtils.toString(stringEntity.getContent(), StandardCharsets.UTF_8);
        assertEquals("\"jsonSuccessful\"", result);

    }

    @Test
    public void testJsonSentUnsuccessfully() throws ClientProtocolException, IOException, ServiceException {
        when(objectMapper.writeValueAsString(submission)).thenReturn("\"jsonUnsuccessful\"");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_GATEWAY);
        submissionSender.send(submission, client, objectMapper, REQUEST_ID);

        verify(client).execute(argCaptor.capture());

        HttpPost httpPost = argCaptor.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        String result = IOUtils.toString(stringEntity.getContent(), StandardCharsets.UTF_8);
        assertEquals("\"jsonUnsuccessful\"", result);
    }

    @Test
    public void testThrowsJsonProcessingException() throws ClientProtocolException, IOException, ServiceException {
        when(objectMapper.writeValueAsString(submission)).thenThrow(new JsonProcessingException("") {
            private static final long serialVersionUID = 1L;
        });
        ServiceException se = assertThrows(ServiceException.class,
                () -> submissionSender.send(submission, client, objectMapper, REQUEST_ID));

        String exceptionMessage = se.getMessage();
        assertTrue(exceptionMessage.contains("Error serialising to JSON"));
    }

    @Test
    void whenBadJsonSuppliedThenItIsSanitisedBeforeSending()
            throws ClientProtocolException, IOException, ServiceException {
        when(objectMapper.writeValueAsString(submission)).thenReturn("unquotedString");
        when(client.execute(any(HttpPost.class))).thenReturn(response);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(HttpStatus.SC_ACCEPTED);
        submissionSender.send(submission, client, objectMapper, REQUEST_ID);

        verify(client).execute((HttpUriRequest) argCaptor.capture());

        HttpPost httpPost = (HttpPost) argCaptor.getValue();
        StringEntity stringEntity = (StringEntity) httpPost.getEntity();
        String result = IOUtils.toString(stringEntity.getContent(), StandardCharsets.UTF_8);
        assertEquals("\"unquotedString\"", result);
    }

    @Test
    public void testThrowsIOException() throws ClientProtocolException, IOException, ServiceException {
        when(objectMapper.writeValueAsString(submission)).thenReturn("");
        when(client.execute(any(HttpPost.class))).thenThrow(new IOException());

        ServiceException se = assertThrows(ServiceException.class,
                () -> submissionSender.send(submission, client, objectMapper, REQUEST_ID));

        String exceptionMessage = se.getMessage();
        assertTrue(exceptionMessage.contains("Error serialising to JSON or sending payload"));
    }
}
