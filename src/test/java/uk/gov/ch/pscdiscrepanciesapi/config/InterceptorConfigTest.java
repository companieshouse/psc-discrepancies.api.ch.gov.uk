package uk.gov.ch.pscdiscrepanciesapi.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.CRUDAuthenticationInterceptor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterceptorConfigTest {

    @Spy
    @InjectMocks
    InterceptorConfig interceptorConfig;

    @Mock
    CRUDAuthenticationInterceptor crudAuthenticationInterceptor;

    @Mock
    InterceptorRegistry registry;

    @Mock
    InterceptorRegistration crudPermissionInterceptorRegistration;

    @Test
    void testThatConfigInterceptorAddsTheCrudAuthenticationInterceptorAddsSucessfully(){
        when(interceptorConfig.crudAuthenticationInterceptor()).thenReturn(crudAuthenticationInterceptor);
        when(registry.addInterceptor(crudAuthenticationInterceptor)).thenReturn(crudPermissionInterceptorRegistration);
        interceptorConfig.addInterceptors(registry);
        InOrder order = Mockito.inOrder(registry);
        order.verify(registry).addInterceptor(crudAuthenticationInterceptor);
    }

}
