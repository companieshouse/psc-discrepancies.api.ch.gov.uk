package uk.gov.ch.pscdiscrepanciesapi.config;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;

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
