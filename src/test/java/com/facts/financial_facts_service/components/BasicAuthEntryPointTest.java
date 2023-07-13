package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.constants.TestConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicAuthEntryPointTest implements TestConstants {

    @InjectMocks
    private BasicAuthEntryPoint basicAuthEntryPoint;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private AuthenticationException mockAuthException;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        basicAuthEntryPoint = new BasicAuthEntryPoint();
        basicAuthEntryPoint.afterPropertiesSet();
    }

    @Test
    void testSetHeadersAndStatus() throws IOException {
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(mockResponse.getWriter()).thenReturn(mockWriter);
        when(mockAuthException.getMessage()).thenReturn("Authentication failed");
        basicAuthEntryPoint.commence(mockRequest, mockResponse, mockAuthException);
        verify(mockResponse).addHeader("WWW-Authenticate", "Basic realm=\"financial_facts_service\"");
        verify(mockResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockWriter).println("financial_facts_service: HTTP Status 401 - Authentication failed");
    }

    @Test
    void testSetRealmName() {
        String expectedRealmName = "financial_facts_service";
        String actualRealmName = basicAuthEntryPoint.getRealmName();
        assertEquals(expectedRealmName, actualRealmName);
    }
}
