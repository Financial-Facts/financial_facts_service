package com.facts.financial_facts_service.configurations;

import com.facts.financial_facts_service.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class WebClientConfig implements Constants {

    static Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${facts-gateway.baseUrl}")
    private String factsGatewayUrl;

    @Value("${facts-gateway.bucket-name}")
    private String bucketName;

    @Value("${sec.cik.api.endpoint}")
    private String secEndpoint;

    @Value("${sec.cik.api.user-agent}")
    private String userAgent;

    @Bean
    public WebClient gatewayWebClient() {
        String getFactsFromGatewayUrl = factsGatewayUrl + SLASH + bucketName;
        return buildWebClient(getFactsFromGatewayUrl, null);
    }

    @Bean
    @Order(-1)
    public WebClient secWebClient() {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.USER_AGENT, userAgent);
        return buildWebClient(secEndpoint, headers);
    }

    private WebClient buildWebClient(String url, Map<String, String> headers) {
        logger.info("Initializing webclient for url {}...", url);
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();
        return WebClient.builder()
            .exchangeStrategies(strategies)
            .baseUrl(url)
            .defaultHeaders(consumer -> {
                consumer.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                if (Objects.nonNull(headers)) {
                    headers.keySet().forEach(key -> {
                        consumer.add(key, headers.get(key));
                    });
                }
            }).build();
    }
}
