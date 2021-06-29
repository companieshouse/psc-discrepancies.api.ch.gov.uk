package uk.gov.ch.pscdiscrepanciesapi.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ch.pscdiscrepanciesapi.models.email.ReportConfirmationEmailData;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.ObligedEntityTypes;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancy;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscDiscrepancyReport;
import uk.gov.ch.pscdiscrepanciesapi.models.rest.PscSubmission;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.email_producer.EmailProducer;
import uk.gov.companieshouse.email_producer.EmailSendingException;
import uk.gov.companieshouse.email_producer.model.EmailData;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String REPORT_SUBMISSION_CONFIRMATION = "psc_discrepancies_submission_confirmation";
    private static final String EMAIL = "EMAIL@EMAIL.com";
    private static final String COMPANY_NUMBER = "01234567";
    private static final String REFERENCE_NUMBER = "987654321";
    private static final String COMPANY_NAME = "Company Name";
    private static final String EMAIL_SUBJECT = "Confirmation of PSC Discrepancy Report";
    private static final String DISCREPANCY_ONE_NAME = "DISC_ONE";
    private static final String DISCREPANCY_ONE_DETAILS = "DISC_ONE_DETAILS";
    @Mock
    private EmailProducer mockEmailProducer;
    @Mock
    private ApiClient mockApiClient;
    @InjectMocks
    private EmailService emailService;

    @Mock
    private PscSubmission mockSubmission;
    @Mock
    private PscDiscrepancyReport mockReport;
    @Mock
    private PscDiscrepancy mockDiscrepancyOne;
    @Mock
    private PscDiscrepancy mockDiscrepancyTwo;
    @Mock
    private ApiResponse<CompanyProfileApi> mockApiResponse;

    @Captor
    ArgumentCaptor<ReportConfirmationEmailData> emailDataCaptor;
    @Mock
    private CompanyResourceHandler mockResourceHandler;
    @Mock
    private CompanyGet mockCompanyGet;
    @Mock
    private CompanyProfileApi mockApiData;

    @BeforeEach
    public void injectIntoValueAnnotation(){
        ReflectionTestUtils.setField(emailService, "subject", "Confirmation of PSC Discrepancy Report");
    }

    @Test
    void test_sendConfirmation_sendsEmail_withCorrectValues_forAllPSCs ()
            throws ApiErrorResponseException, URIValidationException {
        when(mockDiscrepancyOne.getPscName()).thenReturn(DISCREPANCY_ONE_NAME);
        AtomicInteger counter = new AtomicInteger();
        when(mockDiscrepancyOne.getPscDiscrepancyTypes()).thenReturn(Arrays.stream(ObligedEntityTypes.values())
                .map(type -> String.valueOf(counter.incrementAndGet()))
                .collect(Collectors.toList()));
        when(mockDiscrepancyOne.getDetails()).thenReturn(DISCREPANCY_ONE_DETAILS);
        when(mockDiscrepancyTwo.getPscName()).thenReturn(DISCREPANCY_ONE_NAME);
        counter.set(0);
        when(mockDiscrepancyTwo.getPscDiscrepancyTypes()).thenReturn(Arrays.asList("4", "7"));
        when(mockDiscrepancyTwo.getDetails()).thenReturn(DISCREPANCY_ONE_DETAILS);

        List<PscDiscrepancy> pscDiscrepancyList = Lists.list(mockDiscrepancyOne, mockDiscrepancyTwo);

        when(mockReport.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockReport.getObligedEntityEmail()).thenReturn(EMAIL);
        when(mockReport.getSubmissionReference()).thenReturn(REFERENCE_NUMBER);

        when(mockSubmission.getReport()).thenReturn(mockReport);
        when(mockSubmission.getDiscrepancies()).thenReturn(pscDiscrepancyList);

        when(mockApiClient.company()).thenReturn(mockResourceHandler);
        when(mockResourceHandler.get(anyString())).thenReturn(mockCompanyGet);
        when(mockCompanyGet.execute()).thenReturn(mockApiResponse);
        when(mockApiResponse.getData()).thenReturn(mockApiData);
        when(mockApiData.getCompanyName()).thenReturn(COMPANY_NAME);

        emailService.sendConfirmation(mockSubmission);

        verify(mockEmailProducer, times(2)).sendEmail(emailDataCaptor.capture(), matches(REPORT_SUBMISSION_CONFIRMATION));
        Iterator<PscDiscrepancy> pscDiscrepancyListIter = pscDiscrepancyList.iterator();
        emailDataCaptor.getAllValues().forEach(emailData -> verifyParameters(pscDiscrepancyListIter.next(), emailData));// this could fail if executed in parallel
    }

    @Test
    void test_sendConfirmation_throwsError_ifItDoesntSend()
            throws ApiErrorResponseException, URIValidationException {
        when(mockDiscrepancyOne.getPscName()).thenReturn(DISCREPANCY_ONE_NAME);
        AtomicInteger counter = new AtomicInteger();
        when(mockDiscrepancyOne.getPscDiscrepancyTypes()).thenReturn(Arrays.stream(ObligedEntityTypes.values())
                .map(type -> String.valueOf(counter.incrementAndGet()))
                .collect(Collectors.toList()));
        when(mockDiscrepancyOne.getDetails()).thenReturn(DISCREPANCY_ONE_DETAILS);

        List<PscDiscrepancy> pscDiscrepancyList = Lists.list(mockDiscrepancyOne, mockDiscrepancyTwo);

        when(mockReport.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockReport.getObligedEntityEmail()).thenReturn(EMAIL);
        when(mockReport.getSubmissionReference()).thenReturn(REFERENCE_NUMBER);

        when(mockSubmission.getReport()).thenReturn(mockReport);
        when(mockSubmission.getDiscrepancies()).thenReturn(pscDiscrepancyList);

        when(mockApiClient.company()).thenReturn(mockResourceHandler);
        when(mockResourceHandler.get(anyString())).thenReturn(mockCompanyGet);
        when(mockCompanyGet.execute()).thenReturn(mockApiResponse);
        when(mockApiResponse.getData()).thenReturn(mockApiData);
        when(mockApiData.getCompanyName()).thenReturn(COMPANY_NAME);

        doThrow(new EmailSendingException("message", new Exception())).when(mockEmailProducer).sendEmail(any(EmailData.class), anyString());

        assertThrows(EmailSendingException.class, ()-> emailService.sendConfirmation(mockSubmission));
    }

    private void verifyParameters(PscDiscrepancy discrepancy, ReportConfirmationEmailData emailData) {
        assertEquals(COMPANY_NUMBER, emailData.getCompanyNumber());
        assertEquals(EMAIL, emailData.getTo());
        assertEquals(REFERENCE_NUMBER, emailData.getReferenceNumber());
        assertEquals(COMPANY_NAME, emailData.getCompanyName());
        assertEquals(EMAIL_SUBJECT, emailData.getSubject());
        assertArrayEquals(discrepancy.getPscDiscrepancyTypes().toArray(new String[0]), emailData.getPscTypes());
        assertEquals(discrepancy.getDetails(), emailData.getPscMoreInformation());
        assertEquals(discrepancy.getPscName(), emailData.getPscName());

    }

}