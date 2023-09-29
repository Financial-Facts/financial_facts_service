package com.facts.financial_facts_service.components;

import com.facts.financial_facts_service.constants.TestConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitFilterTest implements TestConstants {

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        rateLimitFilter = new RateLimitFilter();
        ReflectionTestUtils.setField(rateLimitFilter, "capacity", 1);
        ReflectionTestUtils.setField(rateLimitFilter, "refillRate", 1);
    }

    @Nested
    @DisplayName("doFilterInternal")
    class doFilterInternalTests {

        @Test
        public void testDoFilterOnConsumptionSuccess() throws ServletException, IOException {
            HttpServletRequest request = mockRequest();
            HttpServletResponse response = mock(HttpServletResponse.class);
            FilterChain filterChain = mock(FilterChain.class);
            rateLimitFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        public void testDoFilterOnConsumptionFailure() throws ServletException, IOException {
            HttpServletRequest request = mockRequest();
            HttpServletResponse response = mockResponse();

            FilterChain filterChain = mock(FilterChain.class);
            rateLimitFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            rateLimitFilter.doFilterInternal(request, response, filterChain);
            verify(response).setStatus(429);
            verify(response).setIntHeader(eq("Time-To-Refill"), anyInt());
        }

        private HttpServletRequest mockRequest() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getRemoteAddr()).thenReturn("address");
            return request;
        }

        private HttpServletResponse mockResponse() throws IOException {
            HttpServletResponse response = mock(HttpServletResponse.class);
            PrintWriter writer = mock(PrintWriter.class);
            when(response.getWriter()).thenReturn(writer);
            return response;
        }
    }

    @Nested
    @DisplayName("shouldNotFilter")
    class shouldNotFilterTests {

        @Test
        public void testShouldNotFilterBasicAuthRoutes() {
            Map<String, String> basicAuthRoutesMap = new HashMap<>();
            basicAuthRoutesMap.put("/v1/discount/bulkSimpleDiscounts", "GET");
            basicAuthRoutesMap.put("/v1/discount", "PUT");
            basicAuthRoutesMap.put("/v1/discount/", "POST");
            basicAuthRoutesMap.put("/v1/discount/" + CIK, "DELETE");
            basicAuthRoutesMap.keySet().forEach(key -> {
                HttpServletRequest request = mockRequest(key, basicAuthRoutesMap.get(key));
                assert(rateLimitFilter.shouldNotFilter(request));
            });
        }

        @Test
        public void testShouldFilterPublicRoutes() {
            Map<String, String> basicAuthRoutesMap = new HashMap<>();
            basicAuthRoutesMap.put("/v1/identity/bulk", "POST");
            basicAuthRoutesMap.put("/v1/discount/" + CIK, "GET");
            basicAuthRoutesMap.put("/v1/facts/" + CIK, "GET");
            basicAuthRoutesMap.keySet().forEach(key -> {
                HttpServletRequest request = mockRequest(key, basicAuthRoutesMap.get(key));
                assert(!rateLimitFilter.shouldNotFilter(request));
            });
        }

        private HttpServletRequest mockRequest(String uri, String method) {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getRequestURI()).thenReturn(uri);
            when(request.getMethod()).thenReturn(method);
            return request;
        }
    }
}
