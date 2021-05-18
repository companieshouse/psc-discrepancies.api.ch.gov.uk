package uk.gov.ch.pscdiscrepanciesapi.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    @Spy
    @InjectMocks
    private InterceptorConfig interceptorConfig;

    @Mock
    private CRUDAuthenticationInterceptor crudAuthenticationInterceptor;

    @Mock
    private InterceptorRegistry registry;

    @Test
    void testThatConfigInterceptorAddsTheCrudAuthenticationInterceptorAddsSucessfully(){
        when(interceptorConfig.crudAuthenticationInterceptor()).thenReturn(crudAuthenticationInterceptor);
        interceptorConfig.addInterceptors(registry);
        InOrder order = Mockito.inOrder(registry);
        order.verify(registry).addInterceptor(crudAuthenticationInterceptor);
    }

}
