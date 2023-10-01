package com.facts.financial_facts_service.components;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${security.rate-limit.bucket.capacity}")
    private int capacity;

    @Value("${security.rate-limit.bucket.refill-rate}")
    private int refillRate;

    private final ConcurrentHashMap<String, Bucket> bucketMap = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String remoteAddress = request.getRemoteAddr();
        ConsumptionProbe probe = consume(remoteAddress);

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            PrintWriter writer = response.getWriter();
            writer.println("financial_facts_service: HTTP Status 429 - Too many requests made in too short a period");
            response.setStatus(429);
            response.setIntHeader("Time-To-Refill", (int) (probe.getNanosToWaitForRefill() / 1000000000));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        boolean isPublicRoute =
            (uri.startsWith("/v1/identity/bulk") && method.equals("POST")) ||
            (compileCikPattern("/v1/discount").matcher(uri).find() && method.equals("GET")) ||
            (compileCikPattern("/v1/facts").matcher(uri).find() && method.equals("GET"));
        return !isPublicRoute;
    }

    private ConsumptionProbe consume(String remoteAddress) {
        if (!bucketMap.containsKey(remoteAddress)) {
            initBucket(remoteAddress);
        }
        return bucketMap.get(remoteAddress).tryConsumeAndReturnRemaining(1);
    }

    private void initBucket(String remoteAddress) {
        Refill refill = Refill.intervally(capacity, Duration.ofMinutes(refillRate));
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        Bucket bucket = Bucket.builder()
                .addLimit(limit)
                .build();
        bucketMap.put(remoteAddress, bucket);
    }

    private Pattern compileCikPattern(String prefix) {
        return Pattern.compile(prefix + "/[Cc][Ii][Kk]\\d{10}");
    }
}
