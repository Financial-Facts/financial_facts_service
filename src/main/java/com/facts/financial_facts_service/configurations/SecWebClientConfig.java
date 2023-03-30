package com.facts.financial_facts_service.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SecWebClientConfig {

    @Value(value = "${sec.cik.api.endpoint}")
    private String secEndpoint;

    @Value(value = "${sec.cik.api.user-agent}")
    private String userAgent;

    @Bean
    public WebClient secWebClient() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(this.secEndpoint)
                .defaultHeaders(consumer -> {
                   consumer.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                   consumer.add(HttpHeaders.USER_AGENT, userAgent);
                }).build();
    }

}
